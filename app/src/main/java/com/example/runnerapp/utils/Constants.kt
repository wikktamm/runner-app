package com.example.runnerapp.utils

import android.graphics.Color

object Constants {
    const val RUN_DATABASE_NAME = "run_db"

    const val REQUEST_CODE_PERMISSIONS = 1
    const val REQUEST_CODE_SERVICE_PAUSE = 2
    const val REQUEST_CODE_SERVICE_START_OR_RESUME = 3

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME"
    const val ACTION_STOP_SERVICE = "ACTION_STOP"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_ID = 2
    const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"

    const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
    const val KEY_USERS_DATA_GIVEN = "KEY_USERS_DATA_GIVEN"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"

    const val INTERVAL_AVG_LOCATION_REQUEST = 6000L
    const val INTERVAL_FASTEST_LOCATION_REQUEST = 3000L
    const val INTERVAL_STOPWATCH_UPDATE = 50L

    const val POLYLINE_COLOR = Color.BLACK
    const val POLYLINE_WIDTH = 8f
    const val MAP_CAMERA_ZOOM = 16f
}