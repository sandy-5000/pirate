package com.pirate.services

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
import com.pirate.MainActivity
import com.pirate.R
import com.pirate.receivers.NotificationActionReceiver
import com.pirate.types.EventInfo
import com.pirate.types.EventType
import com.pirate.types.MessageType
import com.pirate.types.NotificationType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pirate.models.types.UserChats
import com.pirate.types.PreferencesKey
import com.pirate.viewModels.MainViewModel
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
        val encryptedMessage = message.data["message"] ?: ""
        val type = message.data["type"] ?: ""
        val senderId = message.data["sender_id"] ?: ""

        if (NotificationType.entries.any { it.value == type }) {
            val typeEnum = NotificationType.valueOf(type)
            val receivedMessage = if (NotificationType.MESSAGE == typeEnum) {
                MessageParser.decrypt(encryptedMessage)
            } else {
                encryptedMessage
            }
            if (NotificationType.MESSAGE == typeEnum) {
                saveMessageToDatabase(
                    senderId = senderId,
                    username = username,
                    message = receivedMessage,
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
                val messageId = message.substring(0, 12)
                val timestamp = message.substring(13, 26)
                val text = message.substring(27)
                val userChats = UserChats(
                    messageId = messageId,
                    pirateId = senderId,
                    message = text,
                    messageType = MessageType.TEXT.value,
                    messageStatus = 1,
                    side = 1,
                    receivedAt = timestamp,
                )
                dataBase.userChatsModel.insertMessage(userChats = userChats)
                val eventInfo = EventInfo(
                    type = EventType.MESSAGE,
                    pirateId = senderId,
                    username = username,
                    userChats = userChats,
                )
                if (MainViewModel.isApplicationOn() && MainViewModel.getCurrentPirateId() == senderId) {
                    dataBase.friendsInfoModel.updateLastOpened(pirateId = senderId)
                }
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
                    dataBase.userDetailsModel.key(PreferencesKey.APP_NOTIFICATION.value)?.value ?: "false"
                if (appNotificationFlag == "true") {
                    return@launch
                }
                val userChatNotificationFlag =
                    dataBase.preferencesModel.key(PreferencesKey.MUTED_CHATS.value + ":" + pirateId)?.value
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
        if (
            MainViewModel.isApplicationOn() && MainViewModel.getAppInForeground() &&
            MainViewModel.getCurrentPirateId() == pirateId && type == NotificationType.MESSAGE
        ) {
            return
        }

        if (type == NotificationType.MESSAGE_REQUEST) {
            MainViewModel.reloadRequestsData()
        }

        val channelId = "pirate_channel"
        val notificationId = pirateId.hashCode()

        val icon = when (type) {
            NotificationType.MESSAGE -> R.drawable.icon_chat_round_line
            NotificationType.MESSAGE_REQUEST -> R.drawable.icon_users_group
        }
        val markAsReadIcon = R.drawable.icon_check_circle
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
            action = "com.darkube.pirate.MARK_AS_READ"
            putExtra("notificationId", notificationId)
            putExtra("pirateId", pirateId)
        }
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            context,
            pirateId.hashCode(),
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
