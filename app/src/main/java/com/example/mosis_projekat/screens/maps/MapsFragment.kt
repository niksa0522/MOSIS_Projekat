package com.example.mosis_projekat.screens.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.activities.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.firebase.databaseModels.DatabaseLocation
import com.example.mosis_projekat.helpers.PermissionHelper
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment : Fragment() {

    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private val MapsViewModel: MapsViewModel by viewModels()
    //private var markers:MutableList<Marker> = mutableListOf()

    private lateinit var map:GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        map.clear()
        MapsViewModel.setMap(map,requireContext(),findNavController())



        // dodaj locationmanager za pronalazak i updateovanje trenutne lokacije
        //kao i slanje te lokacije na firebase
        val zoomLevel = 17f

        if(PermissionHelper.isLocationPermissionGranted(requireContext())) {
            enableMyLocation()
            if(fusedLocationClient!=null){
                fusedLocationClient?.lastLocation?.addOnCompleteListener {
                    if(it.result!=null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.result.latitude,
                                    it.result.longitude
                                ), zoomLevel
                            )
                        )
                        lastLocation=it.result
                    }
                }
            }else{
                setupLocationTrackingWithLocation()
            }
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }




    private fun setupLocationTracking(){
        if(PermissionHelper.isLocationPermissionGranted(requireContext())){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        }else{
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    @SuppressLint("MissingPermission")
    private fun setupLocationTrackingWithLocation(){
        if(PermissionHelper.isLocationPermissionGranted(requireContext())){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient?.lastLocation?.addOnCompleteListener {
                if(it.result!=null) {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.result.latitude,
                                it.result.longitude
                            ), 17f
                        )
                    )
                    lastLocation=it.result
                }
            }
        }else{
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private val requestPermissionLauncherFLC = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            setupLocationTracking()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (PermissionHelper.isLocationPermissionGranted(requireContext())) {
            map.isMyLocationEnabled = true
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            enableMyLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        setupLocationTracking()
        mapFragment?.getMapAsync(callback)

        val btn: FloatingActionButton = view.findViewById(R.id.fab_add)
        btn.setOnClickListener {
            val bundle = Bundle()
            bundle.putDouble("lat",lastLocation!!.latitude)
            bundle.putDouble("long",lastLocation!!.longitude)
            findNavController().navigate(R.id.action_nav_maps_to_addWorkshopFragment,bundle)
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_show_users -> {
                if(item.isCheckable){
                    if(item.isChecked){
                        MapsViewModel.hideUsers()
                        item.isChecked=false
                    }else{
                        MapsViewModel.showUsers()
                        item.isChecked=true
                    }
                }
                true
            }
            R.id.action_show_workshops ->{
                openWorkshopTypeDialog()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    private fun openWorkshopTypeDialog(){
        setFragmentResultListener("Filtriraj radionice"){ requestKey, bundle ->
            val result = bundle.getStringArrayList("list")
            if(result!=null){
                MapsViewModel.filterWorkshops(result)
                MapsViewModel.workshopTypes=result
            }
            Log.d("WORKSHOPLIST",result.toString());
        }
        val bundle=Bundle()
        bundle.putString("type","Filtriraj radionice")
        bundle.putStringArrayList("chosen",MapsViewModel.workshopTypes)
        findNavController().navigate(R.id.action_nav_maps_to_workshopTypeDialog,bundle)
    }
}