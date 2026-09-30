package com.WqNzVmK.rJpLtF.domain.usecase

import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress
import com.WqNzVmK.rJpLtF.domain.model.RoundResult
import com.WqNzVmK.rJpLtF.domain.repository.ProgressRepository

/** Persists the finished round and returns the refreshed career. */
class SaveRoundResultUseCase(private val repository: ProgressRepository) {
    operator fun invoke(result: RoundResult): PlayerProgress = repository.save(result)
}
