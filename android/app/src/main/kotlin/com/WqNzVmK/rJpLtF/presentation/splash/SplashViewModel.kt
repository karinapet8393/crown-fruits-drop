package com.WqNzVmK.rJpLtF.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.WqNzVmK.rJpLtF.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Holds the splash timer. The screen is shown for exactly
 * [GameConfig.LOADER_DURATION_MS] and only then reports that the menu may take over,
 * so the branded frame is always on screen long enough to be seen (and captured).
 */
class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState.INITIAL)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun start() {
        if (timerJob?.isActive == true || _state.value.readyForMenu) return
        timerJob = viewModelScope.launch {
            var elapsed = 0L
            while (elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(STEP_MS)
                elapsed += STEP_MS
                _state.value = _state.value.copy(elapsedMs = elapsed.coerceAtMost(GameConfig.LOADER_DURATION_MS))
            }
            _state.value = SplashUiState(
                loading = false,
                elapsedMs = GameConfig.LOADER_DURATION_MS,
                readyForMenu = true,
            )
        }
    }

    /** Guards against a second navigation if the fragment is recreated. */
    fun consumeTransition() {
        _state.value = _state.value.copy(readyForMenu = false)
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val STEP_MS = 200L
    }
}
