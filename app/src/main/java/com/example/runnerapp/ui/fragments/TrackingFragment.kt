package com.example.runnerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runnerapp.R
import com.example.runnerapp.services.Polyline
import com.example.runnerapp.services.TrackingService
import com.example.runnerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runnerapp.utils.Constants.MAP_CAMERA_ZOOM
import com.example.runnerapp.utils.Constants.POLYLINE_COLOR
import com.example.runnerapp.utils.Constants.POLYLINE_WIDTH
import com.example.runnerapp.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    private var isTracking = false
    private var trackedPaths = mutableListOf<Polyline>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            map = googleMap
            drawAllPolylines()
        }
        initListeners()
        observeOnChanges()
    }

    private fun observeOnChanges() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.trackedPaths.observe(viewLifecycleOwner, Observer {
            Timber.d("new point")
            trackedPaths = it
            drawLastPolyline()
            zoomToCurrentLocation()
        })
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        } else {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }
    }

    private fun drawLastPolyline() {
        if (trackedPaths.isNotEmpty() && trackedPaths.last().size > 1) {
            val preLastPoint = trackedPaths.last()[trackedPaths.last().size-2]
            val lastPoint = trackedPaths.last().last()
            createAndDrawPoly(preLastPoint, lastPoint)
            Timber.d("i am drawing it")
        }
    }

    private fun createAndDrawPoly(preLastPoint: LatLng, lastPoint: LatLng) {
        val polylineOptions =
            PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH).add(preLastPoint)
                .add(lastPoint)
        map?.addPolyline(polylineOptions)
    }


    private fun drawAllPolylines() {
        for (path in trackedPaths) {
            val poly = PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH)
                .addAll(path)
            map?.addPolyline(poly)
        }
    }

    private fun initListeners() {
        btnToggleRun.setOnClickListener {
            sendProperMessageToService()
        }
    }

    private fun zoomToCurrentLocation() {
        if (trackedPaths.isNotEmpty() && trackedPaths.last().size >= 1) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(trackedPaths.last().last(), MAP_CAMERA_ZOOM))
        }
    }

    private fun sendProperMessageToService() {
        if (isTracking) {
            sendMessageToService(ACTION_PAUSE_SERVICE)
        } else {
            sendMessageToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun sendMessageToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}