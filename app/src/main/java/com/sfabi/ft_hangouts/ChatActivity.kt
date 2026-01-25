package com.sfabi.ft_hangouts

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import android.telephony.SmsManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {

    private var currentLanguageCode: String? = null
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var contactNumber: String
    private var contactName: String? = null
    private lateinit var btnBack: ImageButton

    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: android.widget.ImageButton

    private lateinit var messageAdapter: MessageAdapter
    private var messageList = ArrayList<Message>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.onAttach(newBase))
    }

    private val chatUpdateReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadMessages()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        currentLanguageCode = LanguageUtils.getLanguage(this)

        dbHelper = DatabaseHelper(this)

        rvChat = findViewById(R.id.rvChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        btnBack = findViewById(R.id.btnBack)

        val tvHeaderName = findViewById<android.widget.TextView>(R.id.tvContactName)
        val imgHeaderAvatar = findViewById<android.widget.ImageView>(R.id.imgAvatar)

        contactNumber = intent.getStringExtra("key_phone") ?: ""
        if (contactNumber.isEmpty()) {
            finish()
            return
        }

        val btnSettings = findViewById<View>(R.id.btnOptions)

        btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val contact = dbHelper.getContactByPhone(contactNumber)

        if (contact != null) {
            contactName = contact.name
            tvHeaderName.text = contactName

            val imagePath = contact.imageUri

            if (!imagePath.isNullOrEmpty()) {
                try {
                    val file = java.io.File(imagePath)
                    if (file.exists()) {
                        imgHeaderAvatar.setImageURI(android.net.Uri.fromFile(file))
                    } else {
                        val uri = android.net.Uri.parse(imagePath)
                        if (uri.scheme == "content") {
                            try {
                                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } catch (e: Exception) {
                            }
                        }
                        imgHeaderAvatar.setImageURI(uri)
                    }
                } catch (e: Exception) {
                    imgHeaderAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            } else {
                imgHeaderAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        } else {
            tvHeaderName.text = contactNumber
            imgHeaderAvatar.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        setupRecyclerView()

        btnBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            sendMessage()
        }

        ThemeUtils.applyHeaderColor(this)
    }

    override fun onResume() {
        super.onResume()

        val savedLanguage = LanguageUtils.getLanguage(this)
        if (currentLanguageCode != null && currentLanguageCode != savedLanguage) {
            recreate()
        } else {
            ThemeUtils.applyHeaderColor(this)

            val filter = android.content.IntentFilter("com.sfabi.ft_hangouts.UPDATE_CHAT")

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(chatUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(chatUpdateReceiver, filter)
            }

            loadMessages()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(chatUpdateReceiver)
        } catch (e: Exception) {
        }
    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvChat.layoutManager = layoutManager

        messageAdapter = MessageAdapter(this, messageList)
        rvChat.adapter = messageAdapter

        loadMessages()
    }

    private fun loadMessages() {
        messageList = dbHelper.getMessages(contactNumber) as ArrayList<Message>

        messageAdapter.updateMessages(messageList)

        if (messageList.isNotEmpty()) {
            rvChat.scrollToPosition(messageList.size - 1)
        }
    }

    private fun sendMessage() {
        val smsManager: android.telephony.SmsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            this.getSystemService(android.telephony.SmsManager::class.java)
        } else {
            android.telephony.SmsManager.getDefault()
        }

        val text = etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            dbHelper.addMessage(contactNumber, text, 1)

            smsManager.sendTextMessage(contactNumber, null, text, null, null)

            etMessage.setText("")
            loadMessages()
        }
    }
}