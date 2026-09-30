package com.WqNzVmK.rJpLtF.domain.model

/**
 * One royal dish: exactly two fruits with their target counts.
 * [nameResId] keeps the layer free of Android classes while still localisable.
 */
data class Recipe(
    val id: Int,
    val nameResId: Int,
    val firstFruit: FruitKind,
    val firstTarget: Int,
    val secondFruit: FruitKind,
    val secondTarget: Int,
) {
    val totalTarget: Int get() = firstTarget + secondTarget

    fun wants(kind: FruitKind): Boolean = kind == firstFruit || kind == secondFruit
}
