package com.example.runnerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.runnerapp.R
import com.example.runnerapp.utils.Constants
import com.example.runnerapp.utils.Constants.KEY_NAME
import com.example.runnerapp.utils.Constants.KEY_WEIGHT
import com.example.runnerapp.utils.content
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUsersData()
        btnApplyChanges.setOnClickListener {
            val actionSucceeded = changeUsersDataToSharedPrefs()
            if (actionSucceeded) {
                Snackbar.make(
                    requireView(),
                    "User's data changed successfully!",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "The fields cannot be empty",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private fun changeUsersDataToSharedPrefs(): Boolean {
        val name = etName.content()
        val weight = etWeight.content()
        if (name.isEmpty() || weight.isEmpty()) return false
        sharedPrefs.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constants.KEY_USERS_DATA_GIVEN, true)
            .apply()
        val toolbarText = "Let's go, $name"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }

    private fun loadUsersData() {
        val name = sharedPrefs.getString(KEY_NAME, "")
        val weight = sharedPrefs.getFloat(KEY_WEIGHT, 60f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }
}