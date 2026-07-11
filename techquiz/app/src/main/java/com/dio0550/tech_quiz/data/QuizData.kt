package com.dio0550.tech_quiz.data

import android.content.Context
import com.dio0550.tech_quiz.R
import org.json.JSONObject

/**
 * ホームのカテゴリ一覧と、assets 配下の設問JSONを Question に変換するローダー。
 */
object QuizData {

    /** ホーム画面に並べるカテゴリ（応用情報の分野体系に準拠した順）。 */
    val HOME_CATEGORIES = listOf(
        // テクノロジ系
        Category("base", "基礎理論", R.drawable.ic_cat_functions),
        Category("algo", "アルゴリズムとプログラミング", R.drawable.ic_cat_code),
        Category("cs", "コンピュータシステム", R.drawable.ic_cat_memory),
        Category("db", "データベース", R.drawable.ic_cat_database),
        Category("net", "ネットワーク", R.drawable.ic_cat_lan),
        Category("sec", "セキュリティ", R.drawable.ic_cat_shield),
        Category("dev", "システム開発技術", R.drawable.ic_cat_build),
        // マネジメント系
        Category("pm", "プロジェクトマネジメント", R.drawable.ic_cat_event_note),
        Category("sm", "サービスマネジメント", R.drawable.ic_cat_autorenew),
        Category("audit", "システム監査", R.drawable.ic_cat_fact_check),
        // ストラテジ系
        Category("st", "経営戦略・システム戦略", R.drawable.ic_cat_trending_up),
        Category("legal", "企業と法務", R.drawable.ic_cat_gavel),
    )

    fun categoryName(categoryId: String): String =
        HOME_CATEGORIES.firstOrNull { it.id == categoryId }?.name ?: categoryId

    private val QUESTION_FILES = listOf(
        "questions/network.json",
        "questions/security.json",
        "questions/database.json",
        "questions/base.json",
        "questions/algo.json",
        "questions/pm.json",
        "questions/computer.json",
        "questions/dev.json",
        "questions/service.json",
        "questions/audit.json",
        "questions/strategy.json",
        "questions/legal.json",
    )

    @Volatile
    private var cachedQuestions: List<Question>? = null

    fun loadQuestions(context: Context): List<Question> {
        cachedQuestions?.let { return it }
        val loaded = QUESTION_FILES.flatMap { path ->
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            JSONObject(json).getJSONArray("questions").toQuestions()
        }
        cachedQuestions = loaded
        return loaded
    }

    /** カテゴリ別の総問題数。 */
    fun questionCountByCategory(context: Context): Map<String, Int> =
        loadQuestions(context).groupingBy { it.categoryId }.eachCount()

    private fun org.json.JSONArray.toQuestions(): List<Question> {
        val questions = this
        return List(questions.length()) { index ->
            val item = questions.getJSONObject(index)
            Question(
                id = item.getString("id"),
                categoryId = item.getString("categoryId"),
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

    private fun iconFor(categoryId: String): Int =
        HOME_CATEGORIES.firstOrNull { it.id == categoryId }?.icon ?: R.drawable.ic_cat_functions

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
