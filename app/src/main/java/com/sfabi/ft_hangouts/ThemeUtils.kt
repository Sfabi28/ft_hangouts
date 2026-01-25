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
        val colorString = getHeaderColor(activity)
        val colorInt = try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            Color.BLACK
        }

        val headerBar = activity.findViewById<View>(R.id.header_bar)
        headerBar?.setBackgroundColor(colorInt)

        val searchBarContainer = activity.findViewById<View>(R.id.searching_bar)
        searchBarContainer?.setBackgroundColor(colorInt)

        val contactInfo = activity.findViewById<View>(R.id.incContactInfo)
        contactInfo?.setBackgroundColor(colorInt)
    }
}