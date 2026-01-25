package com.sfabi.ft_hangouts

data class Message(
    val id: Int,
    val phoneNumber: String,
    val body: String,
    val type: Int,
    val timestamp: String
)