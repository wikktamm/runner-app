package com.example.runnerapp.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.runnerapp.utils.Constants.ACTION_PAUSE
import com.example.runnerapp.utils.Constants.ACTION_START_OR_RESUME
import com.example.runnerapp.utils.Constants.ACTION_STOP
import timber.log.Timber

class TrackingService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME -> Timber.d("service started/resumed")
                ACTION_PAUSE -> Timber.d("service paused")
                ACTION_STOP -> Timber.d("service stopped")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}