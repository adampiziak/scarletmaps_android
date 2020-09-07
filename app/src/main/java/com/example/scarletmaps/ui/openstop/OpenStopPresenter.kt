package com.example.scarletmaps.ui.openstop

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.scarletmaps.R
import javax.inject.Singleton

@Singleton
class OpenStopPresenter() {
    fun navigate(v: View, id: Int) {
        v.findNavController().navigate(R.id.fragmentRouteOpen, bundleOf("id" to id))
    }
}