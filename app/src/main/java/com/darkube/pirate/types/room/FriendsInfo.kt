package com.darkube.pirate.types.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends_info")
data class FriendsInfo(
    @PrimaryKey
    @ColumnInfo(name = "pirate_id")
    val pirateId: String,
    @ColumnInfo(name = "first_name", defaultValue = "")
    val firstName: String,
    @ColumnInfo(name = "last_name", defaultValue = "")
    val lastName: String,
    @ColumnInfo(defaultValue = "")
    val username: String,
    @ColumnInfo(defaultValue = "12")
    val image: String,
)
