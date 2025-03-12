package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import com.pirate.models.types.ScheduledMessages

@Dao
interface ScheduledMessagesModel {

    @Query(
        value = """
        SELECT * 
        FROM scheduled_messages 
        WHERE 
            schedule_id = :scheduleId
        """
    )
    suspend fun getMessage(scheduleId: String): ScheduledMessages?

    @Query(
        value = """
        DELETE 
        FROM scheduled_messages 
        WHERE 
            schedule_id = :scheduleId
        """
    )
    suspend fun deleteMessage(scheduleId: String)

}
