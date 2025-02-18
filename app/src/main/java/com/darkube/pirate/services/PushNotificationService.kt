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
import com.darkube.pirate.receivers.NotificationActionReceiver
import com.darkube.pirate.types.NotificationType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("push-note", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: ""
        val body = message.notification?.body ?: ""
        val type = message.data["type"] ?: ""

        if (NotificationType.entries.any { it.value == type }) {
            showNotification(title, body, type)
        }
    }

    private fun showNotification(subText: String, body: String, type: String) {
        val channelId = "pirate_channel"
        val notificationId = 100
        Log.d("msg-type", type)
        val icon = when (type) {
            NotificationType.MESSAGE.value -> R.drawable.chat_round_line_icon
            NotificationType.MESSAGE_REQUEST.value -> R.drawable.users_group_icon
            else -> R.drawable.tabs_icon
        }
        val markAsReadIcon = R.drawable.check_circle_icon
        val title = when(type) {
            NotificationType.MESSAGE.value -> "New Message"
            NotificationType.MESSAGE_REQUEST.value -> "New Request"
            else -> ""
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
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
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val markAsReadIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "MARK_AS_READ"
            putExtra("notificationId", notificationId)
        }
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setSubText(subText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                markAsReadIcon,
                "Mark as Read",
                markAsReadPendingIntent
            )
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@PushNotificationService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, notification)
        }
    }
}