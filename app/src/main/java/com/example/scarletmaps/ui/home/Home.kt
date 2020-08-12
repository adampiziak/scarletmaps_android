package com.example.scarletmaps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.scarletmaps.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Home : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.home, container, false)

        val viewPager = v.findViewById<ViewPager2>(R.id.home_viewpager)
        val pagerAdapter = HomeAdapter(this)
        viewPager.adapter = pagerAdapter

        return v
    }
}