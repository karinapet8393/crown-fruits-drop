package com.WqNzVmK.rJpLtF.presentation.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.AppAssets
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.core.navigation.Navigator
import com.WqNzVmK.rJpLtF.databinding.FragmentTutorialBinding
import com.WqNzVmK.rJpLtF.presentation.menu.MenuFragment

/**
 * Three step cards that explain the feast. Uses vector check / cross glyphs, never
 * emoji, so the meaning survives on every Android font fallback.
 */
class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tutorialBg.loadAsset(AppAssets.BG_GAME)

        binding.tutorialStepOne.showSprite(
            AppAssets.SPRITE_BASKET_GOLD,
            getString(R.string.desc_step_lanes),
        )
        binding.tutorialStepTwo.showSprite(
            AppAssets.SPRITE_APPLE_GOLD,
            getString(R.string.desc_step_good),
        )
        binding.tutorialStepTwo.showGlyph(R.drawable.ic_check)
        binding.tutorialStepThree.showSprite(
            AppAssets.SPRITE_PEPPER_ROYAL,
            getString(R.string.desc_step_bad),
        )
        binding.tutorialStepThree.showGlyph(R.drawable.ic_cross)

        binding.tutorialBack.setOnClickListener { goBack() }
        binding.tutorialMenu.setOnClickListener { goBack() }
    }

    private fun goBack() {
        Navigator.back(parentFragmentManager, MenuFragment())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
