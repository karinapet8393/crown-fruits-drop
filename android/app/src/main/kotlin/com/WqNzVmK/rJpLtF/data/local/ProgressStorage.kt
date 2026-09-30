package com.WqNzVmK.rJpLtF.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * The only persistence in the app: a small SharedPreferences file with the career
 * record. Nothing leaves the device, nothing is ever fetched from a network.
 */
class ProgressStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var bestRecipes: Int
        get() = prefs.getInt(KEY_BEST_RECIPES, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_RECIPES, value).apply()

    var bestAccuracy: Int
        get() = prefs.getInt(KEY_BEST_ACCURACY, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_ACCURACY, value).apply()

    var roundsPlayed: Int
        get() = prefs.getInt(KEY_ROUNDS, 0)
        set(value) = prefs.edit().putInt(KEY_ROUNDS, value).apply()

    private companion object {
        const val FILE_NAME = "crown_fruits_progress"
        const val KEY_BEST_RECIPES = "best_recipes"
        const val KEY_BEST_ACCURACY = "best_accuracy"
        const val KEY_ROUNDS = "rounds_played"
    }
}
