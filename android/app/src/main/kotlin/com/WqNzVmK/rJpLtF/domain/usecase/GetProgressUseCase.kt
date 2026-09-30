package com.WqNzVmK.rJpLtF.domain.usecase

import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress
import com.WqNzVmK.rJpLtF.domain.repository.ProgressRepository

/** Career record shown on the menu and on the result screen. */
class GetProgressUseCase(private val repository: ProgressRepository) {
    operator fun invoke(): PlayerProgress = repository.progress()
}
