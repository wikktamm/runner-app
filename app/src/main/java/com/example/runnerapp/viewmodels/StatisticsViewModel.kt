package com.example.runnerapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.runnerapp.data.repositories.MainRepository

class StatisticsViewModel @ViewModelInject constructor(val repo: MainRepository) : ViewModel() {

    val totalCaloriesBurned = repo.getTotalCaloriesBurned()
    val totalDistance = repo.getTotalDistanceInMeters()
    val averageSpeed = repo.getAvgSpeedInKmH()
    val totalTimeInMillis = repo.getTotalTimeInMillis()

    val runsSortedByDate = repo.getAllRunsSortedDescByTimestamp()
}