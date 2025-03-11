package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pirate.models.types.UserChats
import com.pirate.types.MessageType

@Dao
interface UserChatsModel {

    @Upsert
    suspend fun insertMessage(userChats: UserChats)

    @Query(
        value = """
        UPDATE user_chats 
        SET 
            message_status = :messageStatus
        WHERE 
            message_id = :messageId
        """
    )
    suspend fun updateMessageStatus(messageId: String, messageStatus: String)

    @Query(
        value = """
        DELETE FROM user_chats
        WHERE 
            message_id = :messageId
        """
    )
    suspend fun deleteMessage(messageId: String)

    @Query(
        value = """
        UPDATE user_chats
        SET 
            message = :message,
            referred_id = :referredId,
            referred_message = :referredMessage,
            referred_message_type = :referredMessageType
        WHERE 
            message_id = :messageId AND message_type = 'TXT'
        """
    )
    suspend fun updateMessage(
        messageId: String,
        message: String,
        referredId: String = "",
        referredMessage: String = "",
        referredMessageType: String = MessageType.TEXT.value,
    )

    @Query(
        value = """
        SELECT * 
        FROM user_chats 
        WHERE 
            pirate_id = :pirateId
        ORDER BY received_at DESC
        LIMIT :limit 
        OFFSET :offset
        """
    )
    suspend fun getMessagesForPirate(pirateId: String, limit: Int, offset: Int): List<UserChats>

    @Query(
        value = """
        SELECT * 
        FROM user_chats 
        WHERE 
            pirate_id = :pirateId AND message_id > :messageId 
        ORDER BY received_at DESC
        """
    )
    suspend fun getLatestInsertedMessage(pirateId: String, messageId: String): List<UserChats>

    @Query(
        value = """
        DELETE FROM user_chats
        WHERE 
            pirate_id = :pirateId
        """
    )
    suspend fun deletePirateChat(pirateId: String)

    @Query(value = "DELETE FROM user_chats")
    suspend fun deleteAll()

}
