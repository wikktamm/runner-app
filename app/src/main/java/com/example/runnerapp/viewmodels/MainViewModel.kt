package com.example.runnerapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.runnerapp.data.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(val repo: MainRepository) : ViewModel(){
}