package com.dio0550.tech_quiz.ui

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.dio0550.tech_quiz.data.Breakdown
import com.dio0550.tech_quiz.data.ProgressStore
import com.dio0550.tech_quiz.data.Question
import com.dio0550.tech_quiz.data.QuizData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 出題セッションの状態。Activity スコープで保持し、
 * 出題（Quiz）・解説（Explain）・結果（Summary）画面で共有する。
 */
class QuizViewModel : ViewModel() {

    /** 現在のセッションの設問。 */
    var questions: List<Question> = emptyList()
        private set

    /** 現在のセッションの出題カテゴリ（null は全カテゴリ）。 */
    var sessionCategoryId: String? = null
        private set

    /** 各設問でユーザーが選んだ選択肢（未回答は null）。 */
    private val answers = mutableListOf<Int?>()

    private var appContext: Context? = null
    private var sessionStartedAt = 0L
    private var questionShownAt = 0L
    private var finishedElapsedMs = 0L

    /** 直前に回答した問題の所要秒数。 */
    var lastAnswerSeconds = 0
        private set

    data class State(
        val index: Int = 0,
        val selected: Int? = null,   // 現在の問題で仮選択中の選択肢
        val total: Int = 0,
        val finished: Boolean = false,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    val current: Question? get() = questions.getOrNull(_state.value.index)

    fun start(context: Context, categoryId: String? = null, count: Int = 10) {
        appContext = context.applicationContext
        sessionCategoryId = categoryId
        val all = QuizData.loadQuestions(context)
        val pool = if (categoryId == null) all else all.filter { it.categoryId == categoryId }
        questions = pool.shuffled().take(count)
        answers.clear()
        repeat(questions.size) { answers.add(null) }
        sessionStartedAt = SystemClock.elapsedRealtime()
        questionShownAt = sessionStartedAt
        finishedElapsedMs = 0L
        _state.value = State(index = 0, selected = null, total = questions.size)
    }

    /** 出題画面で選択肢を仮選択する。 */
    fun pick(index: Int) {
        _state.update { it.copy(selected = index) }
    }

    /** 「回答する」確定。仮選択を回答として記録し、学習進捗に反映する。 */
    fun confirm() {
        val s = _state.value
        val selected = s.selected ?: return
        if (answers[s.index] != null) return
        answers[s.index] = selected

        lastAnswerSeconds = ((SystemClock.elapsedRealtime() - questionShownAt) / 1000L).toInt()
        val q = questions[s.index]
        appContext?.let { ProgressStore.record(it, q.categoryId, q.id, selected == q.correct) }
    }

    /** 指定した問題の回答（確定済み）。 */
    fun selectedFor(index: Int): Int? = answers.getOrNull(index)

    /** 次の問題へ。最後なら finished=true。 */
    fun next() {
        val s = _state.value
        val nextIndex = s.index + 1
        if (nextIndex >= questions.size) {
            finishedElapsedMs = SystemClock.elapsedRealtime() - sessionStartedAt
            _state.update { it.copy(finished = true) }
        } else {
            questionShownAt = SystemClock.elapsedRealtime()
            _state.update { it.copy(index = nextIndex, selected = null) }
        }
    }

    val correctCount: Int
        get() = questions.indices.count { answers[it] == questions[it].correct }

    /** セッションの所要時間（ミリ秒）。 */
    val elapsedMs: Long
        get() = if (finishedElapsedMs > 0) {
            finishedElapsedMs
        } else {
            SystemClock.elapsedRealtime() - sessionStartedAt
        }

    /** セッションの出題カテゴリ名（全カテゴリなら「総合」）。 */
    val sessionCategoryName: String
        get() = sessionCategoryId?.let { QuizData.categoryName(it) } ?: "総合"

    /** セッションのカテゴリ別正答率。ホームのカテゴリ表示順に並べる。 */
    fun breakdown(): List<Breakdown> {
        val byCategory = questions.withIndex().groupBy { it.value.categoryId }
        val order = QuizData.HOME_CATEGORIES.map { it.id }
        return byCategory.entries
            .sortedBy { order.indexOf(it.key).let { i -> if (i < 0) order.size else i } }
            .map { (categoryId, items) ->
                val correct = items.count { answers[it.index] == it.value.correct }
                Breakdown(QuizData.categoryName(categoryId), correct * 100 / items.size)
            }
    }
}
