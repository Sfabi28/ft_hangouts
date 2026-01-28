package com.sfabi.ft_hangouts

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val dbHelper = DatabaseHelper(context)

                var message = ""

                for (sms in messages) {
                    message += sms.messageBody ?: ""
                }

                val rawSender = messages[0].displayOriginatingAddress ?: "Sconosciuto"

                val allContacts = dbHelper.getAllContacts()
                val matchingContact = allContacts.firstOrNull { contact ->
                    val cleanRawSender = rawSender.filter { it.isDigit() }
                    val cleanContactPhone = contact.phone.filter { it.isDigit() }

                    if (cleanRawSender.isNotEmpty() && cleanContactPhone.isNotEmpty()) {
                        cleanRawSender.endsWith(cleanContactPhone) || cleanContactPhone.endsWith(cleanRawSender)
                    } else {
                        false
                    }
                }

                var finalSender : String
                if (matchingContact != null) {
                    finalSender = matchingContact.phone
                } else {
                    if (rawSender.startsWith("+39")) {
                        finalSender = rawSender.substring(3)
                    } else {
                        finalSender = rawSender
                    }
                    dbHelper.addContact(Contact(0, finalSender, finalSender, "", "", "", ""))
                }

                dbHelper.addMessage(finalSender, message, 2)

                val updateIntent = Intent("com.sfabi.ft_hangouts.UPDATE_CHAT")
                updateIntent.setPackage(context.packageName)
                context.sendBroadcast(updateIntent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
