package com.sfabi.ft_hangouts

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView

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
    }

    override fun onResume() {
        super.onResume()
        loadChats()
    }

    private fun loadChats() {
        val chatList = dbHelper.getChatPreviews()

        val adapter = ChatAdapter(this, chatList)

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedChat = chatList[position]

            android.widget.Toast.makeText(this, "Apro chat con ${selectedChat.contactName}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}