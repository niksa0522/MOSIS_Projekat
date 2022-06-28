package com.example.mosis_projekat.screens.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.mosis_projekat.R

class SettingsFragment : PreferenceFragmentCompat() {
    private var preferenceLocation: SwitchPreferenceCompat? = null
    private var preferenceNotification: SwitchPreferenceCompat? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferenceLocation = preferenceManager.findPreference("share_location")
        preferenceNotification = preferenceManager.findPreference("notifications")
        if(preferenceLocation!=null){
            preferenceLocation!!.setOnPreferenceChangeListener { preference, newValue ->
                    val test = newValue as Boolean
                    Log.d("SHAREDPREF",test.toString())
                //TODO pali i gasi servis
                //TODO takodje u activity kada se logout gasi i kad se login pali

                true
            }
        }
        if(preferenceNotification!=null){
            preferenceLocation!!.setOnPreferenceChangeListener { preference, newValue ->
                val test = newValue as Boolean
                Log.d("SHAREDPREF",test.toString())
                //TODO pali i gasi servis
                //TODO takodje u activity kada se logout gasi i kad se login pali

                true
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }


}