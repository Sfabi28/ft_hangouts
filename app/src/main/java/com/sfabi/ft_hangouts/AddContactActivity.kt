package com.sfabi.ft_hangouts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: String? = null

    private val getContent = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            
            val ivProfile = findViewById<android.widget.ImageButton>(R.id.ivProfileImage)
            ivProfile.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        dbHelper = DatabaseHelper(this)

        val ivProfile = findViewById<android.widget.ImageButton>(R.id.ivProfileImage)

        ivProfile.setOnClickListener {
            getContent.launch("image/*")
        }

        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()
            val note = etNote.text.toString()
            

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nome e Telefono sono obbligatori!", Toast.LENGTH_SHORT).show()
            } else {
                val contact = Contact(0, name, phone, email, address, note, selectedImageUri)

                val result = dbHelper.addContact(contact)

                if (result > -1) {
                    Toast.makeText(this, "Contatto salvato!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}