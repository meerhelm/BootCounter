package com.example.bootcounter

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile


@Database(entities = [BootEvent::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bootEventDao(): BootEventDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "boot_counter_db"
                    ).build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Error creating database: ${e.message}")
                    throw RuntimeException("Error creating database")
                }
            }
        }
    }
}