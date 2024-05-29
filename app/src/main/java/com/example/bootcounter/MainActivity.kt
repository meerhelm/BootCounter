package com.example.bootcounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bootcounter.NotificationScheduler.scheduleNotification
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var bootEventsTextView: TextView
    private lateinit var dismissalsAllowedEditText: TextInputEditText
    private lateinit var intervalBetweenDismissalsEditText: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        bootEventsTextView = findViewById(R.id.bootEventsTextView)
        dismissalsAllowedEditText = findViewById(R.id.dismissalsAllowedEditText)
        intervalBetweenDismissalsEditText = findViewById(R.id.intervalBetweenDismissalsEditText)
        saveButton = findViewById(R.id.saveButton)
        preferencesHelper = PreferencesHelper(this)

        loadDismissalConfig()
        val db = AppDatabase.getDatabase(this)
        val bootEventDao = db.bootEventDao()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val events = bootEventDao.getAllEvents()
                withContext(Dispatchers.Main) {
                    if (events.isEmpty()) {
                        bootEventsTextView.text = getString(R.string.no_boots_detected)
                    } else {
                        val eventCountPerDay = events.groupBy {
                            SimpleDateFormat(
                                "dd/MM/yyyy", Locale.getDefault()
                            ).format(Date(it.timestamp))
                        }.mapValues { it.value.size }

                        bootEventsTextView.text = eventCountPerDay.entries.joinToString("\n") {
                            "${it.key} - ${it.value}"
                        }
                    }
                }
                Log.d("MainActivity", "Boot events loaded successfully")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading boot events: ${e.message}")
            }
        }

        saveButton.setOnClickListener {
            saveDismissalConfig()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                scheduleNotification(this)
                showImmediateNotification()
            }
        } else {
            scheduleNotification(this)
            showImmediateNotification()
        }
    }

    private fun loadDismissalConfig() {
        val dismissalsAllowed = preferencesHelper.getDismissalsAllowed()
        val intervalBetweenDismissals = preferencesHelper.getIntervalBetweenDismissals()
        dismissalsAllowedEditText.setText(dismissalsAllowed.toString())
        intervalBetweenDismissalsEditText.setText(intervalBetweenDismissals.toString())
        Log.d(
            "MainActivity",
            "Dismissal config loaded: $dismissalsAllowed, $intervalBetweenDismissals"
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                scheduleNotification(this)
                showImmediateNotification()
                Log.d("MainActivity", "Notification permission granted")
            } else {
                Log.d("MainActivity", "Notification permission denied")
            }
        }

    private fun saveDismissalConfig() {
        try {
            val dismissalsAllowed = dismissalsAllowedEditText.text.toString().toInt()
            val intervalBetweenDismissals =
                intervalBetweenDismissalsEditText.text.toString().toInt()
            preferencesHelper.saveDismissalConfig(dismissalsAllowed, intervalBetweenDismissals)
            Log.d("MainActivity", "Save button clicked")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error handling save button click: ${e.message}")
        }
    }


    private fun showImmediateNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationHelper = NotificationManagerHelper(applicationContext)
            notificationHelper.showBootNotification()
        }
    }
}