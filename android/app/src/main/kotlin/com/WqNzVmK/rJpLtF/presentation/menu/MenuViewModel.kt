package com.WqNzVmK.rJpLtF.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.WqNzVmK.rJpLtF.core.di.ServiceLocator
import com.WqNzVmK.rJpLtF.domain.usecase.GetProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(private val getProgress: GetProgressUseCase) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState.INITIAL)
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    fun refresh() {
        _state.value = MenuUiState(getProgress())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MenuViewModel(ServiceLocator.getProgress()) }
        }
    }
}
