package com.dio0550.tech_quiz.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

/**
 * 学習進捗の永続化。カテゴリごとに「回答済みの問題ID」と
 * 「最後の回答が正解だった問題ID」を SharedPreferences に保持する。
 *
 * - 進捗率 = 回答済み問題数 / カテゴリの総問題数
 * - 正答率 = 正解している問題数 / 回答済み問題数
 */
object ProgressStore {

    private const val PREFS_NAME = "quiz_progress"
    private const val KEY_TODAY_DATE = "today_date"
    private const val KEY_TODAY_COUNT = "today_count"

    /** 「今日の学習」の目標問題数。 */
    const val DAILY_GOAL = 30

    data class CategoryStats(val progress: Int, val accuracy: Int)

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun answeredKey(categoryId: String) = "answered_$categoryId"
    private fun correctKey(categoryId: String) = "correct_$categoryId"

    /** 1問の回答結果を記録する。同じ問題は最後の回答結果で上書きされる。 */
    fun record(context: Context, categoryId: String, questionId: String, isCorrect: Boolean) {
        val p = prefs(context)
        val answered = p.getStringSet(answeredKey(categoryId), emptySet())!!.toMutableSet()
        val correct = p.getStringSet(correctKey(categoryId), emptySet())!!.toMutableSet()
        answered.add(questionId)
        if (isCorrect) correct.add(questionId) else correct.remove(questionId)

        val today = LocalDate.now().toString()
        val count = if (p.getString(KEY_TODAY_DATE, null) == today) {
            p.getInt(KEY_TODAY_COUNT, 0)
        } else {
            0
        }

        p.edit()
            .putStringSet(answeredKey(categoryId), answered)
            .putStringSet(correctKey(categoryId), correct)
            .putString(KEY_TODAY_DATE, today)
            .putInt(KEY_TODAY_COUNT, count + 1)
            .apply()
    }

    /** カテゴリの進捗率・正答率(いずれも 0-100)。 */
    fun statsFor(context: Context, categoryId: String, totalInCategory: Int): CategoryStats {
        val p = prefs(context)
        val answered = p.getStringSet(answeredKey(categoryId), emptySet())!!.size
        val correct = p.getStringSet(correctKey(categoryId), emptySet())!!.size
        return CategoryStats(
            progress = if (totalInCategory > 0) answered * 100 / totalInCategory else 0,
            accuracy = if (answered > 0) correct * 100 / answered else 0,
        )
    }

    /** 全カテゴリを通した正答率(0-100)。未回答なら 0。 */
    fun overallAccuracy(context: Context, categoryIds: List<String>): Int {
        val p = prefs(context)
        var answered = 0
        var correct = 0
        categoryIds.forEach { id ->
            answered += p.getStringSet(answeredKey(id), emptySet())!!.size
            correct += p.getStringSet(correctKey(id), emptySet())!!.size
        }
        return if (answered > 0) correct * 100 / answered else 0
    }

    /** 今日回答した問題数。 */
    fun todayCount(context: Context): Int {
        val p = prefs(context)
        val today = LocalDate.now().toString()
        return if (p.getString(KEY_TODAY_DATE, null) == today) p.getInt(KEY_TODAY_COUNT, 0) else 0
    }
}
