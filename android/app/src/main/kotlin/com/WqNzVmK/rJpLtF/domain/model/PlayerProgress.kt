package com.WqNzVmK.rJpLtF.domain.model

/** Persisted career of the royal cook. */
data class PlayerProgress(
    val bestRecipes: Int,
    val bestAccuracy: Int,
    val roundsPlayed: Int,
) {
    companion object {
        val EMPTY = PlayerProgress(bestRecipes = 0, bestAccuracy = 0, roundsPlayed = 0)
    }
}
