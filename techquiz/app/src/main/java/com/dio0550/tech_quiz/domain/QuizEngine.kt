package com.dio0550.tech_quiz.domain

import com.dio0550.tech_quiz.data.Question

class QuizEngine(private val allQuestions: List<Question>) {

    fun buildQuiz(count: Int = 10, category: String? = null): List<QuizItem> {
        val filtered = if (category != null) {
            allQuestions.filter { it.category == category }
        } else {
            allQuestions
        }

        return filtered.shuffled()
            .take(count)
            .map { question ->
                // Shuffle choices for the UI
                QuizItem(
                    question = question,
                    choices = question.choices.shuffled()
                )
            }
    }
}
