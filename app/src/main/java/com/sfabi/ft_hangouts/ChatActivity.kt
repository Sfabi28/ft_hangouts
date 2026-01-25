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
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // I messaggi partono dal basso
        rvChat.layoutManager = layoutManager

        // Creiamo l'adapter
        messageAdapter = MessageAdapter(this, messageList)
        rvChat.adapter = messageAdapter

        // Carichiamo i dati
        loadMessages()
    }

    private fun loadMessages() {
        // Chiamiamo la funzione del DB che hai creato poco fa
        messageList = dbHelper.getMessages(contactNumber) as ArrayList<Message>

        // Aggiorniamo l'adapter
        messageAdapter.updateMessages(messageList)

        // Scorriamo in fondo se ci sono messaggi
        if (messageList.isNotEmpty()) {
            rvChat.scrollToPosition(messageList.size - 1)
        }
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            // 1 = Messaggio inviato da me
            dbHelper.addMessage(contactNumber, text, 1)

            etMessage.setText("") // Pulisci campo
            loadMessages() // Ricarica la lista per vedere la nuova bolla
        }
    }
}