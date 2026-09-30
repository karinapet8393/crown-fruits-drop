package com.WqNzVmK.rJpLtF.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.WqNzVmK.rJpLtF.core.config.GameConfig
import com.WqNzVmK.rJpLtF.core.di.ServiceLocator
import com.WqNzVmK.rJpLtF.domain.model.FallingItem
import com.WqNzVmK.rJpLtF.domain.model.GamePhase
import com.WqNzVmK.rJpLtF.domain.model.Recipe
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.domain.usecase.GetRecipesUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.SaveRoundResultUseCase
import com.WqNzVmK.rJpLtF.domain.usecase.SpawnItemUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the feast: a 16 ms coroutine loop that spawns, moves and resolves items.
 *
 * The round always ends: three mistakes finish it inside one flash, and
 * [GameConfig.ROUND_TIME_MS] is a hard backstop that is never re-armed by input, so
 * a result screen appears even if nobody ever touches the board.
 */
class GameViewModel(
    getRecipes: GetRecipesUseCase,
    private val spawnItem: SpawnItemUseCase,
    private val saveRoundResult: SaveRoundResultUseCase,
) : ViewModel() {

    private val recipes: List<Recipe> = getRecipes()

    private val _state = MutableStateFlow(GameUiState.initial(recipes.first(), recipes.size))
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var loopJob: Job? = null
    private var nextItemId = 0L
    private var elapsedMs = 0L
    private var sinceSpawnMs = 0L
    private var bannerLeftMs = 0L
    private var flashLeftMs = 0L
    private var mistakeLeftMs = 0L
    private var pulseLeftMs = 0L

    private var correctCatches = 0
    private var totalCatches = 0
    private var resultSaved = false

    fun start() {
        if (loopJob?.isActive == true) return
        loopJob = viewModelScope.launch {
            // The round is measured against the wall clock, not against the number of
            // ticks. delay(TICK_MS) only guarantees a lower bound, so on a loaded
            // device one tick easily takes several times longer; feeding the nominal
            // TICK_MS back into tick() stretched the 26 s round into minutes of real
            // time and the result screen never surfaced.
            var previousUptimeMs = SystemClock.uptimeMillis()
            while (_state.value.phase != GamePhase.FINISHED) {
                delay(GameConfig.TICK_MS)
                val now = SystemClock.uptimeMillis()
                val deltaMs = (now - previousUptimeMs).coerceAtLeast(GameConfig.TICK_MS)
                previousUptimeMs = now
                tick(deltaMs)
            }
        }
    }

    fun stop() {
        loopJob?.cancel()
        loopJob = null
    }

    fun moveBasket(lane: Int) {
        val current = _state.value
        if (current.phase == GamePhase.FINISHED) return
        if (lane < 0 || lane >= GameConfig.LANE_COUNT) return
        _state.value = current.copy(basketLane = lane)
    }

    private fun tick(deltaMs: Long) {
        val current = _state.value
        if (current.phase == GamePhase.FINISHED) return

        elapsedMs += deltaMs
        val secondsLeft = ((GameConfig.ROUND_TIME_MS - elapsedMs).coerceAtLeast(0L) / 1000L).toInt()

        flashLeftMs = (flashLeftMs - deltaMs).coerceAtLeast(0L)
        mistakeLeftMs = (mistakeLeftMs - deltaMs).coerceAtLeast(0L)
        pulseLeftMs = (pulseLeftMs - deltaMs).coerceAtLeast(0L)

        var phase = if (current.phase == GamePhase.IDLE) GamePhase.PLAYING else current.phase
        var items = current.items
        var collectedFirst = current.collectedFirst
        var collectedSecond = current.collectedSecond
        var mistakes = current.mistakes
        var served = current.servedRecipes
        var recipeIndex = current.recipeIndex
        var recipe = current.recipe

        if (bannerLeftMs > 0L) {
            bannerLeftMs = (bannerLeftMs - deltaMs).coerceAtLeast(0L)
            if (bannerLeftMs == 0L) {
                phase = GamePhase.PLAYING
            }
        } else {
            sinceSpawnMs += deltaMs
            if (sinceSpawnMs >= spawnIntervalMs(served)) {
                sinceSpawnMs = 0L
                val spawn = spawnItem(recipe, GameConfig.LANE_COUNT, current.basketLane)
                items = items + FallingItem(nextItemId++, spawn.kind, spawn.lane, 0f)
            }

            // The clock above is real time so the round always ends on schedule; the
            // fall step is capped so a stalled frame can never teleport an item past
            // the catch line.
            val step = deltaMs.coerceAtMost(GameConfig.MAX_TICK_MS).toFloat() / fallDurationMs(served)
            val survivors = ArrayList<FallingItem>(items.size)

            items.map { it.advanced(step) }.forEach { item ->
                if (item.progress < GameConfig.CATCH_PROGRESS) {
                    survivors.add(item)
                    return@forEach
                }
                if (item.lane != current.basketLane) {
                    if (item.progress < OFF_BOARD_PROGRESS) survivors.add(item)
                    return@forEach
                }
                totalCatches++
                if (item.kind == recipe.firstFruit && collectedFirst < recipe.firstTarget) {
                    collectedFirst++
                    correctCatches++
                    flashLeftMs = GameConfig.FLASH_MS
                    pulseLeftMs = GameConfig.FLASH_MS
                } else if (item.kind == recipe.secondFruit && collectedSecond < recipe.secondTarget) {
                    collectedSecond++
                    correctCatches++
                    flashLeftMs = GameConfig.FLASH_MS
                    pulseLeftMs = GameConfig.FLASH_MS
                } else if (recipe.wants(item.kind)) {
                    correctCatches++
                    flashLeftMs = GameConfig.FLASH_MS
                } else {
                    mistakes++
                    mistakeLeftMs = GameConfig.MISTAKE_FLASH_MS
                }
            }
            items = survivors

            if (collectedFirst >= recipe.firstTarget && collectedSecond >= recipe.secondTarget) {
                served++
                collectedFirst = 0
                collectedSecond = 0
                items = emptyList()
                if (served < recipes.size) {
                    recipeIndex = served
                    recipe = recipes[recipeIndex]
                    phase = GamePhase.RECIPE_COMPLETE
                    bannerLeftMs = GameConfig.RECIPE_BANNER_MS
                }
            }
        }

        val outOfTime = elapsedMs >= GameConfig.ROUND_TIME_MS
        val outOfPatience = mistakes >= GameConfig.MAX_MISTAKES
        val allServed = served >= recipes.size
        val finished = outOfTime || outOfPatience || allServed
        val result = if (finished) buildResult(served, mistakes, won = allServed) else null

        _state.value = current.copy(
            phase = if (finished) GamePhase.FINISHED else phase,
            recipe = recipe,
            recipeIndex = recipeIndex,
            collectedFirst = collectedFirst,
            collectedSecond = collectedSecond,
            items = if (finished) emptyList() else items,
            mistakes = mistakes,
            servedRecipes = served,
            secondsLeft = secondsLeft,
            flashLane = if (flashLeftMs > 0L) current.basketLane else GameUiState.NO_LANE,
            flashStrength = flashLeftMs.toFloat() / GameConfig.FLASH_MS,
            mistakeStrength = mistakeLeftMs.toFloat() / GameConfig.MISTAKE_FLASH_MS,
            basketPulse = 1f + PULSE_AMPLITUDE * (pulseLeftMs.toFloat() / GameConfig.FLASH_MS),
            result = result,
        )
    }

    private fun buildResult(served: Int, mistakes: Int, won: Boolean): RoundResult {
        val result = RoundResult(
            servedRecipes = served,
            totalRecipes = recipes.size,
            correctCatches = correctCatches,
            totalCatches = totalCatches,
            mistakes = mistakes,
            won = won,
        )
        if (!resultSaved) {
            resultSaved = true
            saveRoundResult(result)
        }
        return result
    }

    private fun spawnIntervalMs(served: Int): Long =
        (GameConfig.SPAWN_INTERVAL_MS - served * GameConfig.SPAWN_INTERVAL_STEP_MS)
            .coerceAtLeast(GameConfig.SPAWN_INTERVAL_MIN_MS)

    private fun fallDurationMs(served: Int): Float {
        val speed = GameConfig.FALL_SPEED_DP_S + served * GameConfig.FALL_SPEED_STEP_DP_S
        return (GameConfig.BOARD_REFERENCE_HEIGHT_DP / speed) * MILLIS_PER_SECOND
    }

    override fun onCleared() {
        stop()
        super.onCleared()
    }

    companion object {
        private const val OFF_BOARD_PROGRESS = 1.05f
        private const val PULSE_AMPLITUDE = 0.12f
        private const val MILLIS_PER_SECOND = 1000f

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GameViewModel(
                    getRecipes = ServiceLocator.getRecipes(),
                    spawnItem = ServiceLocator.spawnItem(),
                    saveRoundResult = ServiceLocator.saveRoundResult(),
                )
            }
        }
    }
}
