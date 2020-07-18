package com.example.runnerapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnerapp.data.repositories.MainRepository
import com.example.runnerapp.data.room.Run
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val repo: MainRepository) : ViewModel(){
    fun insertRun(run: Run){
        viewModelScope.launch {
            repo.insertRun(run)
        }
    }
}