package com.WqNzVmK.rJpLtF.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.WqNzVmK.rJpLtF.core.di.ServiceLocator
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.domain.usecase.GetProgressUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel(
    result: RoundResult,
    getProgress: GetProgressUseCase,
    getRecipes: GetRecipesUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<ResultUiState>
    val state: StateFlow<ResultUiState>

    init {
        val recipes = getRecipes()
        val next = recipes.getOrNull(result.servedRecipes)
        _state = MutableStateFlow(
            ResultUiState(
                result = result,
                progress = getProgress(),
                nextRecipeNameResId = next?.nameResId,
            ),
        )
        state = _state.asStateFlow()
    }

    companion object {
        fun factory(result: RoundResult): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ResultViewModel(
                    result = result,
                    getProgress = ServiceLocator.getProgress(),
                    getRecipes = ServiceLocator.getRecipes(),
                )
            }
        }
    }
}
