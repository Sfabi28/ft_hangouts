package com.sfabi.ft_hangouts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_CONTACTS = "contacts"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_PHONE = "phone"
        const val COL_EMAIL = "email"
        const val COL_ADDRESS = "address"
        const val COL_NOTE = "note"
        const val COL_IMAGE = "image"

        const val TABLE_MESSAGES = "messages"
        const val COL_MSG_ID = "id"
        const val COL_MSG_PHONE = "phone_number"
        const val COL_MSG_BODY = "body"
        const val COL_MSG_TIME = "timestamp"
        const val COL_MSG_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createContactsTable = ("CREATE TABLE $TABLE_CONTACTS ("
                + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_NAME TEXT, "
                + "$COL_PHONE TEXT, "
                + "$COL_EMAIL TEXT, "
                + "$COL_ADDRESS TEXT, "
                + "$COL_NOTE TEXT, "
                + "$COL_IMAGE TEXT)")


        val createMessagesTable = ("CREATE TABLE $TABLE_MESSAGES ("
                + "$COL_MSG_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_MSG_PHONE TEXT, "
                + "$COL_MSG_BODY TEXT, "
                + "$COL_MSG_TYPE INTEGER, "
                + "$COL_MSG_TIME TEXT)")

        db.execSQL(createContactsTable)
        db.execSQL(createMessagesTable)

    }

    //DA RIGUARDARE LA PARTE DEL DROP forse devo usare ALTER
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    fun addContact(contact: Contact): Long {
        val db = this.writableDatabase

        val values = android.content.ContentValues().apply {
            put(COL_NAME, contact.name)
            put(COL_PHONE, contact.phone)
            put(COL_EMAIL, contact.email)
            put(COL_ADDRESS, contact.address)
            put(COL_NOTE, contact.note)
            put(COL_IMAGE, contact.imageUri)
        }

        val result = db.insert(TABLE_CONTACTS, null, values)

        db.close()

        return result
    }

    fun getAllContacts(): List<Contact> {
        val contactList = ArrayList<Contact>()
        
        val db = this.readableDatabase
        
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {

                val idIndex = cursor.getColumnIndex(COL_ID)
                val nameIndex = cursor.getColumnIndex(COL_NAME)
                val phoneIndex = cursor.getColumnIndex(COL_PHONE)
                val emailIndex = cursor.getColumnIndex(COL_EMAIL)
                val addressIndex = cursor.getColumnIndex(COL_ADDRESS)
                val noteIndex = cursor.getColumnIndex(COL_NOTE)
                val imageIndex = cursor.getColumnIndex(COL_IMAGE)

                if (idIndex != -1 && nameIndex != -1 && phoneIndex != -1) {
                    
                    val id = cursor.getInt(idIndex)
                    val name = cursor.getString(nameIndex)
                    val phone = cursor.getString(phoneIndex)
                    val email = cursor.getString(emailIndex)
                    val address = cursor.getString(addressIndex)
                    val note = cursor.getString(noteIndex)
                    val imageUri = cursor.getString(imageIndex)

                    val contact = Contact(id, name, phone, email, address, note, imageUri)
                    
                    contactList.add(contact)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        
        return contactList
    }

}

