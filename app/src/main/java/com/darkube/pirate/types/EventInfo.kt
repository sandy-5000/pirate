package com.darkube.pirate.types

data class EventInfo(
    val type: EventType,
    val id: String = "",
    val username: String = "",
    val message: String = "",
)
