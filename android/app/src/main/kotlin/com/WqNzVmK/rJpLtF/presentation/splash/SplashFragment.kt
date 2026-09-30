package com.WqNzVmK.rJpLtF.presentation.splash

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
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentSplashBinding
import com.WqNzVmK.rJpLtF.presentation.menu.MenuFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Branded loading screen. Deliberately much darker than the menu (night kitchen
 * art under a deep scrim, no chips, no call to action), so the two screens can
 * never be mistaken for one another.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: SplashViewModel by viewModels()

    private var animator: SplashAnimator? = null
    private var navigated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.splashBg.loadAsset(AppAssets.BG_LOADER)

        val choreography = SplashAnimator(
            crown = binding.splashCrown,
            title = binding.splashTitle,
            brand = binding.splashBrand,
            divider = binding.splashDivider,
        )
        animator = choreography
        choreography.play {
            _binding?.splashLoading?.announceForAccessibility(getString(R.string.splash_loading))
        }

        observeState()
        viewModel.start()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: SplashUiState) {
        val safeBinding = _binding ?: return
        safeBinding.splashProgress.visibility = if (state.loading) View.VISIBLE else View.INVISIBLE
        if (state.readyForMenu && !navigated) {
            navigated = true
            viewModel.consumeTransition()
            goToMenu()
        }
    }

    private fun goToMenu() {
        val choreography = animator
        if (choreography == null) {
            commitMenu()
            return
        }
        choreography.playExit { commitMenu() }
    }

    private fun commitMenu() {
        if (!isAdded) return
        // No back stack entry: the splash can never be returned to.
        Navigator.replaceWithFade(parentFragmentManager, MenuFragment())
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        _binding = null
        super.onDestroyView()
    }
}
