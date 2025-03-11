package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import com.pirate.models.types.Preferences
import com.pirate.types.PreferencesKey
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesModel {

    @Query("SELECT * from preferences where `key` = :key")
    fun key(key: String): Preferences?


    @Query("SELECT * FROM user_details WHERE `key` LIKE :startsWith || ':%'")
    fun getMutedChats(startsWith: String = PreferencesKey.MUTED_CHATS.value): Flow<List<Preferences>>

}
