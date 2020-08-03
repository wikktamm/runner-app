package com.example.runnerapp.data.repositories

import com.example.runnerapp.data.room.Run
import com.example.runnerapp.data.room.RunColumn
import com.example.runnerapp.data.room.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedDescByTimestamp() = runDao.getAllRunsSortedDesc(RunColumn.TIMESTAMP)
    fun getAllRunsSortedDescByCaloriesBurned() =
        runDao.getAllRunsSortedDesc(RunColumn.CALORIES_BURNED)

    fun getAllRunsSortedDescByAvgSpeed() = runDao.getAllRunsSortedDesc(RunColumn.AVG_SPEED_KMH)
    fun getAllRunsSortedDescByTimeInMs() = runDao.getAllRunsSortedDesc(RunColumn.TIME_IN_MS)
    fun getAllRunsSortedDescByDistance() = runDao.getAllRunsSortedDesc(RunColumn.DISTANCE_IN_M)

    fun getTotalDistanceInMeters() = runDao.getTotalMeters()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getAvgSpeedInKmH() = runDao.getAvgSpeedInKmH()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
}