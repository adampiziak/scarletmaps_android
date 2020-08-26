package com.example.scarletmaps.ui

import android.Manifest
import android.app.Activity
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.scarletmaps.R
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main.*
import javax.inject.Inject

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val navController = findNavController(R.id.navigation_host)
        bottom_navigation.setupWithNavController(navController)

        requestForegroundPermissions(this)
    }

    private fun requestForegroundPermissions(context: Activity) {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE)
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