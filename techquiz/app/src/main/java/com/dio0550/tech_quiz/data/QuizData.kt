package com.dio0550.tech_quiz.data

import android.content.Context
import com.dio0550.tech_quiz.R
import org.json.JSONObject

/**
 * ホームのカテゴリ一覧と、assets 配下の設問JSONを Question に変換するローダー。
 */
object QuizData {

    /** ホーム画面に並べるカテゴリ（デザイン同様の6件）。 */
    val HOME_CATEGORIES = listOf(
        Category("base", "基礎理論", R.drawable.ic_cat_functions, progress = 80, acc = 78),
        Category("net", "ネットワーク", R.drawable.ic_cat_lan, progress = 88, acc = 81),
        Category("sec", "セキュリティ", R.drawable.ic_cat_shield, progress = 44, acc = 58),
        Category("db", "データベース", R.drawable.ic_cat_database, progress = 85, acc = 84),
        Category("algo", "アルゴリズムとプログラミング", R.drawable.ic_cat_code, progress = 63, acc = 71),
        Category("pm", "プロジェクトマネジメント", R.drawable.ic_cat_event_note, progress = 75, acc = 75),
    )

    /** 結果サマリーの分野別正答率（デザイン同様）。 */
    val SUMMARY_BREAKDOWN = listOf(
        Breakdown("プロトコル", 100),
        Breakdown("ルーティング", 83),
        Breakdown("TCP/UDP", 75),
        Breakdown("無線・物理層", 60),
    )

    private val QUESTION_FILES = listOf(
        "questions/network.json",
        "questions/security.json",
        "questions/database.json",
    )

    fun loadQuestions(context: Context): List<Question> {
        return QUESTION_FILES.flatMap { path ->
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            JSONObject(json).getJSONArray("questions").toQuestions()
        }
    }

    private fun org.json.JSONArray.toQuestions(): List<Question> {
        val questions = this
        return List(questions.length()) { index ->
            val item = questions.getJSONObject(index)
            Question(
                id = item.getString("id"),
                category = item.getString("category"),
                categoryIcon = iconFor(item.getString("categoryId")),
                text = item.getString("text"),
                options = item.getJSONArray("options").toOptions(),
                correct = item.getInt("correct"),
                explanationBody = item.getString("explanationBody"),
                points = item.getJSONArray("points").toPoints(),
            )
        }
    }

    private fun iconFor(categoryId: String): Int = when (categoryId) {
        "base" -> R.drawable.ic_cat_functions
        "net" -> R.drawable.ic_cat_lan
        "sec" -> R.drawable.ic_cat_shield
        "db" -> R.drawable.ic_cat_database
        "algo" -> R.drawable.ic_cat_code
        "pm" -> R.drawable.ic_cat_event_note
        else -> R.drawable.ic_cat_functions
    }

    private fun org.json.JSONArray.toOptions(): List<Option> {
        val keys = listOf("ア", "イ", "ウ", "エ")
        return List(length()) { index ->
            Option(keys[index], getString(index))
        }
    }

    private fun org.json.JSONArray.toPoints(): List<Point> = List(length()) { index ->
        val item = getJSONObject(index)
        Point(item.getString("term"), item.getString("desc"))
    }
}
