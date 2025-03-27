package com.pirate.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pirate.models.FriendsInfoModel
import com.pirate.models.PreferencesModel
import com.pirate.models.ScheduledMessagesModel
import com.pirate.models.UserChatsModel
import com.pirate.models.UserDetailsModel
import com.pirate.models.types.FriendsInfo
import com.pirate.models.types.Preferences
import com.pirate.models.types.ScheduledMessages
import com.pirate.models.types.UserChats
import com.pirate.models.types.UserDetails
import java.io.File
import java.util.Locale

@Database(
    entities = [
        FriendsInfo::class,
        Preferences::class,
        ScheduledMessages::class,
        UserChats::class,
        UserDetails::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class DataBase : RoomDatabase() {

    abstract val friendsInfoModel: FriendsInfoModel
    abstract val preferencesModel: PreferencesModel
    abstract val scheduledMessagesModel: ScheduledMessagesModel
    abstract val userChatsModel: UserChatsModel
    abstract val userDetailsModel: UserDetailsModel

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
            ).addCallback(object: RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(createTriggerOnInsertOrUpdate)
                }
            }).build()
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

private const val createTriggerOnInsertOrUpdate = """
    
    CREATE TRIGGER update_friends_on_message_insert
    AFTER INSERT ON user_chats
    FOR EACH ROW
    BEGIN
        UPDATE friends_info 
        SET 
            last_message_id = NEW.message_id,
            last_message = NEW.message,
            last_message_status = NEW.message_status,
            last_message_type = NEW.message_type,
            received_at = NEW.received_at
        WHERE 
            pirate_id = NEW.pirate_id;
    END;
    
    CREATE TRIGGER update_friends_on_message_update
    AFTER UPDATE ON user_chats
    FOR EACH ROW
    BEGIN
        UPDATE friends_info 
        SET 
            last_message_id = NEW.message_id,
            last_message = NEW.message,
            last_message_status = NEW.message_status,
            last_message_type = NEW.message_type,
            received_at = NEW.received_at
        WHERE 
            pirate_id = NEW.pirate_id AND
            received_at < NEW.received_at;
    END;

"""
