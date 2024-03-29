package com.example.scarletmaps.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.scarletmaps.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main.*

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val navController = findNavController(R.id.navigation_host)
        bottom_navigation.setupWithNavController(navController)

        setupViews()
    }

    private fun setupViews() {
        val navController = findNavController(R.id.navigation_host)
        bottom_navigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentRouteOpen -> {
                    //bottom_navigation.visibility = View.GONE
                }
                else -> bottom_navigation.visibility = View.VISIBLE
            }
        }
    }
}