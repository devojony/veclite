package com.devo.veclite.entity

data class Message(
    val isSelf: Boolean = false,
    var content: String,
    val id: Long? = 0
)
