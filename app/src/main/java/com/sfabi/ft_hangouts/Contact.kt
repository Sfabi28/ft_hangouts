package com.sfabi.ft_hangouts

data class Contact(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val note: String
)