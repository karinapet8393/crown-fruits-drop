package com.WqNzVmK.rJpLtF.presentation.menu

import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress

/** Career numbers shown on the marble sheet. */
data class MenuUiState(
    val progress: PlayerProgress,
) {
    /** A single lonely tile looks broken, so the whole row hides unless both fit. */
    val showStats: Boolean
        get() = progress.bestRecipes > 0 && progress.bestAccuracy > 0

    companion object {
        val INITIAL = MenuUiState(PlayerProgress.EMPTY)
    }
}
