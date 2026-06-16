package com.dio0550.tech_quiz.ui

import androidx.lifecycle.ViewModel
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

    /** 各設問でユーザーが選んだ選択肢（未回答は null）。 */
    private val answers = mutableListOf<Int?>()

    data class State(
        val index: Int = 0,
        val selected: Int? = null,   // 現在の問題で仮選択中の選択肢
        val total: Int = 0,
        val finished: Boolean = false,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    val current: Question? get() = questions.getOrNull(_state.value.index)

    fun start(count: Int = 10) {
        questions = QuizData.NETWORK_QUESTIONS.take(count)
        answers.clear()
        repeat(questions.size) { answers.add(null) }
        _state.value = State(index = 0, selected = null, total = questions.size)
    }

    /** 出題画面で選択肢を仮選択する。 */
    fun pick(index: Int) {
        _state.update { it.copy(selected = index) }
    }

    /** 「回答する」確定。仮選択を回答として記録する。 */
    fun confirm() {
        val s = _state.value
        if (s.selected != null) answers[s.index] = s.selected
    }

    /** 指定した問題の回答（確定済み）。 */
    fun selectedFor(index: Int): Int? = answers.getOrNull(index)

    /** 次の問題へ。最後なら finished=true。 */
    fun next() {
        val s = _state.value
        val nextIndex = s.index + 1
        if (nextIndex >= questions.size) {
            _state.update { it.copy(finished = true) }
        } else {
            _state.update { it.copy(index = nextIndex, selected = null) }
        }
    }

    val correctCount: Int
        get() = questions.indices.count { answers[it] == questions[it].correct }
}
