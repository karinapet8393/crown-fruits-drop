package com.WqNzVmK.rJpLtF.data.repository

import com.WqNzVmK.rJpLtF.data.sample.SampleData
import com.WqNzVmK.rJpLtF.domain.model.FruitKind
import com.WqNzVmK.rJpLtF.domain.model.Recipe
import com.WqNzVmK.rJpLtF.domain.repository.RecipeRepository

class RecipeRepositoryImpl : RecipeRepository {

    override fun recipes(): List<Recipe> = SampleData.ROYAL_MENU

    override fun penaltyKinds(): List<FruitKind> = SampleData.PENALTY_ITEMS
}
