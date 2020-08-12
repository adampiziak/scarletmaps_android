package com.example.scarletmaps.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.scarletmaps.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        setupViews()
    }

    private fun setupViews() {
        val navController = findNavController(R.id.navigation_host)
        bottom_navigation.setupWithNavController(navController)

        /*
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.routeView -> {
                    bottom_navigation.windowInsetsController.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
                }
                else -> bottom_navigation.visibility = View.VISIBLE
            }
        }

         */
    }
}