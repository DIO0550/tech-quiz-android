package com.dio0550.tech_quiz.ui

import androidx.lifecycle.ViewModel
import com.dio0550.tech_quiz.data.Question
import com.dio0550.tech_quiz.domain.QuizEngine
import com.dio0550.tech_quiz.domain.QuizItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 画面の状態。 */
data class QuizUiState(
    val items: List<QuizItem> = emptyList(),
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,   // 未回答なら null
    val correctCount: Int = 0,
    val finished: Boolean = false,
) {
    val current: QuizItem? get() = items.getOrNull(currentIndex)
    val total: Int get() = items.size
    val isAnswered: Boolean get() = selectedIndex != null
}

/**
 * 出題のフロー制御。出題の生成自体は QuizEngine（純粋ロジック）に委譲し、
 * ViewModel は「いま何問目か」「回答したか」などの画面状態だけを持つ。
 */
class QuizViewModel(questions: List<Question>) : ViewModel() {

    private val engine = QuizEngine(questions)

    private val _state = MutableStateFlow(QuizUiState())
    val state: StateFlow<QuizUiState> = _state.asStateFlow()

    fun startQuiz(count: Int = 10, category: String? = null) {
        val items = engine.buildQuiz(count = count, category = category)
        _state.value = QuizUiState(items = items)
    }

    /** 選択肢を選ぶ（1問につき1回のみ反映）。 */
    fun select(index: Int) {
        val s = _state.value
        if (s.isAnswered || s.current == null) return
        val correct = s.current!!.isCorrect(index)
        _state.value = s.copy(
            selectedIndex = index,
            correctCount = s.correctCount + if (correct) 1 else 0,
        )
    }

    /** 次の問題へ。最後なら結果へ。 */
    fun next() {
        val s = _state.value
        if (!s.isAnswered) return
        val nextIndex = s.currentIndex + 1
        if (nextIndex >= s.items.size) {
            _state.value = s.copy(finished = true)
        } else {
            _state.value = s.copy(currentIndex = nextIndex, selectedIndex = null)
        }
    }
}
