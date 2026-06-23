package com.dio0550.tech_quiz

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.data.QuizData
import com.dio0550.tech_quiz.databinding.FragmentSummaryBinding
import com.dio0550.tech_quiz.databinding.ItemBreakdownBinding
import com.dio0550.tech_quiz.ui.QuizViewModel
import kotlin.math.roundToInt

/** 結果サマリー（スコア）。 */
class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val score = viewModel.correctCount
        val total = viewModel.questions.size.coerceAtLeast(1)
        val pct = (score * 100f / total).roundToInt()

        binding.textScore.text = scoreText(score, total)
        binding.textSubtitle.text = subtitleText(pct)
        binding.scoreBar.progress = pct

        binding.statScore.text = getString(R.string.score_format, score, total)
        binding.statTime.text = getString(R.string.summary_time)
        binding.statAvg.text = getString(R.string.summary_avg)

        binding.layoutBreakdown.removeAllViews()
        QuizData.SUMMARY_BREAKDOWN.forEach { b ->
            val item = ItemBreakdownBinding.inflate(layoutInflater, binding.layoutBreakdown, false)
            item.brName.text = b.name
            item.brBar.progress = b.pct
            item.brBar.progressTintList = ColorStateList.valueOf(color(barColor(b.pct)))
            item.brPct.text = getString(R.string.percent_format, b.pct)
            binding.layoutBreakdown.addView(item.root)
        }

        binding.buttonHome.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.buttonRetry.setOnClickListener {
            viewModel.start(requireContext())
            findNavController().navigate(R.id.action_summary_to_quiz)
        }
    }

    /** 「8 / 10」— 分母を小さく表示。 */
    private fun scoreText(score: Int, total: Int): CharSequence {
        val tail = " / $total"
        val s = SpannableString("$score$tail")
        val start = s.length - tail.length
        s.setSpan(RelativeSizeSpan(0.46f), start, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(ForegroundColorSpan(color(R.color.on_surface_var)), start, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    /** 「正答率 80% ・ ネットワーク」— 正答率を primary 太字に。 */
    private fun subtitleText(pct: Int): CharSequence {
        val prefix = "正答率 "
        val pctStr = "$pct%"
        val s = SpannableString("$prefix$pctStr ・ ネットワーク")
        s.setSpan(ForegroundColorSpan(color(R.color.primary)), prefix.length, prefix.length + pctStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(StyleSpan(android.graphics.Typeface.BOLD), prefix.length, prefix.length + pctStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    private fun barColor(pct: Int) = when {
        pct >= 80 -> R.color.success
        pct >= 60 -> R.color.primary
        else -> R.color.tertiary
    }

    private fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
