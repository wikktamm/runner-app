package com.example.runnerapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object PermissionUtils {
    val PERMISSIONS_LIST_BELOW_Q = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @SuppressLint("InlinedApi")
    val PERMISSIONS_LIST_ABOVE_Q = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    fun hasPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                *PERMISSIONS_LIST_BELOW_Q
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                *PERMISSIONS_LIST_ABOVE_Q
            )
        }
    }
}