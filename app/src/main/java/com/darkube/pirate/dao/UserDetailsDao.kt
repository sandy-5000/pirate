package com.darkube.pirate.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.darkube.pirate.types.room.UserDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailsDao {
    @Upsert
    suspend fun update(detail: UserDetails)

    @Query("DELETE from user_details")
    suspend fun deleteAll()

    @Query("DELETE from user_details where `key` = :key")
    suspend fun delete(key: String)

    @Query("SELECT * from user_details ORDER BY `key`")
    fun getAll(): Flow<List<UserDetails>>

    @Query("SELECT * from user_details where `key` = :key")
    fun get(key: String): Flow<UserDetails?>
}
