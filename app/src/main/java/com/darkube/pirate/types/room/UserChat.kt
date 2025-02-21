package com.darkube.pirate.types.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_chats",
    indices = [Index(value = ["pirate_id", "received_at"])]
)
data class UserChat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    @ColumnInfo(name = "received_at")
    val receivedAt: String,
    val message: String,
    val type: String,
    val side: Int,
)
