package com.WqNzVmK.rJpLtF.presentation.recipes

import com.WqNzVmK.rJpLtF.domain.model.Recipe

/** One row of the royal menu together with its unlock state. */
data class RecipeListItem(
    val recipe: Recipe,
    val state: State,
) {
    enum class State { DONE, NEXT, LOCKED }
}
