package com.example.mosis_projekat.screens.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.mosis_projekat.R
import com.example.mosis_projekat.firebase.realtimeDB.RealtimeHelper
import com.example.mosis_projekat.helpers.ServiceHelper
import com.example.mosis_projekat.services.LocationService

class SettingsFragment : PreferenceFragmentCompat() {
    private var preferenceLocation: SwitchPreferenceCompat? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferenceLocation = preferenceManager.findPreference("share_location")
        if(preferenceLocation!=null){
            preferenceLocation!!.setOnPreferenceChangeListener { preference, newValue ->
                    val test = newValue as Boolean
                    Log.d("SHAREDPREF",test.toString())

                if(test){
                    ServiceHelper.startService(requireContext())

                }
                else{
                    ServiceHelper.stopService(requireContext())
                }


                true
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


}