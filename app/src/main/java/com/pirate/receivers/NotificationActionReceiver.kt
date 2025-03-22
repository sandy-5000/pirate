package com.pirate.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.pirate.services.DatabaseProvider
import com.pirate.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.pirate.MARK_AS_READ") {
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
                val dataBase = DatabaseProvider.getInstance(context)
                dataBase.friendsInfoModel.updateLastOpened(pirateId = pirateId)
                MainViewModel.refreshLastOpened()
            } catch (e: Exception) {
                Log.e("NotificationReceiver", "Error marking messages as read: ${e.message}")
            }
        }
    }
}
