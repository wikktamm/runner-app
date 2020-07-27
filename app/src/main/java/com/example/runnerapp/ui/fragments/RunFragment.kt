package com.example.runnerapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runnerapp.R
import com.example.runnerapp.adapters.RunAdapter
import com.example.runnerapp.data.room.RunColumn
import com.example.runnerapp.utils.Constants.REQUEST_CODE_PERMISSIONS
import com.example.runnerapp.utils.PermissionUtils
import com.example.runnerapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
        setupRecyclerView()
        when (viewModel.sortType) {
            RunColumn.TIMESTAMP -> spFilter.setSelection(0)
            RunColumn.TIME_IN_MS -> spFilter.setSelection(1)
            RunColumn.DISTANCE_IN_M -> spFilter.setSelection(2)
            RunColumn.AVG_SPEED_KMH -> spFilter.setSelection(3)
            RunColumn.CALORIES_BURNED -> spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        viewModel.sortRuns(RunColumn.TIMESTAMP)
                    }
                    1 -> {
                        viewModel.sortRuns(RunColumn.TIME_IN_MS)
                    }
                    2 -> {
                        viewModel.sortRuns(RunColumn.DISTANCE_IN_M)
                    }
                    3 -> {
                        viewModel.sortRuns(RunColumn.AVG_SPEED_KMH)
                    }
                    4 -> {
                        viewModel.sortRuns(RunColumn.CALORIES_BURNED)
                    }
                }
            }

        }
        spFilter.setSelection(0)
        observeOnChanges()
    }

    private fun observeOnChanges() {
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.differ.submitList(it)
        })
    }

    private fun setupRecyclerView() {
        rvRuns.apply {
            runAdapter = RunAdapter()
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasPermissions(requireContext())) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.prompt_permissions_needed),
                REQUEST_CODE_PERMISSIONS,
                *PermissionUtils.PERMISSIONS_LIST_BELOW_Q
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.prompt_permissions_needed),
                REQUEST_CODE_PERMISSIONS,
                *PermissionUtils.PERMISSIONS_LIST_ABOVE_Q
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else requestPermissions()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}