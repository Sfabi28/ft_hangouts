package com.sfabi.ft_hangouts

data class ChatPreview(
    val chatId: Int,
    val contactName: String,
    val phoneNumber: String,
    val lastMessage: String,
    val timestamp: String,
    val imageUri: String?
)