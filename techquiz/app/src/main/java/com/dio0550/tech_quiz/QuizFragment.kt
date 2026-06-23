package com.dio0550.tech_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.databinding.FragmentQuizBinding
import com.dio0550.tech_quiz.databinding.ItemOptionBinding
import com.dio0550.tech_quiz.ui.OptionMode
import com.dio0550.tech_quiz.ui.QuizViewModel
import com.dio0550.tech_quiz.ui.applyOptionMode

/** 出題（4択）。 */
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()
    private var isNavigatingToExplain = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.questions.isEmpty()) viewModel.start(requireContext())

        binding.buttonClose.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.footerAnswer.visibility = View.GONE
        render()
    }

    private fun render() {
        isNavigatingToExplain = false
        val q = viewModel.current ?: return
        val s = viewModel.state.value

        binding.textProgress.text = getString(R.string.progress_format, s.index + 1, s.total)
        binding.quizProgress.progress = (s.index + 1) * 100 / s.total

        binding.textCategory.text = q.category
        TextViewCompat.setCompoundDrawableTintList(
            binding.textCategory,
            ContextCompat.getColorStateList(requireContext(), R.color.primary)
        )
        binding.textCategory.setCompoundDrawablesRelativeWithIntrinsicBounds(q.categoryIcon, 0, 0, 0)

        binding.textQuestion.text = q.text

        binding.layoutOptions.removeAllViews()
        q.options.forEachIndexed { i, o ->
            val item = ItemOptionBinding.inflate(layoutInflater, binding.layoutOptions, false)
            item.optionKey.text = o.key
            item.optionLabel.text = o.label
            item.root.applyOptionMode(if (i == s.selected) OptionMode.SELECTED else OptionMode.DEFAULT)
            item.root.setOnClickListener {
                answer(i)
            }
            binding.layoutOptions.addView(item.root)
        }
    }

    private fun answer(index: Int) {
        if (isNavigatingToExplain) return
        isNavigatingToExplain = true

        viewModel.pick(index)
        viewModel.confirm()
        for (i in 0 until binding.layoutOptions.childCount) {
            val option = binding.layoutOptions.getChildAt(i)
            option.isEnabled = false
            option.applyOptionMode(if (i == index) OptionMode.SELECTED else OptionMode.DEFAULT)
        }

        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.quizFragment) {
            navController.navigate(R.id.action_quiz_to_explain)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
