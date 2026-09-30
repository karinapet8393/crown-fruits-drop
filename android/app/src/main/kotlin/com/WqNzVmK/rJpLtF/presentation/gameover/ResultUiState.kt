package com.WqNzVmK.rJpLtF.presentation.gameover

import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress
import com.WqNzVmK.rJpLtF.domain.model.RoundResult

/** Everything the result screen renders after a finished feast. */
data class ResultUiState(
    val result: RoundResult,
    val progress: PlayerProgress,
    val nextRecipeNameResId: Int?,
) {
    val servedRecipes: Int get() = result.servedRecipes
    val totalRecipes: Int get() = result.totalRecipes
    val accuracy: Int get() = result.accuracyPercent
    val best: Int get() = progress.bestRecipes
    val won: Boolean get() = result.won

    /** A row with a single visible tile looks broken, so it needs at least two. */
    val visibleStatCount: Int
        get() = listOf(servedRecipes > 0, accuracy > 0, best > 0).count { it }

    val showStats: Boolean get() = visibleStatCount >= MIN_VISIBLE_STATS

    private companion object {
        const val MIN_VISIBLE_STATS = 2
    }
}
