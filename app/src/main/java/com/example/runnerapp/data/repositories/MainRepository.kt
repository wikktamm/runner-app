package com.example.runnerapp.data.repositories

import com.example.runnerapp.data.room.Run
import com.example.runnerapp.data.room.RunColumn
import com.example.runnerapp.data.room.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedDescByTimestamp() = runDao.getAllRunsSortedDesc(RunColumn.timestamp)
    fun getAllRunsSortedDescByCaloriesBurned() =
        runDao.getAllRunsSortedDesc(RunColumn.caloriesBurned)

    fun getAllRunsSortedDescByAvgSpeed() = runDao.getAllRunsSortedDesc(RunColumn.averageSpeedKMH)
    fun getAllRunsSortedDescByTimeInMs() = runDao.getAllRunsSortedDesc(RunColumn.timeInMs)
    fun getAllRunsSortedDescByDistance() = runDao.getAllRunsSortedDesc(RunColumn.distanceInMeters)

    fun getTotalMeters() = runDao.getTotalMeters()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getAvgSpeedInKmH() = runDao.getAvgSpeedInKmH()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
}