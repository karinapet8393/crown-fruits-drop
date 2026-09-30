package com.WqNzVmK.rJpLtF.data.sample

import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.domain.model.FruitKind
import com.WqNzVmK.rJpLtF.domain.model.Recipe

/**
 * The royal menu of one feast. Four dishes, each asking for two fruits, ordered
 * from the easiest tart to the six-fruit coronation punch.
 */
object SampleData {

    val ROYAL_MENU: List<Recipe> = listOf(
        Recipe(
            id = 1,
            nameResId = R.string.recipe_golden_tart,
            firstFruit = FruitKind.GOLDEN_APPLE,
            firstTarget = 3,
            secondFruit = FruitKind.HONEY_PEAR,
            secondTarget = 2,
        ),
        Recipe(
            id = 2,
            nameResId = R.string.recipe_berry_pie,
            firstFruit = FruitKind.CROWN_BERRY,
            firstTarget = 3,
            secondFruit = FruitKind.RED_PLUM,
            secondTarget = 3,
        ),
        Recipe(
            id = 3,
            nameResId = R.string.recipe_emerald_sorbet,
            firstFruit = FruitKind.MINT_GRAPE,
            firstTarget = 4,
            secondFruit = FruitKind.GREEN_FIG,
            secondTarget = 2,
        ),
        Recipe(
            id = 4,
            nameResId = R.string.recipe_coronation_punch,
            firstFruit = FruitKind.BLUE_PLUM,
            firstTarget = 3,
            secondFruit = FruitKind.GOLDEN_APPLE,
            secondTarget = 3,
        ),
    )

    /** Items the head cook never wants on a plate. */
    val PENALTY_ITEMS: List<FruitKind> = listOf(FruitKind.ROYAL_PEPPER)
}
