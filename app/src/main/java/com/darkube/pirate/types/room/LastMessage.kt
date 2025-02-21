package com.darkube.pirate.types.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_message")
data class LastMessage(
    @PrimaryKey
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    val username: String,
    val message: String,
    @ColumnInfo(name = "receive_time")
    val receiveTime: String,
)
