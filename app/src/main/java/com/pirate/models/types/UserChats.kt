package com.pirate.models.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pirate.types.MessageType

@Entity(
    tableName = "user_chats",
    indices = [Index(value = ["pirate_id", "received_at"])]
)
data class UserChats(

    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    val message: String,
    @ColumnInfo(name = "message_status", defaultValue = "-1")
    val messageStatus: Int = -1,
    @ColumnInfo(name = "message_type", defaultValue = "TXT")
    val messageType: String = MessageType.TEXT.value,
    @ColumnInfo(name = "file_path", defaultValue = "")
    val filePath: String = "",
    @ColumnInfo(name = "received_at")
    val receivedAt: String,
    val side: Int,

    @ColumnInfo(name = "referred_id", defaultValue = "")
    val referredId: String = "",
    @ColumnInfo(name = "referred_message", defaultValue = "")
    val referredMessage: String = "",
    @ColumnInfo(name = "referred_message_type", defaultValue = "TXT")
    val referredMessageType: String = "",

)
