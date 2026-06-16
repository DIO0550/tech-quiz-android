package com.dio0550.tech_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.data.QuizData
import com.dio0550.tech_quiz.databinding.FragmentHomeBinding
import com.dio0550.tech_quiz.databinding.ItemCategoryBinding
import com.dio0550.tech_quiz.ui.QuizViewModel

/** ホーム（学習 / カテゴリ選択）。 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateCategories()
        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.buttonStart.setOnClickListener { startQuiz() }
    }

    private fun populateCategories() {
        val cats = QuizData.HOME_CATEGORIES
        binding.listCategories.removeAllViews()
        cats.forEachIndexed { i, c ->
            val item = ItemCategoryBinding.inflate(layoutInflater, binding.listCategories, false)
            item.catIcon.setImageResource(c.icon)
            item.catName.text = c.name
            item.catBar.progress = c.progress
            item.catPct.text = getString(R.string.percent_format, c.acc)
            if (i == cats.lastIndex) item.catDivider.visibility = View.GONE
            item.root.setOnClickListener { startQuiz() }
            binding.listCategories.addView(item.root)
        }
    }

    private fun startQuiz() {
        viewModel.start()
        findNavController().navigate(R.id.action_home_to_quiz)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
