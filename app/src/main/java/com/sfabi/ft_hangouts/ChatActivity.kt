package com.sfabi.ft_hangouts

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ListView
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var contactNumber: String
    private var contactName: String? = null
    private lateinit var btnBack: ImageButton

    private lateinit var lvMessages: ListView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: android.widget.ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        dbHelper = DatabaseHelper(this)

        lvMessages = findViewById(R.id.lvMessages)
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

        btnBack.setOnClickListener {
            finish()
        }

        ThemeUtils.applyHeaderColor(this)
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
    }
}