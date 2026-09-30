package com.WqNzVmK.rJpLtF.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.WqNzVmK.rJpLtF.core.di.ServiceLocator
import com.WqNzVmK.rJpLtF.domain.usecase.GetProgressUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipesViewModel(
    private val getRecipes: GetRecipesUseCase,
    private val getProgress: GetProgressUseCase,
) : ViewModel() {

    private val _items = MutableStateFlow<List<RecipeListItem>>(emptyList())
    val items: StateFlow<List<RecipeListItem>> = _items.asStateFlow()

    fun refresh() {
        val best = getProgress().bestRecipes
        _items.value = getRecipes().mapIndexed { index, recipe ->
            val state = when {
                index < best -> RecipeListItem.State.DONE
                index == best -> RecipeListItem.State.NEXT
                else -> RecipeListItem.State.LOCKED
            }
            RecipeListItem(recipe, state)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecipesViewModel(
                    getRecipes = ServiceLocator.getRecipes(),
                    getProgress = ServiceLocator.getProgress(),
                )
            }
        }
    }
}
