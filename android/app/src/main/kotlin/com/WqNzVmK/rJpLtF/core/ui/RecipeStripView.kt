package com.WqNzVmK.rJpLtF.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.WqNzVmK.rJpLtF.R
import com.WqNzVmK.rJpLtF.core.assets.FruitAssetMap
import com.WqNzVmK.rJpLtF.core.assets.loadAsset
import com.WqNzVmK.rJpLtF.databinding.ViewRecipeStripBinding
import com.WqNzVmK.rJpLtF.domain.model.Recipe

/**
 * The recipe card in miniature: the two wanted fruits as AI sprites, their
 * counters and a gold progress bar. Shared by the game header and the recipe list.
 */
class RecipeStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewRecipeStripBinding.inflate(LayoutInflater.from(context), this)

    fun render(recipe: Recipe, collectedFirst: Int, collectedSecond: Int) {
        binding.stripFruitA.loadAsset(FruitAssetMap.assetFor(recipe.firstFruit))
        binding.stripFruitB.loadAsset(FruitAssetMap.assetFor(recipe.secondFruit))

        binding.stripFruitA.contentDescription = context.getString(recipe.firstFruit.nameResId)
        binding.stripFruitB.contentDescription = context.getString(recipe.secondFruit.nameResId)

        val first = collectedFirst.coerceAtMost(recipe.firstTarget)
        val second = collectedSecond.coerceAtMost(recipe.secondTarget)

        binding.stripCountA.text = context.getString(R.string.game_counter, first, recipe.firstTarget)
        binding.stripCountB.text = context.getString(R.string.game_counter, second, recipe.secondTarget)

        val done = first + second
        val total = recipe.totalTarget.coerceAtLeast(1)
        binding.stripProgress.max = total
        binding.stripProgress.setProgressCompat(done, false)

        val state = context.getString(R.string.value_fraction, done, total)
        ViewCompat.setStateDescription(this, state)
        contentDescription = "${context.getString(recipe.nameResId)} $state"
    }
}
