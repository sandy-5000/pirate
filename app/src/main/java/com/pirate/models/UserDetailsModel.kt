package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pirate.models.types.UserDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailsModel {

    @Upsert
    suspend fun update(detail: UserDetails)

    @Query("SELECT * from user_details where `key` = :key")
    fun key(key: String): UserDetails?

    @Query("SELECT * from user_details ORDER BY `key`")
    fun getAll(): Flow<List<UserDetails>>

    @Query("DELETE from user_details where `key` = :key")
    suspend fun delete(key: String)

    @Query(value = "DELETE from user_details")
    suspend fun deleteAll()

}
