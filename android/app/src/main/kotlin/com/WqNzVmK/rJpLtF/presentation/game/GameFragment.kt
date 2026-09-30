package com.WqNzVmK.rJpLtF.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.core.config.GameConfig
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentGameBinding
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.presentation.gameover.ResultFragment
import com.WqNzVmK.rJpLtF.presentation.menu.MenuFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * The kitchen floor: header with the running recipe and the clock, the three-chute
 * board in the middle and the lane controls at the bottom (G1 classic stack).
 */
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: GameViewModel by viewModels { GameViewModel.Factory }

    private var handedOver = false
    private var lastHud: HudSignature? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameBg.loadAsset(AppAssets.BG_GAME)

        binding.gameMenu.setOnClickListener {
            viewModel.stop()
            Navigator.back(parentFragmentManager, MenuFragment())
        }
        binding.gameLaneLeft.setOnClickListener { viewModel.moveBasket(LANE_LEFT) }
        binding.gameLaneCenter.setOnClickListener { viewModel.moveBasket(GameConfig.LANE_CENTER) }
        binding.gameLaneRight.setOnClickListener { viewModel.moveBasket(LANE_RIGHT) }
        binding.gameBoard.setOnLaneTapListener { lane -> viewModel.moveBasket(lane) }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { render(it) }
            }
        }
    }

    private fun render(state: GameUiState) {
        val safeBinding = _binding ?: return

        // The board redraws on every one of the 60 ticks per second; the text HUD only
        // when a number actually changed, so the window is not kept busy for nothing.
        renderHud(state)

        safeBinding.gameBoard.render(
            items = state.items,
            basketLane = state.basketLane,
            flashLane = state.flashLane,
            flashStrength = state.flashStrength,
            mistakeStrength = state.mistakeStrength,
            basketPulse = state.basketPulse,
        )

        safeBinding.gameBanner.visibility = if (state.bannerVisible) View.VISIBLE else View.GONE

        val result = state.result
        if (result != null && !handedOver) {
            handedOver = true
            handOver(result)
        }
    }

    private fun renderHud(state: GameUiState) {
        val safeBinding = _binding ?: return
        val signature = HudSignature(
            recipeIndex = state.recipeIndex,
            collectedFirst = state.collectedFirst,
            collectedSecond = state.collectedSecond,
            mistakes = state.mistakes,
            servedRecipes = state.servedRecipes,
            secondsLeft = state.secondsLeft,
        )
        if (signature == lastHud) return
        lastHud = signature

        safeBinding.gameRecipeHeader.text = getString(
            R.string.game_recipe_header,
            state.recipeIndex + 1,
            state.recipeCount,
            getString(state.recipe.nameResId),
        )
        safeBinding.gameTimer.text = getString(R.string.game_timer, state.secondsLeft)
        safeBinding.gameTimer.setTextColor(
            requireContext().getColor(if (state.timeRunningOut) R.color.red else R.color.gold),
        )
        safeBinding.gameStatus.text = getString(
            R.string.game_status,
            state.recipeIndex + 1,
            state.recipeCount,
            state.servedRecipes,
        )
        safeBinding.gameRecipeStrip.render(state.recipe, state.collectedFirst, state.collectedSecond)
        safeBinding.gameMistakes.render(state.mistakes)
    }

    /** Snapshot of everything the text HUD shows, so redundant updates are skipped. */
    private data class HudSignature(
        val recipeIndex: Int,
        val collectedFirst: Int,
        val collectedSecond: Int,
        val mistakes: Int,
        val servedRecipes: Int,
        val secondsLeft: Int,
    )

    private fun handOver(result: RoundResult) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(GameConfig.RESULT_DELAY_MS)
            if (!isAdded) return@launch
            viewModel.stop()
            Navigator.push(parentFragmentManager, ResultFragment.newInstance(result))
        }
    }

    override fun onStart() {
        super.onStart()
        if (!handedOver) viewModel.start()
    }

    override fun onStop() {
        viewModel.stop()
        super.onStop()
    }

    override fun onDestroyView() {
        viewModel.stop()
        lastHud = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val LANE_LEFT = 0
        const val LANE_RIGHT = 2
    }
}
