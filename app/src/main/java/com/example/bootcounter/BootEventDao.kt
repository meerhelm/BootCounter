package com.example.bootcounter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BootEventDao {
    @Insert
    suspend fun insert(bootEvent: BootEvent)

    @Query("SELECT * FROM boot_events ORDER BY timestamp DESC")
    suspend fun getAllEvents(): List<BootEvent>

    @Query("SELECT * FROM boot_events ORDER BY timestamp DESC LIMIT 2")
    suspend fun getLastTwoEvents(): List<BootEvent>
}
