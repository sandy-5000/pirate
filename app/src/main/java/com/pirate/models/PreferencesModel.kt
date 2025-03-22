package com.pirate.models

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pirate.models.types.Preferences
import com.pirate.types.PreferencesKey
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesModel {

    @Upsert
    suspend fun update(preferences: Preferences)

    @Query("SELECT * from preferences where `key` = :key")
    fun key(key: String): Preferences?

    @Query("SELECT * FROM user_details WHERE `key` LIKE :startsWith || ':%'")
    fun getMutedChats(startsWith: String = PreferencesKey.MUTED_CHATS.value): Flow<List<Preferences>>

    @Query("DELETE from user_details where `key` = :key")
    suspend fun delete(key: String)
}
