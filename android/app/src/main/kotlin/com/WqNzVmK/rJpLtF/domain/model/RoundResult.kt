package com.WqNzVmK.rJpLtF.domain.model

/** Everything the result screen and the progress storage need after a round. */
data class RoundResult(
    val servedRecipes: Int,
    val totalRecipes: Int,
    val correctCatches: Int,
    val totalCatches: Int,
    val mistakes: Int,
    val won: Boolean,
) {
    val accuracyPercent: Int
        get() = if (totalCatches <= 0) 0 else (correctCatches * 100) / totalCatches
}
