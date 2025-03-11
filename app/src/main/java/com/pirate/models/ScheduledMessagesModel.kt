package com.pirate.models

import androidx.room.Dao
import androidx.room.Query

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
    fun getMessage(scheduleId: String)

    @Query(
        value = """
        DELETE 
        FROM scheduled_messages 
        WHERE 
            schedule_id = :scheduleId
        """
    )
    fun deleteMessage(scheduleId: String)

}
