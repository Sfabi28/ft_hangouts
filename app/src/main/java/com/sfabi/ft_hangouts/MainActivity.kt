package com.sfabi.ft_hangouts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Troviamo il bottone "+" che abbiamo messo nell'XML
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        // 2. Quando lo clicchi...
        fab.setOnClickListener {
            // ...Apri la pagina di aggiunta contatto
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }
    }
}