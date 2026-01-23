package com.sfabi.ft_hangouts
import com.sfabi.ft_hangouts.R

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View

object ThemeUtils {

    private const val PREF_NAME = "AppPreferences"
    private const val KEY_HEADER_COLOR = "header_color"

    fun saveHeaderColor(context: Context, colorHex: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HEADER_COLOR, colorHex).apply()
    }

    fun getHeaderColor(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HEADER_COLOR, "#FF000000") ?: "#FF000000"
    }

    fun applyHeaderColor(activity: Activity) {
        val headerBar = activity.findViewById<View>(R.id.header_bar)

        if (headerBar != null) {
            val colorString = getHeaderColor(activity)
            try {
                headerBar.setBackgroundColor(Color.parseColor(colorString))
            } catch (e: Exception) {
                headerBar.setBackgroundColor(Color.BLACK)
            }
        }
    }

    fun applyThemeToChat(activity: Activity) {
        val headerBar = activity.findViewById<View>(R.id.header_bar)
        val headerColorStr = getHeaderColor(activity)
        val headerColorInt = try { Color.parseColor(headerColorStr) } catch (e: Exception) { Color.BLACK }

        headerBar?.setBackgroundColor(headerColorInt)

        val contactInfoBar = activity.findViewById<View>(R.id.incContactInfo)

        if (contactInfoBar != null) {
            val r = Color.red(headerColorInt)
            val g = Color.green(headerColorInt)
            val b = Color.blue(headerColorInt)

            val newAlpha = 220
            val subBarColor = Color.argb(newAlpha, r, g, b)

            contactInfoBar.setBackgroundColor(subBarColor)
        }
    }
}