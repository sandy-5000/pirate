package com.darkube.pirate.dao

import androidx.room.Dao
import androidx.room.Query
import com.darkube.pirate.types.room.LastMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface LastMessageDao {
    @Query(
        value = """
        INSERT OR REPLACE INTO last_message (pirate_id, username, message, receive_time) 
        VALUES (:pirateId, :username, :message, CURRENT_TIMESTAMP)
        """
    )
    fun upsertMessage(pirateId: String, username: String, message: String)

    @Query(value = "SELECT * FROM last_message ORDER BY receive_time DESC")
    fun getAllMessages(): Flow<List<LastMessage>>
}
