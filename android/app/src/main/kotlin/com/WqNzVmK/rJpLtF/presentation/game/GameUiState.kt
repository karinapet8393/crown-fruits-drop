package com.WqNzVmK.rJpLtF.presentation.game

import com.WqNzVmK.rJpLtF.core.config.GameConfig
import com.WqNzVmK.rJpLtF.domain.model.FallingItem
import com.WqNzVmK.rJpLtF.domain.model.GamePhase
import com.WqNzVmK.rJpLtF.domain.model.Recipe
import com.WqNzVmK.rJpLtF.domain.model.RoundResult

/** One rendered frame of the feast. */
data class GameUiState(
    val phase: GamePhase,
    val recipe: Recipe,
    val recipeIndex: Int,
    val recipeCount: Int,
    val collectedFirst: Int,
    val collectedSecond: Int,
    val items: List<FallingItem>,
    val basketLane: Int,
    val mistakes: Int,
    val servedRecipes: Int,
    val secondsLeft: Int,
    val flashLane: Int,
    val flashStrength: Float,
    val mistakeStrength: Float,
    val basketPulse: Float,
    val result: RoundResult?,
) {
    val bannerVisible: Boolean get() = phase == GamePhase.RECIPE_COMPLETE
    val timeRunningOut: Boolean get() = secondsLeft <= LOW_TIME_SECONDS

    companion object {
        const val NO_LANE = -1
        const val LOW_TIME_SECONDS = 10

        fun initial(recipe: Recipe, recipeCount: Int) = GameUiState(
            phase = GamePhase.IDLE,
            recipe = recipe,
            recipeIndex = 0,
            recipeCount = recipeCount,
            collectedFirst = 0,
            collectedSecond = 0,
            items = emptyList(),
            basketLane = GameConfig.LANE_CENTER,
            mistakes = 0,
            servedRecipes = 0,
            secondsLeft = (GameConfig.ROUND_TIME_MS / 1000L).toInt(),
            flashLane = NO_LANE,
            flashStrength = 0f,
            mistakeStrength = 0f,
            basketPulse = 1f,
            result = null,
        )
    }
}
