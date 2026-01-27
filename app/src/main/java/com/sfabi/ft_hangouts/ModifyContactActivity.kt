package com.sfabi.ft_hangouts

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ModifyContactActivity : AppCompatActivity() {

    private var currentLanguageCode: String? = null
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var ivProfile: ImageButton

    private var contactId: Int = -1
    private var existingImagePath: String = ""

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            ivProfile.setImageURI(uri)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_contact)

        dbHelper = DatabaseHelper(this)
        ivProfile = findViewById(R.id.ivProfileImage)

        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        contactId = intent.getIntExtra("CONTACT_ID", -1)

        if (contactId == -1) {
            btnDelete.visibility = View.GONE
            etPhone.setText(intent.getStringExtra("CONTACT_PHONE"))
        } else {
            btnDelete.visibility = View.VISIBLE
            etName.setText(intent.getStringExtra("CONTACT_NAME"))
            etPhone.setText(intent.getStringExtra("CONTACT_PHONE"))
            etEmail.setText(intent.getStringExtra("CONTACT_EMAIL"))
            etAddress.setText(intent.getStringExtra("CONTACT_ADDRESS"))
            etNote.setText(intent.getStringExtra("CONTACT_NOTE"))
            existingImagePath = intent.getStringExtra("CONTACT_IMAGE") ?: ""

            if (existingImagePath.isNotEmpty()) {
                val imageFile = File(existingImagePath)
                if (imageFile.exists()) {
                    ivProfile.setImageURI(Uri.fromFile(imageFile))
                }
            }
        }

        ivProfile.setOnClickListener { getContent.launch("image/*") }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhone.text.toString()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, getString(R.string.info_toast), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imagePathToSave = if (selectedImageUri != null) {
                saveImageToInternalStorage(this, selectedImageUri!!) ?: existingImagePath
            } else {
                existingImagePath
            }

            val contact = Contact(
                id = contactId,
                name = name,
                phone = phone,
                email = etEmail.text.toString(),
                address = etAddress.text.toString(),
                note = etNote.text.toString(),
                imageUri = imagePathToSave
            )

            var success = false

            if (contactId == -1) {
                val newId = dbHelper.addContact(contact)
                success = newId > -1
            } else {
                val rowsAffected = dbHelper.updateContact(contact)
                success = rowsAffected > 0
            }

            if (success) {
                Toast.makeText(this, getString(R.string.good_toast), Toast.LENGTH_SHORT).show()

                val updateIntent = Intent("com.sfabi.ft_hangouts.UPDATE_CHAT")
                updateIntent.setPackage(packageName)
                sendBroadcast(updateIntent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.bad_toast), Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            val originalPhone = intent.getStringExtra("CONTACT_PHONE") ?: ""

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->

                    if (originalPhone.isNotEmpty()) {
                        dbHelper.deleteChat(originalPhone)
                    }

                    val deletedRows = dbHelper.deleteContact(contactId)

                    if (deletedRows > 0) {
                        Toast.makeText(this, getString(R.string.deleted_success), Toast.LENGTH_SHORT).show()

                        val updateIntent = Intent("com.sfabi.ft_hangouts.UPDATE_CHAT")
                        updateIntent.setPackage(packageName)
                        sendBroadcast(updateIntent)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        finish()
                    }
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
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
