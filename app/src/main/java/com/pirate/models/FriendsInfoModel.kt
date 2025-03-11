package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pirate.models.types.FriendsInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsInfoModel {

    @Upsert
    suspend fun upsert(friendsInfo: FriendsInfo)

    @Query(
        value = """
        UPDATE friends_info
        SET
            name = :name,
            username = :username,
            image = :image
        WHERE
            pirate_id = :pirateId
        """
    )
    suspend fun update(
        pirateId: String,
        name: String,
        username: String,
        image: String,
    )

    @Query(
        value = """
        UPDATE friends_info
        SET
            last_opened_at = CURRENT_TIMESTAMP
        WHERE
            pirate_id = :pirateId
        """
    )
    suspend fun updateLastOpened(pirateId: String)

    @Query(
        value = """
        UPDATE friends_info
        SET
            pinned = :pinned
        WHERE
            pirate_id = :pirateId
        """
    )
    suspend fun setPinned(pirateId: String, pinned: Int)

    @Query(value = "SELECT * FROM friends_info")
    fun getAll(): Flow<List<FriendsInfo>>

    @Query(value = "DELETE FROM friends_info")
    suspend fun deleteAll()

}