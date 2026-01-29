package com.sfabi.ft_hangouts

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ChatAdapter
    private var allChats: List<ChatPreview> = ArrayList()
    private var displayedChats: List<ChatPreview> = ArrayList()

    private val PERMISSION_REQUEST_CODE = 100

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.CALL_PHONE
    )

    private var currentLanguageCode: String? = null

    private val updateChatReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.sfabi.ft_hangouts.UPDATE_CHAT") {
                loadChats()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentLanguageCode = LanguageUtils.getLanguage(this)
        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listViewContacts)

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterChats(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        ThemeUtils.applyHeaderColor(this)

        val filter = IntentFilter("com.sfabi.ft_hangouts.UPDATE_CHAT")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateChatReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(updateChatReceiver, filter)
        }

        setupListViewListeners()
    }

    override fun onResume() {
        super.onResume()
        val savedLanguage = LanguageUtils.getLanguage(this)

        if (currentLanguageCode != null && currentLanguageCode != savedLanguage) {
            recreate()
        } else {
            ThemeUtils.applyHeaderColor(this)
            if (hasPermissions()) {
                loadChats()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(updateChatReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
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
                Toast.makeText(this, getString(R.string.permission_toast), Toast.LENGTH_LONG).show()
            }
        }
    }

    public fun loadChats() {
        allChats = dbHelper.getChatPreviews()

        displayedChats = ArrayList(allChats)

        updateAdapter()
    }

    private fun filterChats(query: String) {
        val searchText = query.lowercase().trim()

        displayedChats = if (searchText.isEmpty()) {
            ArrayList(allChats)
        } else {
            allChats.filter { chat ->
                val nameMatch = chat.contactName.lowercase().contains(searchText)
                val phoneMatch = chat.phoneNumber.contains(searchText)
                nameMatch || phoneMatch
            }
        }
        updateAdapter()
    }

    private fun updateAdapter() {
        adapter = ChatAdapter(this, displayedChats)
        listView.adapter = adapter
    }

    private fun setupListViewListeners() {
        listView.setOnItemClickListener { parent, _, position, _ ->
            val selectedChat = parent.adapter.getItem(position) as ChatPreview

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("key_name", selectedChat.contactName)
            intent.putExtra("key_phone", selectedChat.phoneNumber)
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { parent, _, position, _ ->
            if (!hasPermissions()) {
                Toast.makeText(this, getString(R.string.permission_toast), Toast.LENGTH_LONG).show()
            } else {
                val selectedChat = parent.adapter.getItem(position) as ChatPreview
                showDeleteDialog(selectedChat)
            }
            true
        }
    }
    private fun showDeleteDialog(chat: ChatPreview) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_title));
        val nameToShow = if(chat.contactName.isNotEmpty()) chat.contactName else chat.phoneNumber
        builder.setMessage("${getString(R.string.delete_confirm)} $nameToShow?")

        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            deleteContactAndChat(chat)
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun deleteContactAndChat(chat: ChatPreview) {
        val deletedRows = dbHelper.deleteContact(chat.chatId)

        if (deletedRows > 0) {
            Toast.makeText(this, getString(R.string.deleted_success), Toast.LENGTH_SHORT).show()
        }

        loadChats()

        val etSearch = findViewById<EditText>(R.id.etSearch)
        if (etSearch.text.isNotEmpty()) {
            filterChats(etSearch.text.toString())
        }
    }

}