package com.pirate.models.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class Preferences(
    @PrimaryKey
    val key: String,
    val value: String,
)
