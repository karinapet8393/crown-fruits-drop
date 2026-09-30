package com.WqNzVmK.rJpLtF.presentation.menu

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentMenuBinding
import com.WqNzVmK.rJpLtF.presentation.game.GameFragment
import com.WqNzVmK.rJpLtF.presentation.recipes.RecipesFragment
import com.WqNzVmK.rJpLtF.presentation.tutorial.TutorialFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Bottom-sheet menu: palace kitchen art on top, a marble sheet with the title,
 * the hint, the career tiles and the calls to action at the bottom.
 */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: MenuViewModel by viewModels { MenuViewModel.Factory }

    private var basketBreath: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuBg.loadAsset(AppAssets.BG_MENU)
        binding.menuBasket.loadAsset(AppAssets.SPRITE_BASKET_GOLD)
        binding.menuCtaPlate.loadAsset(AppAssets.BUTTON_CTA)

        binding.menuPlay.setOnClickListener {
            Navigator.push(parentFragmentManager, GameFragment())
        }
        binding.menuRecipes.setOnClickListener {
            Navigator.push(parentFragmentManager, RecipesFragment())
        }
        binding.menuHowTo.setOnClickListener {
            Navigator.push(parentFragmentManager, TutorialFragment())
        }

        playEntrance()
        observeState()
    }

    private fun playEntrance() {
        val sheet = binding.menuSheet
        sheet.translationY = resources.displayMetrics.density * SHEET_RISE_DP
        sheet.animate()
            .translationY(0f)
            .setDuration(SHEET_RISE_MS)
            .start()

        val cta = binding.menuPlay
        cta.scaleX = CTA_FROM_SCALE
        cta.scaleY = CTA_FROM_SCALE
        cta.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(OvershootInterpolator(CTA_TENSION))
            .setDuration(CTA_MS)
            .start()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { render(it) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val safeBinding = _binding ?: return
        if (!state.showStats) {
            safeBinding.menuStatsRow.visibility = View.GONE
            return
        }
        safeBinding.menuStatsRow.visibility = View.VISIBLE
        safeBinding.menuStatBest.bind(
            getString(R.string.value_fraction, state.progress.bestRecipes, 4),
        )
        safeBinding.menuStatAccuracy.bind(
            getString(R.string.value_percent, state.progress.bestAccuracy),
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        startBreathing()
    }

    override fun onPause() {
        basketBreath?.cancel()
        basketBreath = null
        _binding?.menuBasket?.translationY = 0f
        super.onPause()
    }

    private fun startBreathing() {
        val target = _binding?.menuBasket ?: return
        val amplitude = resources.displayMetrics.density * BREATH_DP
        basketBreath = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, -amplitude, amplitude).apply {
            duration = BREATH_MS
            repeatMode = ValueAnimator.REVERSE
            repeatCount = BREATH_CYCLES
            start()
        }
    }

    override fun onDestroyView() {
        basketBreath?.cancel()
        basketBreath = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val SHEET_RISE_DP = 40f
        const val SHEET_RISE_MS = 360L
        const val CTA_FROM_SCALE = 0.94f
        const val CTA_MS = 420L
        const val CTA_TENSION = 1.1f
        const val BREATH_DP = 4f
        const val BREATH_MS = 2200L

        /** Finite on purpose: a perpetual animation would keep the window busy. */
        const val BREATH_CYCLES = 5
    }
}
