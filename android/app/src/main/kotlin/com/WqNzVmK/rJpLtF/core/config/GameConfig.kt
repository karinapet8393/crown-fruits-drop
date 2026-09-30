package com.WqNzVmK.rJpLtF.core.config

/** Every tunable number of the feast lives here. */
object GameConfig {

    /** Splash stays exactly this long; the indeterminate indicator spins all of it. */
    const val LOADER_DURATION_MS = 8000L

    /** Title / crown / divider entrance offsets inside the splash. */
    const val SPLASH_CROWN_ANIM_MS = 520L
    const val SPLASH_TITLE_ANIM_MS = 480L
    const val SPLASH_TITLE_DELAY_MS = 180L
    const val SPLASH_BRAND_DELAY_MS = 420L
    const val SPLASH_DIVIDER_DELAY_MS = 520L

    /**
     * Hard round length. It doubles as the no-input backstop: the round always
     * resolves into the result screen after this long, whether the cook moved or not.
     */
    const val ROUND_TIME_MS = 26_000L
    const val IDLE_END_MS = 26_000L

    /** 30 fps: enough for the chutes, half the traversals of a 60 fps board. */
    const val TICK_MS = 33L

    /** Largest fall step one tick may apply, so a stalled frame cannot skip the catch line. */
    const val MAX_TICK_MS = 100L
    const val SPAWN_INTERVAL_MS = 780L
    const val SPAWN_INTERVAL_MIN_MS = 520L
    const val SPAWN_INTERVAL_STEP_MS = 40L

    /** Falling speed in dp per second, plus the bonus for every dish already served. */
    const val FALL_SPEED_DP_S = 340f
    const val FALL_SPEED_STEP_DP_S = 18f
    const val BOARD_REFERENCE_HEIGHT_DP = 420f

    /** The basket line: an item is resolved once it reaches this share of the chute. */
    const val CATCH_PROGRESS = 0.88f

    const val MAX_MISTAKES = 3
    const val RECIPE_COUNT = 4
    const val LANE_COUNT = 3
    const val LANE_CENTER = 1

    const val LANE_SWITCH_MS = 140L
    const val RECIPE_BANNER_MS = 900L
    const val RESULT_DELAY_MS = 420L
    const val FLASH_MS = 220L
    const val MISTAKE_FLASH_MS = 260L
    const val MISTAKE_VIBRATION_MS = 40L
}
