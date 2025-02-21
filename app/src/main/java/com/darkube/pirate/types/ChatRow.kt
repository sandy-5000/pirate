package com.darkube.pirate.types

data class ChatRow(
    val pirateId: String,
    val username: String,
    val message: String,
    val receiveTime: String,
    val image: String,
)
