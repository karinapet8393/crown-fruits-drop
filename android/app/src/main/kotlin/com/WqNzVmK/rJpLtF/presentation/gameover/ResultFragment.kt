package com.WqNzVmK.rJpLtF.presentation.gameover

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentResultBinding
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.presentation.game.GameFragment
import com.WqNzVmK.rJpLtF.presentation.menu.MenuFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * End of the feast: the served dishes, the career tiles, the next recipe teaser and
 * the two calls to action (PLAY AGAIN / MENU).
 */
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: ResultViewModel by viewModels {
        ResultViewModel.factory(readResult())
    }

    private var glowAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resultBg.loadAsset(AppAssets.BG_GAME)
        binding.resultCtaPlate.loadAsset(AppAssets.BUTTON_CTA)

        binding.resultPlayAgain.setOnClickListener {
            Navigator.push(parentFragmentManager, GameFragment())
        }
        binding.resultMenu.setOnClickListener {
            Navigator.back(parentFragmentManager, MenuFragment())
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { render(it) }
            }
        }
    }

    private fun render(state: ResultUiState) {
        val safeBinding = _binding ?: return

        safeBinding.resultTitle.setText(if (state.won) R.string.result_win else R.string.result_lose)
        safeBinding.resultTitle.setTextColor(
            requireContext().getColor(if (state.won) R.color.green else R.color.red),
        )

        renderDishes(state)
        renderStats(state)

        val nextResId = state.nextRecipeNameResId
        safeBinding.resultNext.text = if (nextResId == null) {
            getString(R.string.result_next_none)
        } else {
            getString(R.string.result_next, getString(nextResId))
        }

        if (state.won) playGlow()
    }

    private fun renderDishes(state: ResultUiState) {
        val safeBinding = _binding ?: return
        val slots: List<ImageView> = listOf(
            safeBinding.resultDishOne,
            safeBinding.resultDishTwo,
            safeBinding.resultDishThree,
            safeBinding.resultDishFour,
        )
        slots.forEachIndexed { index, slot ->
            val served = index < state.servedRecipes
            slot.loadAsset(AppAssets.SPRITE_DISH_TART)
            slot.alpha = if (served) 0f else PENDING_ALPHA
            slot.contentDescription = getString(
                if (served) R.string.desc_dish_served else R.string.desc_dish_pending,
            )
            if (!served) return@forEachIndexed
            slot.scaleX = DISH_FROM_SCALE
            slot.scaleY = DISH_FROM_SCALE
            slot.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(index * DISH_STAGGER_MS)
                .setDuration(DISH_MS)
                .start()
        }
    }

    private fun renderStats(state: ResultUiState) {
        val safeBinding = _binding ?: return
        if (!state.showStats) {
            safeBinding.resultStatsRow.visibility = View.GONE
            return
        }
        safeBinding.resultStatsRow.visibility = View.VISIBLE
        safeBinding.resultStatRecipes.bind(
            getString(R.string.value_fraction, state.servedRecipes, state.totalRecipes),
        )
        safeBinding.resultStatAccuracy.bind(getString(R.string.value_percent, state.accuracy))
        safeBinding.resultStatBest.bind(getString(R.string.value_plain, state.best))
    }

    private fun playGlow() {
        if (glowAnimator != null) return
        val target = _binding?.resultGlow ?: return
        glowAnimator = ValueAnimator.ofFloat(0f, GLOW_ALPHA).apply {
            duration = GLOW_MS
            addUpdateListener { animation ->
                val value = animation.animatedValue
                if (value is Float) {
                    _binding?.resultGlow?.alpha = value
                }
            }
            start()
        }
        target.alpha = 0f
    }

    override fun onDestroyView() {
        glowAnimator?.cancel()
        glowAnimator = null
        _binding = null
        super.onDestroyView()
    }

    private fun readResult(): RoundResult {
        val args = arguments ?: return EMPTY_RESULT
        return RoundResult(
            servedRecipes = args.getInt(KEY_SERVED, 0),
            totalRecipes = args.getInt(KEY_TOTAL, DEFAULT_TOTAL),
            correctCatches = args.getInt(KEY_CORRECT, 0),
            totalCatches = args.getInt(KEY_CATCHES, 0),
            mistakes = args.getInt(KEY_MISTAKES, 0),
            won = args.getBoolean(KEY_WON, false),
        )
    }

    companion object {
        private const val KEY_SERVED = "served"
        private const val KEY_TOTAL = "total"
        private const val KEY_CORRECT = "correct"
        private const val KEY_CATCHES = "catches"
        private const val KEY_MISTAKES = "mistakes"
        private const val KEY_WON = "won"

        private const val DEFAULT_TOTAL = 4
        private const val PENDING_ALPHA = 0.25f
        private const val DISH_FROM_SCALE = 0.9f
        private const val DISH_STAGGER_MS = 90L
        private const val DISH_MS = 260L
        private const val GLOW_ALPHA = 0.9f
        private const val GLOW_MS = 320L

        private val EMPTY_RESULT = RoundResult(
            servedRecipes = 0,
            totalRecipes = DEFAULT_TOTAL,
            correctCatches = 0,
            totalCatches = 0,
            mistakes = 0,
            won = false,
        )

        fun newInstance(result: RoundResult): ResultFragment = ResultFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_SERVED, result.servedRecipes)
                putInt(KEY_TOTAL, result.totalRecipes)
                putInt(KEY_CORRECT, result.correctCatches)
                putInt(KEY_CATCHES, result.totalCatches)
                putInt(KEY_MISTAKES, result.mistakes)
                putBoolean(KEY_WON, result.won)
            }
        }
    }
}
