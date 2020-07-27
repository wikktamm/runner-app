package com.example.runnerapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnerapp.data.repositories.MainRepository
import com.example.runnerapp.data.room.Run
import com.example.runnerapp.data.room.RunColumn
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val repo: MainRepository) : ViewModel() {

    private val runsSortedByTimeInMillis = repo.getAllRunsSortedDescByTimeInMs()
    private val runsSortedByDistance = repo.getAllRunsSortedDescByDistance()
    private val runsSortedCaloriesBurned = repo.getAllRunsSortedDescByCaloriesBurned()
    private val runsSortedByAvgSpeed = repo.getAllRunsSortedDescByAvgSpeed()
    private val runsSortedByTimestamp = repo.getAllRunsSortedDescByTimestamp()

    val runs = MediatorLiveData<List<Run>>()
    var sortType = RunColumn.TIMESTAMP

    init {
        runs.addSource(runsSortedByTimeInMillis) { result ->
            if (sortType == RunColumn.TIME_IN_MS) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if (sortType == RunColumn.DISTANCE_IN_M) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runsSortedCaloriesBurned) { result ->
            if (sortType == RunColumn.CALORIES_BURNED) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == RunColumn.AVG_SPEED_KMH) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runsSortedByTimestamp) { result ->
            if (sortType == RunColumn.TIMESTAMP) {
                result?.let {
                    runs.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: RunColumn) {
        when (sortType) {
            RunColumn.TIME_IN_MS -> {
                runsSortedByTimeInMillis.value?.let {
                    runs.value = it
                }
            }
            RunColumn.AVG_SPEED_KMH -> {
                runsSortedByAvgSpeed.value?.let {
                    runs.value = it
                }
            }
            RunColumn.TIMESTAMP -> {
                runsSortedByTimestamp.value?.let {
                    runs.value = it
                }
            }
            RunColumn.CALORIES_BURNED -> {
                runsSortedCaloriesBurned.value?.let {
                    runs.value = it
                }
            }
            RunColumn.DISTANCE_IN_M -> {
                runsSortedByDistance.value?.let {
                    runs.value = it
                }
            }
        }
        this.sortType = sortType
    }

    fun insertRun(run: Run) {
        viewModelScope.launch {
            repo.insertRun(run)
        }
    }
}