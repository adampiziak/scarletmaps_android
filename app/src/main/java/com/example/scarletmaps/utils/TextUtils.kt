package com.example.scarletmaps.utils

import javax.inject.Singleton

@Singleton
class TextUtils {
     fun capitalizeWords(words: String): String {
        return words.split(" ").joinToString(" ") { it.capitalize() }
    }
}