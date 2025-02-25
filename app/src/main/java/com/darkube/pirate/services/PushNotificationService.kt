package com.darkube.pirate.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.darkube.pirate.MainActivity
import com.darkube.pirate.R
import com.darkube.pirate.models.MainViewModel
import com.darkube.pirate.receivers.NotificationActionReceiver
import com.darkube.pirate.types.DetailsKey
import com.darkube.pirate.types.EventInfo
import com.darkube.pirate.types.EventType
import com.darkube.pirate.types.MessageType
import com.darkube.pirate.types.NotificationType
import com.darkube.pirate.utils.DatabaseProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("push-note", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val username = message.data["username"] ?: ""
        val receivedMessage = message.data["message"] ?: ""
        val type = message.data["type"] ?: ""
        val senderId = message.data["sender_id"] ?: ""

        if (NotificationType.entries.any { it.value == type }) {
            val typeEnum = NotificationType.valueOf(type)
            if (NotificationType.MESSAGE == typeEnum) {
                saveMessageToDatabase(
                    senderId = senderId,
                    username = username,
                    message = receivedMessage
                )
            }
            fetchConditionsFromDatabase(
                context = this,
                pirateId = senderId,
                username = username,
                message = receivedMessage,
                type = typeEnum,
            )
        }
    }

    private fun saveMessageToDatabase(senderId: String, username: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataBase = DatabaseProvider.getInstance(applicationContext)
                dataBase.lastMessageDao.upsertMessage(senderId, username, message)
                dataBase.userChatDao.insertMessage(
                    pirateId = senderId,
                    message = message,
                    type = MessageType.TEXT.value,
                    side = 1
                )
                val eventInfo = EventInfo(
                    type = EventType.MESSAGE,
                    id = senderId,
                    username = username,
                    message = message
                )
                MainViewModel.emit(eventInfo = eventInfo)
            } catch (e: Exception) {
                Log.e("push-note", "Error saving message: ${e.message}")
            }
        }
    }

    private fun fetchConditionsFromDatabase(
        context: Context,
        pirateId: String,
        username: String,
        message: String,
        type: NotificationType,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataBase = DatabaseProvider.getInstance(applicationContext)
                val appNotificationFlag =
                    dataBase.userDetailsDao.key(DetailsKey.APP_NOTIFICATION.value)?.value ?: "false"
                if (appNotificationFlag == "true") {
                    return@launch
                }
                val userChatNotificationFlag =
                    dataBase.userDetailsDao.key(DetailsKey.CHAT_NOTIFICATION.value + ":" + pirateId)?.value
                        ?: "false"
                if (userChatNotificationFlag == "true") {
                    return@launch
                }
                NotificationHelper.showNotification(
                    context = context,
                    pirateId = pirateId,
                    username = username,
                    message = message,
                    type = type,
                )
            } catch (e: Exception) {
                Log.e("push-note", "Error while fetching details: ${e.message}")
            }
        }
    }
}

object NotificationHelper {
    fun showNotification(
        context: Context,
        pirateId: String,
        username: String,
        message: String,
        type: NotificationType,
    ) {
        if (MainViewModel.isApplicationOn() && MainViewModel.getCurrentPirateId() == pirateId && type == NotificationType.MESSAGE) {
            return
        }

        if (type == NotificationType.MESSAGE_REQUEST) {
            MainViewModel.reloadRequestsData()
        }

        val channelId = "pirate_channel"
        val notificationId = pirateId.hashCode()

        val icon = when (type) {
            NotificationType.MESSAGE -> R.drawable.chat_round_line_icon
            NotificationType.MESSAGE_REQUEST -> R.drawable.users_group_icon
        }
        val markAsReadIcon = R.drawable.check_circle_icon
        val title = when (type) {
            NotificationType.MESSAGE -> "New Message"
            NotificationType.MESSAGE_REQUEST -> "New Request"
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pirate",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val markAsReadIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "MARK_AS_READ"
            putExtra("notificationId", notificationId)
        }
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setSubText(username)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                markAsReadIcon,
                "Mark as Read",
                markAsReadPendingIntent
            )
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, notification)
        }
    }
}
