package com.example.bootcounter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationHandler(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(content: String) {
        try {
            val notificationId = 1
            val channelId = "boot_event_channel"
            createNotificationChannel(channelId)

            val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
            val dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Boot Event")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setDeleteIntent(dismissPendingIntent)
                .build()

            notificationManager.notify(notificationId, notification)
            Log.d("NotificationHandler", "Notification shown: $content")
        } catch (e: Exception) {
            Log.e("NotificationHandler", "Error showing notification: ${e.message}")
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val name = "Boot Event Channel"
                val descriptionText = "Channel for boot event notifications"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
                Log.d("NotificationHandler", "Notification channel created: $channelId")
            } catch (e: Exception) {
                Log.e("NotificationHandler", "Error creating notification channel: ${e.message}")
            }
        }
    }
}