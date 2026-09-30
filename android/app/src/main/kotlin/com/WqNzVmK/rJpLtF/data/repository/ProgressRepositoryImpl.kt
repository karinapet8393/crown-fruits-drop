package com.WqNzVmK.rJpLtF.data.repository

import com.WqNzVmK.rJpLtF.data.local.ProgressStorage
import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val storage: ProgressStorage) : ProgressRepository {

    override fun progress(): PlayerProgress = PlayerProgress(
        bestRecipes = storage.bestRecipes,
        bestAccuracy = storage.bestAccuracy,
        roundsPlayed = storage.roundsPlayed,
    )

    override fun save(result: RoundResult): PlayerProgress {
        if (result.servedRecipes > storage.bestRecipes) {
            storage.bestRecipes = result.servedRecipes
        }
        if (result.accuracyPercent > storage.bestAccuracy) {
            storage.bestAccuracy = result.accuracyPercent
        }
        storage.roundsPlayed = storage.roundsPlayed + 1
        return progress()
    }
}
