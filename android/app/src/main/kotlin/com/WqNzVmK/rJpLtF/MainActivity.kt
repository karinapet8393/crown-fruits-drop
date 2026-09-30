package com.WqNzVmK.rJpLtF

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.WqNzVmK.rJpLtF.core.di.ServiceLocator
import com.WqNzVmK.rJpLtF.databinding.ActivityMainBinding
import com.WqNzVmK.rJpLtF.presentation.splash.SplashFragment

/**
 * The only Activity of Crown Fruits Drop. Screens are Fragments inside
 * R.id.fragment_container: Splash -> Menu -> Game -> Result -> Menu.
 */
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)

        val inflated = ActivityMainBinding.inflate(layoutInflater)
        binding = inflated
        setContentView(inflated.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SplashFragment())
                .commit()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}
