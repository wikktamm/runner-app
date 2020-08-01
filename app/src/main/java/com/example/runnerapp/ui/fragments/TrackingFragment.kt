package com.example.runnerapp.ui.fragments

import android.content.Intent
import android.graphics.Camera
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runnerapp.R
import com.example.runnerapp.data.room.Run
import com.example.runnerapp.services.Polyline
import com.example.runnerapp.services.TrackingService
import com.example.runnerapp.utils.CalculationsUtils
import com.example.runnerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runnerapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runnerapp.utils.Constants.MAP_CAMERA_ZOOM
import com.example.runnerapp.utils.Constants.POLYLINE_COLOR
import com.example.runnerapp.utils.Constants.POLYLINE_WIDTH
import com.example.runnerapp.utils.FormatUtils
import com.example.runnerapp.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.text.Format
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    @set:Inject
    var weight: Float = 50.1F
    private var isTracking = false
    private var trackedPaths = mutableListOf<Polyline>()
    private var timePassedInMs = 0L

    private var menu: Menu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showDialogCancelRun()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialogCancelRun() {
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.q_cancel_the_run))
            .setMessage(getString(R.string.q_want_to_cancel_run))
            .setIcon(R.drawable.ic_delete_black)
            .setPositiveButton("Yes") { _, _ ->
                stopRun()
            }
            .setNegativeButton("No") { _, _ ->
            }
            .create()
        dialog.show()
    }

    private fun stopRun() {
        sendMessageToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun observeOnChanges() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
            updateMenuIcon(it)
        })
        TrackingService.trackedPaths.observe(viewLifecycleOwner, Observer {
            trackedPaths = it
            drawLastPolyline()
            zoomToCurrentLocation()
        })
        TrackingService.totalTimeInMs.observe(viewLifecycleOwner, Observer {
            timePassedInMs = it
            val formattedTime = FormatUtils.getFormattedTime(timePassedInMs, true)
            tvTimer.text = formattedTime
        })
    }

    private fun updateMenuIcon(isTracking: Boolean?) {
        isTracking?.let {
            menu?.getItem(0)?.isVisible = it
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            btnToggleRun.text = getString(R.string.stop)
            btnFinishRun.visibility = View.GONE
        } else {
            btnToggleRun.text = getString(R.string.start)
            if (timePassedInMs > 0) {
                btnFinishRun.visibility = View.VISIBLE
            }
        }
    }

    private fun drawLastPolyline() {
        if (trackedPaths.isNotEmpty() && trackedPaths.last().size > 1) {
            val preLastPoint = trackedPaths.last()[trackedPaths.last().size - 2]
            val lastPoint = trackedPaths.last().last()
            createAndDrawPoly(preLastPoint, lastPoint)
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
        btnFinishRun.setOnClickListener {
            zoomToSeeTrackedPaths()
            stopRunAndSaveIt()
        }
    }

    private fun stopRunAndSaveIt() {
        map?.snapshot { bitmap ->
            var distance = 0
            for (polyline in trackedPaths) {
                distance += CalculationsUtils.calculatePolylineLength(polyline).toInt()
            }
            var avgSpeed = round((distance / 1000f) / (timePassedInMs / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = CalculationsUtils.getCaloriesBurned(distance, weight)
            val run = Run(bitmap, timestamp, caloriesBurned, avgSpeed, timePassedInMs, distance)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                getString(R.string.prompt_run_saved_successfully),
                Snackbar.LENGTH_SHORT
            ).show()
            stopRun()
        }
    }

    private fun zoomToCurrentLocation() {
        if (trackedPaths.isNotEmpty() && trackedPaths.last().size >= 1) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    trackedPaths.last().last(),
                    MAP_CAMERA_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeTrackedPaths() {
        val bounds = LatLngBounds.builder()
        for (polyline in trackedPaths) {
            for (point in polyline) {
                bounds.include(point)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
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