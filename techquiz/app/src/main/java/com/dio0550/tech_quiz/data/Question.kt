package com.dio0550.tech_quiz.data

import androidx.annotation.DrawableRes

/** 4択の選択肢。key は「ア・イ・ウ・エ」のラベル。 */
data class Option(val key: String, val label: String)

/** 解説の補足ポイント（用語 → 説明）。 */
data class Point(val term: String, val desc: String)

/** 1問のデータ。explanationBody 内の強調語は {kw}…{/kw} で囲む。 */
data class Question(
    val id: String,
    val categoryId: String,
    val category: String,
    @DrawableRes val categoryIcon: Int,
    val text: String,
    val options: List<Option>,
    val correct: Int,
    val explanationBody: String,
    val points: List<Point>,
)

/** ホームのカテゴリ行。進捗・正答率は ProgressStore から実データを表示する。 */
data class Category(
    val id: String,
    val name: String,
    @DrawableRes val icon: Int,
)

/** 結果サマリーの分野別正答率。 */
data class Breakdown(val name: String, val pct: Int)
