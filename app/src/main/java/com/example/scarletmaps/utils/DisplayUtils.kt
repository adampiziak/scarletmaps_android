package com.example.scarletmaps.utils

import android.content.res.Resources

object DisplayUtils {
    fun dpToPx(dp: Int): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }
}