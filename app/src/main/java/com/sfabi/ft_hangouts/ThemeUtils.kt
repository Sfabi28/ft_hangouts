package com.sfabi.ft_hangouts

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

object ThemeUtils {

    private const val PREF_NAME = "AppPreferences"
    private const val KEY_HEADER_COLOR = "header_color"

    private const val DEFAULT_COLOR = "#FFD54F"

    fun saveHeaderColor(context: Context, colorHex: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HEADER_COLOR, colorHex).apply()
    }

    fun getHeaderColor(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HEADER_COLOR, DEFAULT_COLOR) ?: DEFAULT_COLOR
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    fun applyHeaderColor(activity: Activity) {
        val colorString = getHeaderColor(activity)
        val backgroundColorInt = try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            Color.parseColor(DEFAULT_COLOR)
        }

        val textColorInt = if (isColorDark(backgroundColorInt)) Color.WHITE else Color.BLACK

        activity.findViewById<View>(R.id.header_bar)?.setBackgroundColor(backgroundColorInt)

        activity.findViewById<View>(R.id.searching_bar)?.setBackgroundColor(backgroundColorInt)

        activity.findViewById<View>(R.id.incContactInfo)?.setBackgroundColor(backgroundColorInt)

        val titleText = activity.findViewById<TextView>(R.id.header_title)
        titleText?.setTextColor(textColorInt)

        val btnBack = activity.findViewById<ImageButton>(R.id.btnBack)
        btnBack?.setColorFilter(textColorInt)

        val btnOptions = activity.findViewById<ImageButton>(R.id.btnOptions)
        btnOptions?.setColorFilter(textColorInt)

        val btnCall = activity.findViewById<ImageButton>(R.id.btnCall)
        btnCall?.setColorFilter(textColorInt)

        val contactName = activity.findViewById<TextView>(R.id.tvContactName)
        contactName?.setTextColor(textColorInt)

        val fab = activity.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd)
        if (fab != null) {
            fab.backgroundTintList = android.content.res.ColorStateList.valueOf(backgroundColorInt)
            fab.setColorFilter(textColorInt)
        }

        val btnSave = activity.findViewById<android.widget.Button>(R.id.btnSave)
        if (btnSave != null) {
            btnSave.backgroundTintList = android.content.res.ColorStateList.valueOf(backgroundColorInt)
            btnSave.setTextColor(textColorInt)
        }

        val btnEn = activity.findViewById<android.widget.Button>(R.id.btnEn)
        if (btnEn != null) {
            btnEn.backgroundTintList = android.content.res.ColorStateList.valueOf(backgroundColorInt)
            btnEn.setTextColor(textColorInt)
        }

        val btnIt = activity.findViewById<android.widget.Button>(R.id.btnIt)
        if (btnIt != null) {
            btnIt.backgroundTintList = android.content.res.ColorStateList.valueOf(backgroundColorInt)
            btnIt.setTextColor(textColorInt)
        }
    }
}