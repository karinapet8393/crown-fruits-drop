package com.WqNzVmK.rJpLtF.domain.usecase

import com.WqNzVmK.rJpLtF.domain.model.Recipe
import com.WqNzVmK.rJpLtF.domain.repository.RecipeRepository

/** The four dishes of one feast, in serving order. */
class GetRecipesUseCase(private val repository: RecipeRepository) {
    operator fun invoke(): List<Recipe> = repository.recipes()
}
