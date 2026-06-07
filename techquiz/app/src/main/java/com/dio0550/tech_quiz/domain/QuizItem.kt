package com.dio0550.tech_quiz.domain

import com.dio0550.tech_quiz.data.Question

data class QuizItem(
    val question: Question,
    val choices: List<String>
) {
    fun isCorrect(index: Int): Boolean {
        // Here we assume the choices in QuizItem might be shuffled, 
        // so we need to check against the original question's correct choice text.
        val correctChoice = question.choices[question.correctIndex]
        return choices.getOrNull(index) == correctChoice
    }
}
