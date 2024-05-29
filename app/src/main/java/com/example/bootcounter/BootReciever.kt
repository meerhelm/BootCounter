package com.example.bootcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val db = AppDatabase.getDatabase(context)
            val bootEventDao = db.bootEventDao()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    bootEventDao.insert(BootEvent(timestamp = System.currentTimeMillis()))
                    NotificationScheduler.scheduleNotification(context)
                    showImmediateNotification(context.applicationContext)
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error handling boot event: ${e.message}")
                }
            }
        }
    }

    private fun showImmediateNotification(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationHelper = NotificationManagerHelper(context)
            notificationHelper.showBootNotification()
        }
    }
}
