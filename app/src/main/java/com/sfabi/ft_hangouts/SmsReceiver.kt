package com.sfabi.ft_hangouts

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

                for (sms in messages) {
                    var rawSender = sms.displayOriginatingAddress ?: "Sconosciuto"
                    val messageBody = sms.messageBody ?: ""

                    val allContacts = dbHelper.getAllContacts()
                    val matchingContact = allContacts.firstOrNull { contact ->
                        val cleanRawSender = rawSender.filter { it.isDigit() }
                        val cleanContactPhone = contact.phone.filter { it.isDigit() }
                        cleanRawSender.endsWith(cleanContactPhone) || cleanContactPhone.endsWith(cleanRawSender)
                    }

                    val finalSender = if (matchingContact != null) {
                        matchingContact.phone
                    } else {
                        if (rawSender.startsWith("+39")) {
                            rawSender.substring(3)
                        } else {
                            rawSender
                        }
                    }

                    dbHelper.addMessage(finalSender, messageBody, 2)

                    val updateIntent = Intent("com.sfabi.ft_hangouts.UPDATE_CHAT")
                    updateIntent.setPackage(context.packageName)
                    context.sendBroadcast(updateIntent)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
