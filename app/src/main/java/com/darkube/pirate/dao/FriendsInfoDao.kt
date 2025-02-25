package com.darkube.pirate.dao

import androidx.room.Dao
import androidx.room.Query
import com.darkube.pirate.types.room.FriendsInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsInfoDao {
    @Query("INSERT OR REPLACE INTO friends_info (pirate_id, username, image) VALUES (:pirateId, :username, :image)")
    fun updateMainInfo(pirateId: String, username: String, image: String)

    @Query("UPDATE friends_info SET first_name = :firstName, last_name = :lastName WHERE pirate_id = :pirateId")
    fun updateNameInfo(pirateId: String, firstName: String, lastName: String)

    @Query(
        value = """
            INSERT OR REPLACE INTO friends_info (pirate_id, first_name, last_name, username, image)
            VALUES (:pirateId, :firstName, :lastName, :username, :image)
        """
    )
    fun updateAllInfo(
        pirateId: String,
        firstName: String,
        lastName: String,
        username: String,
        image: String
    )

    @Query("SELECT * FROM friends_info")
    fun getAll(): Flow<List<FriendsInfo>>

    @Query("DELETE FROM friends_info")
    suspend fun deleteAll()
}