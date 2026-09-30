package com.WqNzVmK.rJpLtF.domain.model

/**
 * A single item sliding down one of the three chutes.
 * [progress] runs 0f at the top rim to 1f at the basket line.
 */
data class FallingItem(
    val id: Long,
    val kind: FruitKind,
    val lane: Int,
    val progress: Float,
) {
    fun advanced(delta: Float): FallingItem = copy(progress = progress + delta)
}
