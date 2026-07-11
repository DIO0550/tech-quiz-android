package com.dio0550.tech_quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dio0550.tech_quiz.data.ProgressStore
import com.dio0550.tech_quiz.data.QuizData
import com.dio0550.tech_quiz.databinding.FragmentHomeBinding
import com.dio0550.tech_quiz.databinding.ItemCategoryBinding
import com.dio0550.tech_quiz.ui.QuizViewModel

/** ホーム（学習 / カテゴリ選択）。 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    /** 出題フィルタの選択肢。 */
    private val difficulties = listOf("やさしい", "ふつう", "むずかしい")
    private val counts = listOf(5, 10, 20)

    /** 現在の選択。 */
    private var selectedDifficulty = "ふつう"
    private var selectedCount = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.buttonStart.setOnClickListener { startQuiz(categoryId = null) }

        updateFilterChips()
        binding.chipDifficulty.setOnClickListener { showDifficultyMenu(it as TextView) }
        binding.chipCount.setOnClickListener { showCountMenu(it as TextView) }
    }

    private fun updateFilterChips() {
        binding.chipDifficulty.text = getString(R.string.difficulty_chip, selectedDifficulty)
        binding.chipCount.text = getString(R.string.count_chip, selectedCount)
    }

    private fun showDifficultyMenu(anchor: TextView) {
        PopupMenu(requireContext(), anchor).apply {
            difficulties.forEachIndexed { i, label -> menu.add(0, i, i, label) }
            setOnMenuItemClickListener { item ->
                selectedDifficulty = difficulties[item.itemId]
                updateFilterChips()
                true
            }
        }.show()
    }

    private fun showCountMenu(anchor: TextView) {
        PopupMenu(requireContext(), anchor).apply {
            counts.forEachIndexed { i, count -> menu.add(0, i, i, getString(R.string.count_chip, count)) }
            setOnMenuItemClickListener { item ->
                selectedCount = counts[item.itemId]
                updateFilterChips()
                true
            }
        }.show()
    }

    override fun onResume() {
        super.onResume()
        populateStats()
        populateCategories()
    }

    private fun populateStats() {
        val context = requireContext()
        val acc = ProgressStore.overallAccuracy(context, QuizData.HOME_CATEGORIES.map { it.id })
        binding.statOverallAcc.text = getString(R.string.percent_format, acc)
        binding.statToday.text =
            getString(R.string.score_format, ProgressStore.todayCount(context), ProgressStore.DAILY_GOAL)
    }

    private fun populateCategories() {
        val context = requireContext()
        val counts = QuizData.questionCountByCategory(context)
        val cats = QuizData.HOME_CATEGORIES
        binding.listCategories.removeAllViews()
        cats.forEachIndexed { i, c ->
            val stats = ProgressStore.statsFor(context, c.id, counts[c.id] ?: 0)
            val item = ItemCategoryBinding.inflate(layoutInflater, binding.listCategories, false)
            item.catIcon.setImageResource(c.icon)
            item.catName.text = c.name
            item.catBar.progress = stats.progress
            item.catPct.text = getString(R.string.percent_format, stats.accuracy)
            if (i == cats.lastIndex) item.catDivider.visibility = View.GONE
            item.root.setOnClickListener { startQuiz(categoryId = c.id) }
            binding.listCategories.addView(item.root)
        }
    }

    private fun startQuiz(categoryId: String?) {
        viewModel.start(requireContext(), categoryId, count = selectedCount)
        findNavController().navigate(R.id.action_home_to_quiz)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
