package com.example.runnerapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query(
        "SELECT * FROM runs ORDER BY CASE WHEN :column = 'timestamp' THEN timestamp END DESC, CASE WHEN :column = 'timeInMs' THEN timeInMs END DESC, CASE WHEN :column = 'caloriesBurned' THEN caloriesBurned END DESC, CASE WHEN :column = 'averageSpeedKMH'  THEN averageSpeedKMH END DESC, CASE WHEN :column = 'distanceInMeters' THEN distanceInMeters END DESC")
    fun getAllRunsSortedDesc(column: RunColumn): LiveData<List<Run>>

    @Query("SELECT SUM(distanceInMeters) FROM runs")
    fun getTotalMeters(): LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM runs")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT AVG(averageSpeedKMH) FROM runs")
    fun getAvgSpeedInKmH(): LiveData<Float>

    @Query("SELECT SUM(timeInMs) FROM runs")
    fun getTotalTimeInMillis(): LiveData<Long>
}