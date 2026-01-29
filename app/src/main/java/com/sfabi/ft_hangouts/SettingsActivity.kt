package com.sfabi.ft_hangouts

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        ThemeUtils.applyHeaderColor(this)

        val btnBack = findViewById<View>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }

        setupColorButton(R.id.btnColor1, "#E57373")
        setupColorButton(R.id.btnColor2, "#81C784")
        setupColorButton(R.id.btnColor3, "#64B5F6")
        setupColorButton(R.id.btnColor4, "#FFD54F")
        setupColorButton(R.id.btnColor5, "#4DD0E1")
        setupColorButton(R.id.btnColor6, "#BA68C8")
        setupColorButton(R.id.btnColor7, "#222222")
        setupColorButton(R.id.btnColor8, "#E0E0E0")

        setUpLanguageButton(R.id.btnEn, "en")
        setUpLanguageButton(R.id.btnIt, "it")

        val btnSystem = findViewById<View>(R.id.btnSystem)
        btnSystem.setOnClickListener {
            LanguageUtils.clearLanguage(this)
            recreate()
        }

        updateLanguageUI()
    }
    private fun setupColorButton(btnId: Int, colorHex: String) {
        val button = findViewById<View>(btnId)
        button.setOnClickListener {
            ThemeUtils.saveHeaderColor(this, colorHex)
            ThemeUtils.applyHeaderColor(this)
        }
    }

    private fun setUpLanguageButton(btnId: Int, language: String) {
        val button = findViewById<View>(btnId)
        button.setOnClickListener {
            LanguageUtils.saveLanguage(this, language)

            recreate()
        }
    }

    private fun updateLanguageUI() {
        val savedLang = LanguageUtils.getLanguage(this)

        val btnEn = findViewById<View>(R.id.btnEn)
        val btnIt = findViewById<View>(R.id.btnIt)
        val btnSystem = findViewById<View>(R.id.btnSystem)

        btnEn.alpha = 1.0f; btnEn.isEnabled = true
        btnIt.alpha = 1.0f; btnIt.isEnabled = true
        btnSystem.alpha = 1.0f; btnSystem.isEnabled = true

        if (savedLang == null) {
            btnSystem.alpha = 0.5f
            btnSystem.isEnabled = false
        } else if (savedLang == "it") {
            btnIt.alpha = 0.5f
            btnIt.isEnabled = false
        } else {
            btnEn.alpha = 0.5f
            btnEn.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
        updateLanguageUI()
    }
}