package com.pirate.models.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends_info")
data class FriendsInfo(

    @PrimaryKey
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    @ColumnInfo(defaultValue = "")
    val name: String,
    @ColumnInfo(defaultValue = "")
    val username: String,
    @ColumnInfo(defaultValue = "LOCAL:12")
    val image: String,
    @ColumnInfo(name = "last_opened_at", defaultValue = "0")
    val lastOpenedAt: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val pinned: Int = 0,

    @ColumnInfo(name = "last_message_id")
    val lastMessageId: String = "",
    @ColumnInfo(name = "last_message", defaultValue = "")
    val lastMessage: String = "",
    @ColumnInfo(name = "last_message_status", defaultValue = "-1")
    val lastMessageStatus: Int = -1,
    @ColumnInfo(name = "last_message_type", defaultValue = "TXT")
    val lastMessageType: String = "TXT",
    @ColumnInfo(name = "received_at", defaultValue = "0")
    val receivedAt: Long = 0,

)
