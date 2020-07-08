package com.example.runnerapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.location.Location
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runnerapp.R
import com.example.runnerapp.ui.activities.MainActivity
import com.example.runnerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runnerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runnerapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runnerapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runnerapp.utils.Constants.NOTIFICATION_ID
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    private var firstRun = true

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val trackedPaths = MutableLiveData<Polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        setInitialValues()
    }

    private fun addEmptyPolyline() {
        trackedPaths.value?.apply {
            add(mutableListOf())
            trackedPaths.postValue(this)
        } ?: trackedPaths.postValue(mutableListOf())
    }

    private fun addPositionToPolyline(location: Location?) {
        location?.let{
            trackedPaths.value?.apply {
                last().add(LatLng(location.latitude, location.longitude))
                trackedPaths.postValue(this)
            }
        }
    }

    private fun setInitialValues() {
        isTracking.postValue(false)
        trackedPaths.postValue(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (firstRun) {
                        firstRun = false
                        Timber.d("service started")
                        startForegroundService()
                    } else {
                        Timber.d("service resumed")
                    }
                }
                ACTION_PAUSE_SERVICE -> Timber.d("service paused")
                ACTION_STOP_SERVICE -> Timber.d("service stopped")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //todo IMPORTANCE_LOW to avoid sounds with each notification
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        }, PendingIntent.FLAG_UPDATE_CURRENT)
}