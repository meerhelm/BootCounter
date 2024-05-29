package com.example.bootcounter

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    fun scheduleNotification(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "NotificationWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            notificationWork
        )
    }
}
