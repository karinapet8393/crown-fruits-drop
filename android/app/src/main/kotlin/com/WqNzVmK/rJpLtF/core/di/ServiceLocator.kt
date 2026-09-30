package com.WqNzVmK.rJpLtF.core.di

import android.content.Context
import com.WqNzVmK.rJpLtF.data.local.ProgressStorage
import com.WqNzVmK.rJpLtF.data.repository.ProgressRepositoryImpl
import com.WqNzVmK.rJpLtF.data.repository.RecipeRepositoryImpl
import com.WqNzVmK.rJpLtF.domain.repository.ProgressRepository
import com.WqNzVmK.rJpLtF.domain.repository.RecipeRepository
import com.WqNzVmK.rJpLtF.domain.usecase.GetProgressUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.GetRecipesUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.SaveRoundResultUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.SpawnItemUseCase

/**
 * Manual dependency wiring. There is deliberately no Application subclass:
 * MainActivity calls [init] with the application context before the first fragment
 * is committed, so every getter below is ready by the time a ViewModel asks.
 */
object ServiceLocator {

    private var recipes: RecipeRepository? = null
    private var progress: ProgressRepository? = null

    fun init(context: Context) {
        if (recipes == null) {
            recipes = RecipeRepositoryImpl()
        }
        if (progress == null) {
            progress = ProgressRepositoryImpl(ProgressStorage(context.applicationContext))
        }
    }

    val recipeRepository: RecipeRepository
        get() = recipes ?: RecipeRepositoryImpl().also { recipes = it }

    val progressRepository: ProgressRepository
        get() = progress ?: throw IllegalStateException("ServiceLocator.init was not called")

    fun getRecipes(): GetRecipesUseCase = GetRecipesUseCase(recipeRepository)

    fun spawnItem(): SpawnItemUseCase = SpawnItemUseCase(recipeRepository)

    fun getProgress(): GetProgressUseCase = GetProgressUseCase(progressRepository)

    fun saveRoundResult(): SaveRoundResultUseCase = SaveRoundResultUseCase(progressRepository)
}
