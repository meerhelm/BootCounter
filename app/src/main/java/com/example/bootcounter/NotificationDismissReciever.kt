package com.example.bootcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationDismissReceiver", "Notification dismissed")

        val sharedPreferences =
            context.getSharedPreferences("BootCounterPrefs", Context.MODE_PRIVATE)
        val dismissalsAllowed = sharedPreferences.getInt("dismissalsAllowed", 5)
        val intervalBetweenDismissals = sharedPreferences.getInt("intervalBetweenDismissals", 20)
        var dismissCount = sharedPreferences.getInt("dismissCount", 0)
        dismissCount++

        val delay: Long
        if (dismissCount <= dismissalsAllowed) {
            delay = (dismissCount * intervalBetweenDismissals).toLong()
        } else {
            dismissCount = 0
            delay = 15L
        }

        with(sharedPreferences.edit()) {
            putInt("dismissCount", dismissCount)
            apply()
        }

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)

        Log.d("NotificationDismissReceiver", "Notification rescheduled in $delay minutes")
    }
}