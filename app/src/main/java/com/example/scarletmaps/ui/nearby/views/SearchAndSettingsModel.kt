package com.example.scarletmaps.ui.nearby.views

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R

@EpoxyModelClass(layout = R.layout.search_and_settings)
abstract class SearchAndSettingsModel: EpoxyModelWithHolder<SearchAndSettingsModel.Holder>() {
    class Holder: EpoxyHolder() {
        override fun bindView(itemView: View) {

        }
    }
}