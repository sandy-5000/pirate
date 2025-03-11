package com.pirate.models.types

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_messages")
data class ScheduledMessages(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    val scheduleId: String,
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    val message: String,
    @ColumnInfo(name = "send_at")
    val sendAt: String,
)
