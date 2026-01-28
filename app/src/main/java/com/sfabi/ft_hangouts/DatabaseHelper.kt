package com.sfabi.ft_hangouts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.w3c.dom.Text

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 14
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

        const val TABLE_CHATS = "chats"
        const val COL_CHAT_ID = "id"
        const val COL_CHAT_PHONE = "phone_number"
        const val COL_CHAT_LAST_MSG = "last_message"
        const val COL_CHAT_TIME = "last_timestamp"
        const val COL_CHAT_UNREAD = "unread_count"

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

        val createChatsTable = ("CREATE TABLE $TABLE_CHATS ("
                + "$COL_CHAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_CHAT_PHONE TEXT UNIQUE, "
                + "$COL_CHAT_LAST_MSG TEXT, "
                + "$COL_CHAT_TIME TEXT, "
                + "$COL_CHAT_UNREAD INTEGER DEFAULT 0)")

        db.execSQL(createContactsTable)
        db.execSQL(createMessagesTable)
        db.execSQL(createChatsTable)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHATS")
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

        if (result != -1L) {
            val chatValues = android.content.ContentValues().apply {
                put(COL_CHAT_PHONE, contact.phone) // Chiave di collegamento
                put(COL_CHAT_LAST_MSG, "") // Messaggio vuoto
                put(COL_CHAT_TIME, System.currentTimeMillis().toString()) // Orario attuale
                put(COL_CHAT_UNREAD, 0)
            }

            db.insertWithOnConflict(TABLE_CHATS, null, chatValues, SQLiteDatabase.CONFLICT_IGNORE)
        }

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

    fun addMessage(number: String, text: String, type: Int) {
        val db = this.writableDatabase
        val time = System.currentTimeMillis().toString()

        // Insert the message
        val messageValues = android.content.ContentValues().apply {
            put(COL_MSG_PHONE, number)
            put(COL_MSG_BODY, text)
            put(COL_MSG_TYPE, type)
            put(COL_MSG_TIME, time)
        }
        db.insert(TABLE_MESSAGES, null, messageValues)

        // Prepare values for chat update
        val chatUpdateValues = android.content.ContentValues().apply {
            put(COL_CHAT_LAST_MSG, text)
            put(COL_CHAT_TIME, time)
        }

        // Try to update existing chat
        val rowsAffected = db.update(TABLE_CHATS, chatUpdateValues, "$COL_CHAT_PHONE = ?", arrayOf(number))

        // If it's a received message and chat was updated, increment unread count
        if (rowsAffected > 0 && type == 2) {
            db.execSQL("UPDATE $TABLE_CHATS SET $COL_CHAT_UNREAD = $COL_CHAT_UNREAD + 1 WHERE $COL_CHAT_PHONE = ?", arrayOf(number))
        }

        // If no chat was updated (i.e., it doesn't exist), create it
        if (rowsAffected == 0) {
            val newChatValues = android.content.ContentValues().apply {
                put(COL_CHAT_PHONE, number)
                put(COL_CHAT_LAST_MSG, text)
                put(COL_CHAT_TIME, time)
                put(COL_CHAT_UNREAD, if (type == 2) 1 else 0) // Set initial unread count
            }
            db.insert(TABLE_CHATS, null, newChatValues)
        }

        db.close()
    }

    fun getMessages(number: String): List<Message> {
        val messagesList = ArrayList<Message>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_MESSAGES WHERE $COL_MSG_PHONE = ? ORDER BY $COL_MSG_TIME ASC"

        val cursor = db.rawQuery(query, arrayOf(number))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_PHONE))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_BODY))
                val type = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_TYPE))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_TIME))

                messagesList.add(Message(id, phone, body, type, time))

            } while (cursor.moveToNext())
        }

        cursor.close()
        return messagesList
    }

    fun markChatAsRead(phoneNumber: String) {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put(COL_CHAT_UNREAD, 0)
        }
        db.update(TABLE_CHATS, values, "$COL_CHAT_PHONE = ?", arrayOf(phoneNumber))
    }
    fun getChatPreviews(): List<ChatPreview> {
        val previewList = ArrayList<ChatPreview>()
        val db = this.readableDatabase

        val query = """
            SELECT T1.$COL_CHAT_ID, T1.$COL_CHAT_PHONE, T1.$COL_CHAT_LAST_MSG, T1.$COL_CHAT_TIME, 
                   T2.$COL_NAME, T2.$COL_IMAGE, T1.$COL_CHAT_UNREAD 
            FROM $TABLE_CHATS T1 
            LEFT JOIN $TABLE_CONTACTS T2 ON T1.$COL_CHAT_PHONE = T2.$COL_PHONE
            ORDER BY T1.$COL_CHAT_TIME DESC 
        """

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0) // T1.id
                val phone = cursor.getString(1) // T1.phone
                val msg = cursor.getString(2) // T1.last_message
                val time = cursor.getString(3) // T1.time

                val name = cursor.getString(4) ?: phone
                val image = cursor.getString(5)

                val unreadCount = cursor.getInt(6)

                previewList.add(ChatPreview(id, name, phone, msg, time, image, unreadCount))

            } while (cursor.moveToNext())
        }
        cursor.close()
        return previewList
    }

    fun getContactByPhone(phoneNumber: String): Contact? {
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACTS WHERE $COL_PHONE = ?", arrayOf(phoneNumber))

        var contact: Contact? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
            val note = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE))

            contact = Contact(id, name, phone, email, address, note, imageUri)
        }

        cursor.close()
        return contact
    }

    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put(COL_NAME, contact.name)
            put(COL_PHONE, contact.phone)
            put(COL_EMAIL, contact.email)
            put(COL_ADDRESS, contact.address)
            put(COL_NOTE, contact.note)
            put(COL_IMAGE, contact.imageUri)
        }

        return db.update(TABLE_CONTACTS, values, "$COL_ID = ?", arrayOf(contact.id.toString()))
    }
    fun deleteChat(phoneNumber: String) {
        val db = this.writableDatabase
        db.delete(TABLE_CHATS, "$COL_CHAT_PHONE = ?", arrayOf(phoneNumber))
        db.delete(TABLE_MESSAGES, "$COL_MSG_PHONE = ?", arrayOf(phoneNumber))
        db.close()
    }

    // In DatabaseHelper.kt

    fun deleteContact(contactId: Int): Int {
        val db = this.writableDatabase
        var phone: String? = null

        val cursor = db.query(TABLE_CONTACTS, arrayOf(COL_PHONE), "$COL_ID = ?", arrayOf(contactId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE))
        }
        cursor.close()

        val deletedRows = db.delete(TABLE_CONTACTS, "$COL_ID = ?", arrayOf(contactId.toString()))

        if (deletedRows > 0 && phone != null) {
            deleteChat(phone) // Riusiamo la tua funzione esistente!
        }

        if (phone == null) {
            db.close()
        }

        return deletedRows
    }

}
