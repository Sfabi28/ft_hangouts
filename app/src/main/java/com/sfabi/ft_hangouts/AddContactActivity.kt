package com.sfabi.ft_hangouts

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddContactActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private var selectedImageUri: Uri? = null

    private lateinit var ivProfile: ImageButton

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            ivProfile.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        dbHelper = DatabaseHelper(this)

        ivProfile = findViewById(R.id.ivProfileImage)
        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)

        ivProfile.setOnClickListener {
            getContent.launch("image/*")
        }

        ThemeUtils.applyHeaderColor(this)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()
            val note = etNote.text.toString()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nome e Telefono sono obbligatori!", Toast.LENGTH_SHORT).show()
            } else {

                var imagePathToSave = ""

                if (selectedImageUri != null) {
                    val newPath = saveImageToInternalStorage(this, selectedImageUri!!)
                    if (newPath != null) {
                        imagePathToSave = newPath
                    }
                }

                val contact = Contact(0, name, phone, email, address, note, imagePathToSave)

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

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyHeaderColor(this)
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}