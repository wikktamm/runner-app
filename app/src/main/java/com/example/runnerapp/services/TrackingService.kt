package com.example.runnerapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runnerapp.R
import com.example.runnerapp.ui.activities.MainActivity
import com.example.runnerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runnerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runnerapp.utils.Constants.INTERVAL_AVG_LOCATION_REQUEST
import com.example.runnerapp.utils.Constants.INTERVAL_FASTEST_LOCATION_REQUEST
import com.example.runnerapp.utils.Constants.INTERVAL_STOPWATCH_UPDATE
import com.example.runnerapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runnerapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runnerapp.utils.Constants.NOTIFICATION_ID
import com.example.runnerapp.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val trackedPaths = MutableLiveData<Polylines>()
        val totalTimeInMs = MutableLiveData<Long>()
    }

    private var firstRun = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var totalTimeInS = MutableLiveData<Long>()
    private var isTimerRunning = false
    private var timeStarted = 0L
    private var timeRun = 0L

    //running "one polyline" took that amount of ms
    private var singleRun = 0L

    //used for updating notification content
    private var passedSecondsTimestamp = 0L

    override fun onCreate() {
        super.onCreate()
        setInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationRequests(it)
        })
    }

    private fun setInitialValues() {
        isTracking.postValue(false)
        trackedPaths.postValue(mutableListOf())
        totalTimeInMs.postValue(0L)
        totalTimeInS.postValue(0L)
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        isTimerRunning = true
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                singleRun = System.currentTimeMillis() - timeStarted
                totalTimeInMs.postValue(singleRun + timeRun)
                if (totalTimeInMs.value!! >= passedSecondsTimestamp + 1000L) {
                    totalTimeInS.postValue(totalTimeInS.value!! + 1)
                    passedSecondsTimestamp += 1000L
                }
                delay(INTERVAL_STOPWATCH_UPDATE)
            }
            timeRun += singleRun
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationRequests(isTracking: Boolean) {
        if (isTracking) {
            if (PermissionUtils.hasPermissions(this)) {
                val locationRequest = LocationRequest().apply {
                    interval = INTERVAL_AVG_LOCATION_REQUEST
                    fastestInterval = INTERVAL_FASTEST_LOCATION_REQUEST
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPositionToPolyline(location)
                        Timber.d("location tracked ${location.latitude} - ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addEmptyPolyline() {
        trackedPaths.value?.apply {
            add(mutableListOf())
            trackedPaths.postValue(this)
        } ?: trackedPaths.postValue(mutableListOf(mutableListOf()))
    }

    private fun addPositionToPolyline(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            trackedPaths.value?.apply {
                last().add(pos)
                trackedPaths.postValue(this)
            }
        }
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
                        Timber.d("service resumed")//todo
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("service paused")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> Timber.d("service stopped")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTimerRunning = false
        isTracking.postValue(false)
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
        startTimer()
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