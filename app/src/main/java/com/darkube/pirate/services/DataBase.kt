package com.darkube.pirate.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darkube.pirate.dao.FriendsInfoDao
import com.darkube.pirate.dao.LastMessageDao
import com.darkube.pirate.dao.UserChatDao
import com.darkube.pirate.dao.UserDetailsDao
import com.darkube.pirate.types.room.FriendsInfo
import com.darkube.pirate.types.room.LastMessage
import com.darkube.pirate.types.room.UserChat
import com.darkube.pirate.types.room.UserDetails
import java.io.File
import java.util.Locale

@Database(
    entities = [UserDetails::class, LastMessage::class, FriendsInfo::class, UserChat::class],
    version = 1,
    exportSchema = false,
)
abstract class DataBase : RoomDatabase() {

    abstract val userDetailsDao: UserDetailsDao
    abstract val lastMessageDao: LastMessageDao
    abstract val friendsInfoDao: FriendsInfoDao
    abstract val userChatDao: UserChatDao

}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: DataBase? = null

    fun getInstance(context: Context): DataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DataBase::class.java,
                "pirate_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }

    @JvmStatic
    fun getDatabaseSize(context: Context): String {
        val dbFile = File(context.getDatabasePath("pirate_database").absolutePath)

        if (!dbFile.exists()) return "0 KB"

        val sizeInBytes = dbFile.length().toDouble()
        return when {
            sizeInBytes >= 1_073_741_824 -> String.format(
                Locale.US,
                "%.2f GB",
                sizeInBytes / (1024 * 1024 * 1024)
            )

            sizeInBytes >= 1_048_576 -> String.format(
                Locale.US,
                "%.2f MB",
                sizeInBytes / (1024 * 1024)
            )

            else -> String.format(Locale.US, "%.2f KB", sizeInBytes / 1024)
        }
    }
}
