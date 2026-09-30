package com.WqNzVmK.rJpLtF.domain.repository

import com.WqNzVmK.rJpLtF.domain.model.FruitKind
import com.WqNzVmK.rJpLtF.domain.model.Recipe

/** Source of the royal menu. */
interface RecipeRepository {
    fun recipes(): List<Recipe>
    fun penaltyKinds(): List<FruitKind>
}
