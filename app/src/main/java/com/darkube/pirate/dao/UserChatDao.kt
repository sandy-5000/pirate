package com.darkube.pirate.dao

import androidx.room.Dao
import androidx.room.Query
import com.darkube.pirate.types.room.UserChat

@Dao
interface UserChatDao {
    @Query(
        value = """
        INSERT INTO user_chats (pirate_id, received_at, message, type, side)
        VALUES (:pirateId, CURRENT_TIMESTAMP, :message, :type, :side)
        """
    )
    suspend fun insertMessage(pirateId: String, message: String, type: String, side: Int)

    @Query(
        value = """
        SELECT * FROM user_chats 
        WHERE pirate_id = :pirateId
        ORDER BY received_at DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getMessagesForPirate(pirateId: String, limit: Int, offset: Int): List<UserChat>

    @Query(
        value = """
        SELECT * FROM user_chats 
        WHERE pirate_id = :pirateId AND id > :id 
        ORDER BY received_at DESC
        """
    )
    suspend fun getLatestInsertedMessage(pirateId: String, id: Int): List<UserChat>

    @Query("DELETE FROM user_chats WHERE pirate_id = :pirateId")
    suspend fun deletePirateChat(pirateId: String)

    @Query("DELETE FROM user_chats")
    suspend fun deleteAll()
}
