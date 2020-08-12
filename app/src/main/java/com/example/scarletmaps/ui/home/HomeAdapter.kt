package com.example.scarletmaps.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.scarletmaps.ui.nearby.Nearby
import com.example.scarletmaps.ui.routelist.RouteList
import com.example.scarletmaps.ui.stoplist.StopList

class HomeAdapter(f: Fragment) : FragmentStateAdapter(f) {

    override fun getItemCount(): Int = Companion.NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return Nearby()
        }

        if (position == 1) {
            return RouteList()
        }

        return StopList()

    }

    companion object {
        private const val NUM_PAGES = 3
    }

}