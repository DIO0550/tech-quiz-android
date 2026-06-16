package com.dio0550.tech_quiz.ui

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dio0550.tech_quiz.R

/** 選択肢行（item_option）の見た目の状態。 */
enum class OptionMode { DEFAULT, SELECTED, CORRECT, WRONG }

/** item_option を状態に応じて着色する。 */
fun View.applyOptionMode(mode: OptionMode) {
    val ctx = context
    val key = findViewById<TextView>(R.id.option_key)
    val label = findViewById<TextView>(R.id.option_label)
    val tail = findViewById<ImageView>(R.id.option_tail)

    fun color(id: Int) = ContextCompat.getColor(ctx, id)

    when (mode) {
        OptionMode.DEFAULT -> {
            setBackgroundResource(R.drawable.bg_option)
            key.setBackgroundResource(R.drawable.bg_key_default)
            key.setTextColor(color(R.color.on_surface_var))
            label.setTextColor(color(R.color.on_surface))
            tail.visibility = View.GONE
        }
        OptionMode.SELECTED -> {
            setBackgroundResource(R.drawable.bg_option_selected)
            key.setBackgroundResource(R.drawable.bg_key_primary)
            key.setTextColor(color(R.color.on_primary))
            label.setTextColor(color(R.color.on_primary_container))
            tail.visibility = View.GONE
        }
        OptionMode.CORRECT -> {
            setBackgroundResource(R.drawable.bg_option_correct)
            key.setBackgroundResource(R.drawable.bg_key_success)
            key.setTextColor(color(R.color.white))
            label.setTextColor(color(R.color.on_success_container))
            tail.setImageResource(R.drawable.ic_check_circle)
            tail.setColorFilter(color(R.color.success))
            tail.visibility = View.VISIBLE
        }
        OptionMode.WRONG -> {
            setBackgroundResource(R.drawable.bg_option_wrong)
            key.setBackgroundResource(R.drawable.bg_key_error)
            key.setTextColor(color(R.color.white))
            label.setTextColor(color(R.color.on_error_container))
            tail.setImageResource(R.drawable.ic_cancel)
            tail.setColorFilter(color(R.color.error))
            tail.visibility = View.VISIBLE
        }
    }
}

/** 解説本文の {kw}…{/kw} を primary 太字でハイライトした文字列を作る。 */
fun buildKwText(context: Context, raw: String): CharSequence {
    val kwColor = ContextCompat.getColor(context, R.color.primary)
    val sb = SpannableStringBuilder()
    var rest = raw
    val regex = Regex("""\{kw}(.*?)\{/kw}""")
    while (true) {
        val m = regex.find(rest) ?: break
        sb.append(rest.substring(0, m.range.first))
        val word = m.groupValues[1]
        val start = sb.length
        sb.append(word)
        sb.setSpan(ForegroundColorSpan(kwColor), start, sb.length, 0)
        sb.setSpan(StyleSpan(Typeface.BOLD), start, sb.length, 0)
        rest = rest.substring(m.range.last + 1)
    }
    sb.append(rest)
    return sb
}
