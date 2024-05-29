package com.example.bootcounter

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val notificationHelper = NotificationManagerHelper(applicationContext)
        return if (notificationHelper.showBootNotification()) {
            Result.success()
        } else {
            Result.failure()
        }
    }
}
