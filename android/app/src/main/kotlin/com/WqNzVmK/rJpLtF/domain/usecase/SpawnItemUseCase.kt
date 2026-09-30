package com.WqNzVmK.rJpLtF.domain.usecase

import com.WqNzVmK.rJpLtF.domain.model.FruitKind
import com.WqNzVmK.rJpLtF.domain.model.Recipe
import com.WqNzVmK.rJpLtF.domain.repository.RecipeRepository
import kotlin.random.Random

/**
 * Picks the next item and the chute it slides down.
 *
 * Fair-spawn rule: an item the current recipe does NOT ask for never appears in the
 * lane the basket already holds. Standing still is therefore always safe, and a
 * mistake is only ever the consequence of moving into a bad chute. Recipe fruits may
 * land anywhere, so the feast still progresses while the cook stays put.
 */
class SpawnItemUseCase(
    private val repository: RecipeRepository,
    private val random: Random = Random.Default,
) {

    data class Spawn(val kind: FruitKind, val lane: Int)

    operator fun invoke(recipe: Recipe, laneCount: Int, basketLane: Int): Spawn {
        val wanted = random.nextInt(100) < WANTED_SHARE_PERCENT
        return if (wanted) {
            val kind = if (random.nextBoolean()) recipe.firstFruit else recipe.secondFruit
            Spawn(kind, random.nextInt(laneCount))
        } else {
            Spawn(penaltyKind(recipe), safeLane(laneCount, basketLane))
        }
    }

    private fun penaltyKind(recipe: Recipe): FruitKind {
        val pool = FruitKind.entries.filter { !recipe.wants(it) }
        val penalties = repository.penaltyKinds()
        val merged = pool + penalties
        if (merged.isEmpty()) return FruitKind.ROYAL_PEPPER
        return merged[random.nextInt(merged.size)]
    }

    private fun safeLane(laneCount: Int, basketLane: Int): Int {
        if (laneCount <= 1) return 0
        val candidates = (0 until laneCount).filter { it != basketLane }
        return candidates[random.nextInt(candidates.size)]
    }

    private companion object {
        const val WANTED_SHARE_PERCENT = 55
    }
}
