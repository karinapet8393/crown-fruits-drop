package com.WqNzVmK.rJpLtF.presentation.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.WqNzVmK.rJpLtF.core.config.GameConfig

/**
 * The entrance choreography of the loading screen.
 *
 * Deliberately finite: crown, wordmark, brand line and the crown divider each play
 * exactly once and then settle. Nothing loops forever, because a perpetually
 * animating window keeps the accessibility tree busy and blocks automated capture.
 * The only continuous motion on the splash is the Material indeterminate indicator.
 */
class SplashAnimator(
    private val crown: View,
    private val title: View,
    private val brand: View,
    private val divider: View,
) {

    private var running: AnimatorSet? = null

    /** Plays the whole sequence; [onSettled] fires once every view has landed. */
    fun play(onSettled: () -> Unit) {
        cancel()

        prepare()

        val crownFade = ObjectAnimator.ofFloat(crown, View.ALPHA, 0f, 1f).apply {
            duration = GameConfig.SPLASH_CROWN_ANIM_MS
            interpolator = DecelerateInterpolator()
        }
        val crownScaleX = ObjectAnimator.ofFloat(crown, View.SCALE_X, CROWN_FROM_SCALE, 1f).apply {
            duration = GameConfig.SPLASH_CROWN_ANIM_MS
            interpolator = OvershootInterpolator(OVERSHOOT_TENSION)
        }
        val crownScaleY = ObjectAnimator.ofFloat(crown, View.SCALE_Y, CROWN_FROM_SCALE, 1f).apply {
            duration = GameConfig.SPLASH_CROWN_ANIM_MS
            interpolator = OvershootInterpolator(OVERSHOOT_TENSION)
        }

        val titleFade = ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f).apply {
            duration = GameConfig.SPLASH_TITLE_ANIM_MS
            startDelay = GameConfig.SPLASH_TITLE_DELAY_MS
            interpolator = DecelerateInterpolator()
        }
        val titleRise = ObjectAnimator.ofFloat(
            title,
            View.TRANSLATION_Y,
            title.resources.displayMetrics.density * TITLE_RISE_DP,
            0f,
        ).apply {
            duration = GameConfig.SPLASH_TITLE_ANIM_MS
            startDelay = GameConfig.SPLASH_TITLE_DELAY_MS
            interpolator = DecelerateInterpolator()
        }

        val brandFade = ObjectAnimator.ofFloat(brand, View.ALPHA, 0f, BRAND_TARGET_ALPHA).apply {
            duration = GameConfig.SPLASH_TITLE_ANIM_MS
            startDelay = GameConfig.SPLASH_BRAND_DELAY_MS
            interpolator = DecelerateInterpolator()
        }

        val dividerFade = ObjectAnimator.ofFloat(divider, View.ALPHA, 0f, 1f).apply {
            duration = GameConfig.SPLASH_TITLE_ANIM_MS
            startDelay = GameConfig.SPLASH_DIVIDER_DELAY_MS
            interpolator = DecelerateInterpolator()
        }
        val dividerWiden = ObjectAnimator.ofFloat(divider, View.SCALE_X, DIVIDER_FROM_SCALE, 1f).apply {
            duration = GameConfig.SPLASH_TITLE_ANIM_MS
            startDelay = GameConfig.SPLASH_DIVIDER_DELAY_MS
            interpolator = DecelerateInterpolator()
        }

        val set = AnimatorSet()
        set.playTogether(
            crownFade,
            crownScaleX,
            crownScaleY,
            titleFade,
            titleRise,
            brandFade,
            dividerFade,
            dividerWiden,
        )
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                settle()
                onSettled()
            }
        })
        running = set
        set.start()
    }

    /** Short fade used right before the menu takes over. */
    fun playExit(onDone: () -> Unit) {
        val fade = ObjectAnimator.ofFloat(title, View.ALPHA, title.alpha, EXIT_ALPHA).apply {
            duration = EXIT_MS
            interpolator = AccelerateInterpolator()
        }
        fade.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) = onDone()
        })
        fade.start()
    }

    fun cancel() {
        running?.cancel()
        running = null
    }

    private fun prepare() {
        crown.alpha = 0f
        crown.scaleX = CROWN_FROM_SCALE
        crown.scaleY = CROWN_FROM_SCALE
        title.alpha = 0f
        title.translationY = title.resources.displayMetrics.density * TITLE_RISE_DP
        brand.alpha = 0f
        divider.alpha = 0f
        divider.scaleX = DIVIDER_FROM_SCALE
    }

    private fun settle() {
        crown.alpha = 1f
        crown.scaleX = 1f
        crown.scaleY = 1f
        title.alpha = 1f
        title.translationY = 0f
        brand.alpha = BRAND_TARGET_ALPHA
        divider.alpha = 1f
        divider.scaleX = 1f
    }

    private companion object {
        const val CROWN_FROM_SCALE = 0.75f
        const val DIVIDER_FROM_SCALE = 0.4f
        const val TITLE_RISE_DP = 28f
        const val BRAND_TARGET_ALPHA = 0.9f
        const val OVERSHOOT_TENSION = 1.1f
        const val EXIT_ALPHA = 0.85f
        const val EXIT_MS = 180L
    }
}
