package com.pirate.types

enum class FriendType(val value: String) {
    SELF("SELF"),
    FRIENDS("FRIENDS"),
    NOT_FRIENDS("NOT_FRIENDS"),
    REQUEST_SENT("REQUEST_SENT"),
    REQUEST_RECEIVED("REQUEST_RECEIVED"),
    SENDER_BLOCKED("SENDER_BLOCKED"),
    RECEIVER_BLOCKED("RECEIVER_BLOCKED"),
    INVALID("INVALID")
}
