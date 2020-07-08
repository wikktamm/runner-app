package com.example.runnerapp.data.room

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs")
data class Run(
    var image: Bitmap? = null,
    var timestamp: Long = 0L,
    var caloriesBurned: Int = 0,
    var averageSpeedKMH: Float = 0f,
    var timeInMs: Long = 0L,
    var distanceInMeters: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

enum class RunColumn {
    TIMESTAMP, CALORIES_BURNED, AVG_SPEED_KMH, TIME_IN_MS, DISTANCE_IN_M
}
