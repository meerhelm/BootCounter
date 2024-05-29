package com.example.bootcounter

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PreferencesKeys.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveDismissalConfig(dismissalsAllowed: Int, intervalBetweenDismissals: Int) {
        with(sharedPreferences.edit()) {
            putInt(PreferencesKeys.KEY_DISMISSALS_ALLOWED, dismissalsAllowed)
            putInt(PreferencesKeys.KEY_INTERVAL_BETWEEN_DISMISSALS, intervalBetweenDismissals)
            apply()
        }
    }

    fun getDismissalsAllowed(): Int {
        return sharedPreferences.getInt(PreferencesKeys.KEY_DISMISSALS_ALLOWED, 5)
    }

    fun getIntervalBetweenDismissals(): Int {
        return sharedPreferences.getInt(PreferencesKeys.KEY_INTERVAL_BETWEEN_DISMISSALS, 20)
    }
}
