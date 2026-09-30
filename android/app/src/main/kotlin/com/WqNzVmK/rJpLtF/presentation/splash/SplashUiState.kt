package com.WqNzVmK.rJpLtF.presentation.splash

/** What the loading screen shows while the royal kitchen warms up. */
data class SplashUiState(
    val loading: Boolean,
    val elapsedMs: Long,
    val readyForMenu: Boolean,
) {
    companion object {
        val INITIAL = SplashUiState(loading = true, elapsedMs = 0L, readyForMenu = false)
    }
}
