package com.WqNzVmK.rJpLtF.core.assets

import com.WqNzVmK.rJpLtF.domain.model.FruitKind

/** Maps a domain fruit to the AI sprite that represents it. */
object FruitAssetMap {

    fun assetFor(kind: FruitKind): String = when (kind) {
        FruitKind.GOLDEN_APPLE -> AppAssets.SPRITE_APPLE_GOLD
        FruitKind.HONEY_PEAR -> AppAssets.SPRITE_PEAR_HONEY
        FruitKind.CROWN_BERRY -> AppAssets.SPRITE_BERRY_CROWN
        FruitKind.RED_PLUM -> AppAssets.SPRITE_PLUM_RED
        FruitKind.MINT_GRAPE -> AppAssets.SPRITE_GRAPE_MINT
        FruitKind.GREEN_FIG -> AppAssets.SPRITE_FIG_GREEN
        FruitKind.BLUE_PLUM -> AppAssets.SPRITE_PLUM_BLUE
        FruitKind.ROYAL_PEPPER -> AppAssets.SPRITE_PEPPER_ROYAL
    }
}
