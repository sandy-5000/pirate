package com.pirate.models.types

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
data class UserDetails(
    @PrimaryKey
    val key: String,
    val value: String,
)
