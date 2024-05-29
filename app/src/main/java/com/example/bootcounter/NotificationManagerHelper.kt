package com.example.bootcounter

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationManagerHelper(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val bootEventDao = db.bootEventDao()
    private val notificationHandler = NotificationHandler(context)

    suspend fun showBootNotification(): Boolean {
        return try {
            val events = withContext(Dispatchers.IO) { bootEventDao.getAllEvents() }
            when {
                events.isEmpty() -> notificationHandler.showNotification("No boots detected")
                events.size == 1 -> notificationHandler.showNotification(
                    "The boot was detected = ${
                        formatDate(
                            events[0].timestamp
                        )
                    }"
                )

                else -> {
                    val timeDelta = events[0].timestamp - events[1].timestamp
                    notificationHandler.showNotification(
                        "Last boots time delta = ${
                            formatTimeDelta(
                                timeDelta
                            )
                        }"
                    )
                }
            }
            Log.d("NotificationManagerHelper", "Notification shown successfully")
            true
        } catch (e: Exception) {
            Log.e("NotificationManagerHelper", "Error in showBootNotification: ${e.message}")
            false
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun formatTimeDelta(delta: Long): String {
        val seconds = delta / 1000
        val minutes = seconds / 60
        return "$minutes minutes"
    }
}
