package com.WqNzVmK.rJpLtF.domain.model

/**
 * Everything that can slide down a palace chute. Pure Kotlin: the enum only carries
 * the string resource id of its royal name, never an Android type.
 */
enum class FruitKind(val nameResId: Int) {
    GOLDEN_APPLE(com.WqNzVmK.rJpLtF.R.string.fruit_golden_apple),
    HONEY_PEAR(com.WqNzVmK.rJpLtF.R.string.fruit_honey_pear),
    CROWN_BERRY(com.WqNzVmK.rJpLtF.R.string.fruit_crown_berry),
    RED_PLUM(com.WqNzVmK.rJpLtF.R.string.fruit_red_plum),
    MINT_GRAPE(com.WqNzVmK.rJpLtF.R.string.fruit_mint_grape),
    GREEN_FIG(com.WqNzVmK.rJpLtF.R.string.fruit_green_fig),
    BLUE_PLUM(com.WqNzVmK.rJpLtF.R.string.fruit_blue_plum),
    ROYAL_PEPPER(com.WqNzVmK.rJpLtF.R.string.fruit_royal_pepper);

    /** Penalty items are never part of a recipe. */
    val isPenalty: Boolean get() = this == ROYAL_PEPPER
}
