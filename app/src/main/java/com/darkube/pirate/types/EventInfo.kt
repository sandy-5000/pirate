package com.darkube.pirate.types

enum class EventType {
    MESSAGE, REQUEST_ACCEPTED, REQUEST_REJECTED
}

data class EventInfo(
    val type: EventType,
    val id: String = "",
    val username: String = "",
    val message: String = "",
)
