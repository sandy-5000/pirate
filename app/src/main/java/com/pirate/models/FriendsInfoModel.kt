package com.pirate.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pirate.models.types.FriendsInfo
import com.pirate.utils.getTimeStamp
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsInfoModel {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
            last_opened_at = :timeStamp
        WHERE
            pirate_id = :pirateId
        """
    )
    suspend fun updateLastOpenedInternal(pirateId: String, timeStamp: Long)

    suspend fun updateLastOpened(pirateId: String) {
        updateLastOpenedInternal(pirateId, getTimeStamp())
    }

    @Query(
        value = """
        UPDATE friends_info
        SET
            last_opened_at = :timeStamp
        """
    )
    suspend fun updateLastOpenedAllInternal(timeStamp: Long)

    suspend fun updateLastOpenedAll() {
        updateLastOpenedAllInternal(getTimeStamp())
    }

    @Query(
        value = """
        UPDATE friends_info 
        SET 
            last_message_id = '', 
            last_message = '',
            last_message_status = -1,
            last_message_type = 'TXT',
            received_at = 0
        WHERE 
            pirate_id = :pirateId
        """
    )
    suspend fun clearLastMessage(pirateId: String)

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
    suspend fun getAll(): List<FriendsInfo>

    @Query(value = "DELETE FROM friends_info")
    suspend fun deleteAll()

}