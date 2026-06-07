package com.dio0550.tech_quiz

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.data.Question
import com.dio0550.tech_quiz.databinding.FragmentQuizBinding
import com.dio0550.tech_quiz.ui.QuizViewModel
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For simplicity, providing some dummy questions
        val questions = listOf(
            Question("1", "応用情報技術者試験の午前試験は何問ですか？", listOf("40問", "60問", "80問", "100問"), 2, "General"),
            Question("2", "HTTPのポート番号は？", listOf("21", "25", "80", "443"), 2, "Network"),
            Question("3", "OSI参照モデルの第3層は？", listOf("物理層", "データリンク層", "ネットワーク層", "トランスポート層"), 2, "Network")
        )

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return QuizViewModel(questions) as T
            }
        }).get(QuizViewModel::class.java)

        setupListeners()
        observeState()

        if (viewModel.state.value.items.isEmpty()) {
            viewModel.startQuiz(count = 3)
        }
    }

    private fun setupListeners() {
        binding.buttonChoice1.setOnClickListener { viewModel.select(0) }
        binding.buttonChoice2.setOnClickListener { viewModel.select(1) }
        binding.buttonChoice3.setOnClickListener { viewModel.select(2) }
        binding.buttonChoice4.setOnClickListener { viewModel.select(3) }

        binding.buttonNext.setOnClickListener {
            if (viewModel.state.value.finished) {
                findNavController().popBackStack()
            } else {
                viewModel.next()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: com.dio0550.tech_quiz.ui.QuizUiState) {
        val current = state.current
        if (current == null) return

        binding.textProgress.text = "Question ${state.currentIndex + 1} / ${state.items.size}"
        binding.textQuestion.text = current.question.text

        val buttons = listOf(
            binding.buttonChoice1,
            binding.buttonChoice2,
            binding.buttonChoice3,
            binding.buttonChoice4
        )

        current.choices.forEachIndexed { index, choice ->
            buttons[index].text = choice
            buttons[index].isEnabled = !state.isAnswered
            
            // Reset colors
            buttons[index].setBackgroundColor(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_primary))
        }

        if (state.isAnswered) {
            val selectedIndex = state.selectedIndex!!
            val isCorrect = current.isCorrect(selectedIndex)
            
            buttons[selectedIndex].setBackgroundColor(if (isCorrect) Color.GREEN else Color.RED)
            
            // If incorrect, highlight the correct one
            if (!isCorrect) {
                val correctIdxInCurrent = current.choices.indexOf(current.question.choices[current.question.correctIndex])
                if (correctIdxInCurrent != -1) {
                    buttons[correctIdxInCurrent].setBackgroundColor(Color.GREEN)
                }
            }

            binding.textResultFeedback.visibility = View.VISIBLE
            binding.textResultFeedback.text = if (isCorrect) "正解！" else "不正解..."
            binding.textResultFeedback.setTextColor(if (isCorrect) Color.GREEN else Color.RED)

            binding.buttonNext.visibility = View.VISIBLE
            binding.buttonNext.text = if (state.currentIndex + 1 >= state.items.size) "Finish" else "Next"
        } else {
            binding.textResultFeedback.visibility = View.GONE
            binding.buttonNext.visibility = View.GONE
        }

        if (state.finished) {
            binding.textQuestion.text = "Quiz Finished!\nCorrect: ${state.correctCount} / ${state.items.size}"
            binding.layoutChoices.visibility = View.GONE
            binding.buttonNext.text = "Back to Home"
            binding.buttonNext.visibility = View.VISIBLE
            binding.textResultFeedback.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
