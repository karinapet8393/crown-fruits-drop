package com.WqNzVmK.rJpLtF.domain.repository

import com.WqNzVmK.rJpLtF.domain.model.PlayerProgress
import com.WqNzVmK.rJpLtF.domain.model.RoundResult

/** Local career storage. */
interface ProgressRepository {
    fun progress(): PlayerProgress
    fun save(result: RoundResult): PlayerProgress
}
