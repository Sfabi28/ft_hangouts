package com.sfabi.ft_hangouts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.content.Context

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

        setupColorButton(R.id.btnColor1, "#FF0000") // Rosso
        setupColorButton(R.id.btnColor2, "#00FF00") // Verde
        setupColorButton(R.id.btnColor3, "#0000FF") // Blu
        setupColorButton(R.id.btnColor4, "#FFFF00") // Giallo
        setupColorButton(R.id.btnColor5, "#00FFFF") // Ciano
        setupColorButton(R.id.btnColor6, "#FF00FF") // Magenta
        setupColorButton(R.id.btnColor7, "#000000") // Nero
        setupColorButton(R.id.btnColor8, "#FFFFFF") // Bianco
        setUpLanguageButton(R.id.btnEn, "en")
        setUpLanguageButton(R.id.btnIt, "it")
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

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
    }
}