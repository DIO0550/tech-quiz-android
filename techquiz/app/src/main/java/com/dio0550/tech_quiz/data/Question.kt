package com.dio0550.tech_quiz.data

data class Question(
    val id: String,
    val text: String,
    val choices: List<String>,
    val correctIndex: Int,
    val category: String? = null
)
