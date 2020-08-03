package com.example.runnerapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runnerapp.R
import com.example.runnerapp.utils.FormatUtils
import com.example.runnerapp.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOnChanges()
    }

    private fun observeOnChanges() {
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                tvTotalCalories.text = "$it kcal"
            }
        })
        viewModel.averageSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeedStr = (it * 10f) / 10f
                tvAverageSpeed.text = "$avgSpeedStr km/h"
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val distanceInKm = it / 1000f
                val distanceStr = (distanceInKm * 10f) / 10f
                tvTotalDistance.text = "$distanceStr km"
            }
        })
        viewModel.totalTimeInMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                val formattedTimeStr = FormatUtils.getFormattedTime(it)
                tvTotalTime.text = "$formattedTimeStr"
            }
        })
    }
}