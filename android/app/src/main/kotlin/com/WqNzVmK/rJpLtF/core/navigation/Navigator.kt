package com.WqNzVmK.rJpLtF.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.WqNzVmK.rJpLtF.R

/**
 * Every screen change of the single-activity app goes through here so the custom
 * fade / slide animations stay consistent. The splash is replaced without a back
 * stack entry, so it can never be returned to.
 */
object Navigator {

    fun replaceWithFade(manager: FragmentManager, fragment: Fragment) {
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun push(manager: FragmentManager, fragment: Fragment) {
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right,
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun back(manager: FragmentManager, fragment: Fragment) {
        manager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
