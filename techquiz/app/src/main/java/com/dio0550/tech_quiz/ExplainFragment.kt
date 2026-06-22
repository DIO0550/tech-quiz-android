package com.dio0550.tech_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.databinding.FragmentExplainBinding
import com.dio0550.tech_quiz.databinding.ItemOptionBinding
import com.dio0550.tech_quiz.databinding.ItemPointBinding
import com.dio0550.tech_quiz.ui.OptionMode
import com.dio0550.tech_quiz.ui.QuizViewModel
import com.dio0550.tech_quiz.ui.applyOptionMode
import com.dio0550.tech_quiz.ui.buildKwText

/** 正誤・解説。 */
class ExplainFragment : Fragment() {

    private var _binding: FragmentExplainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val q = viewModel.current ?: run {
            findNavController().popBackStack(R.id.homeFragment, false)
            return
        }
        val s = viewModel.state.value
        val picked = viewModel.selectedFor(s.index)
        val isCorrect = picked == q.correct

        binding.textProgress.text = getString(R.string.progress_format, s.index + 1, s.total)

        // verdict header
        if (isCorrect) {
            binding.verdictIcon.setImageResource(R.drawable.ic_check_circle)
            binding.verdictIcon.setColorFilter(color(R.color.success))
            binding.verdictText.setText(R.string.verdict_correct)
            binding.verdictText.setTextColor(color(R.color.success))
            binding.verdictMeta.text = getString(R.string.verdict_meta_correct)
        } else {
            binding.verdictIcon.setImageResource(R.drawable.ic_cancel)
            binding.verdictIcon.setColorFilter(color(R.color.error))
            binding.verdictText.setText(R.string.verdict_wrong)
            binding.verdictText.setTextColor(color(R.color.error))
            binding.verdictMeta.text = getString(R.string.verdict_meta_wrong)
        }

        binding.textQuestion.text = q.text

        // options recap
        binding.layoutOptions.removeAllViews()
        q.options.forEachIndexed { i, o ->
            val item = ItemOptionBinding.inflate(layoutInflater, binding.layoutOptions, false)
            item.optionKey.text = o.key
            item.optionLabel.text = o.label
            val mode = when {
                i == q.correct -> OptionMode.CORRECT
                i == picked -> OptionMode.WRONG
                else -> OptionMode.DEFAULT
            }
            item.root.applyOptionMode(mode)
            item.root.isClickable = false
            binding.layoutOptions.addView(item.root)
        }

        // explanation
        binding.textExplanation.text = buildKwText(requireContext(), q.explanationBody)
        binding.layoutPoints.removeAllViews()
        q.points.forEach { p ->
            val item = ItemPointBinding.inflate(layoutInflater, binding.layoutPoints, false)
            item.pointTerm.text = p.term
            item.pointDesc.text = p.desc
            binding.layoutPoints.addView(item.root)
        }

        binding.buttonClose.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.buttonNext.setOnClickListener {
            viewModel.next()
            if (viewModel.state.value.finished) {
                findNavController().navigate(R.id.action_explain_to_summary)
            } else {
                findNavController().navigate(R.id.action_explain_to_quiz)
            }
        }
    }

    private fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
