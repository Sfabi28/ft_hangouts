package com.sfabi.ft_hangouts

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LanguageUtils {

    private const val PREF_NAME = "AppPreferences"
    private const val KEY_LANGUAGE = "app_language"

    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun onAttach(context: Context): Context {
        val lang = getLanguage(context)
        return setAppLocale(context, lang)
    }

    private fun setAppLocale(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}