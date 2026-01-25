package com.sfabi.ft_hangouts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            if (messages != null) {
                val dbHelper = DatabaseHelper(context)

                for (sms in messages) {
                    val rawSender = sms.displayOriginatingAddress ?: "Sconosciuto"
                    val messageBody = sms.messageBody ?: ""

                    var finalSender = rawSender

                    if (dbHelper.getContactByPhone(rawSender) == null) {

                        if (rawSender.startsWith("+39")) {
                            val numberWithoutPrefix = rawSender.substring(3)

                            if (dbHelper.getContactByPhone(numberWithoutPrefix) != null) {
                                finalSender = numberWithoutPrefix
                            }
                        }
                    }
                    dbHelper.addMessage(finalSender, messageBody, 2)

                    val updateIntent = Intent("com.sfabi.ft_hangouts.UPDATE_CHAT")
                    context.sendBroadcast(updateIntent)
                }
            }
        }
    }
}