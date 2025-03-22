package com.pirate.types

import com.pirate.models.types.UserChats

data class EventInfo(
    val type: EventType,
    val pirateId: String = "",
    val username: String = "",
    val userChats: UserChats?,
)
