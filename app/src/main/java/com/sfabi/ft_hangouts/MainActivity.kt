package com.sfabi.ft_hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.View


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView

    private val PERMISSION_REQUEST_CODE = 100

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        listView = findViewById(R.id.listViewContacts)

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }

        if (!hasPermissions()) {
            requestPermissions()
        }

        val btnSettings = findViewById<View>(R.id.btnOptions)

        btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        ThemeUtils.applyHeaderColor(this)
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
        if (hasPermissions()) {
            loadChats()
        }
    }

    private fun hasPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                loadChats()
            } else {
                Toast.makeText(this, "Senza permessi l'app non può funzionare!", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun loadChats() {
        val chatList = dbHelper.getChatPreviews()

        val adapter = ChatAdapter(this, chatList)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedChat = chatList[position]

            val intent = Intent(this, ChatActivity::class.java)

            intent.putExtra("key_name", selectedChat.contactName)
            intent.putExtra("key_phone", selectedChat.phoneNumber)

            startActivity(intent)
        }
    }
}