package com.darkube.pirate.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.types.DetailsKey
import com.darkube.pirate.types.room.UserDetails
import com.darkube.pirate.utils.DatabaseProvider
import com.darkube.pirate.utils.getCurrentUtcTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.darkube.pirate.MARK_AS_READ") {
            val notificationId = intent.getIntExtra("notificationId", -1)
            val pirateId = intent.getStringExtra("pirateId") ?: ""

            if (notificationId != -1 && pirateId.isNotEmpty()) {
                markMessageAsRead(context, pirateId)
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(notificationId)
            }
        }
    }

    private fun markMessageAsRead(context: Context, pirateId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = DatabaseProvider.getInstance(context)
                database.userDetailsDao.update(
                    UserDetails(
                        key = DetailsKey.LAST_OPENED.value + ":" + pirateId,
                        value = getCurrentUtcTimestamp()
                    )
                )
                MainViewModel.refreshLastOpened()
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error marking messages as read: ${e.message}")
            }
        }
    }
}
