package com.example.runnerapp.utils

import android.location.Location
import com.example.runnerapp.services.Polyline

object CalculationsUtils {
    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (index in 0..polyline.size - 2) {
            val firstPoint = polyline[index]
            val secondPoint = polyline[index + 1]
            val results = FloatArray(1)
            Location.distanceBetween(
                firstPoint.latitude,
                firstPoint.longitude,
                secondPoint.latitude,
                secondPoint.longitude,
                results
            )
            distance += results[0]
        }
        return distance
    }

    fun getCaloriesBurned(distance: Int, weight: Float): Int {
        return ((distance / 1000f) * weight).toInt()
    }
}