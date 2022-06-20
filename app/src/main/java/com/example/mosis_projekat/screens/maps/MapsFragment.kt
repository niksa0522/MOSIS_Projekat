package com.example.mosis_projekat.screens.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.activities.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.firebase.databaseModels.DatabaseLocation
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
        MapsViewModel.setMap(map,requireContext(),findNavController())
        //val home:LatLng = LatLng(43.3147738,21.898629)



        // dodaj locationmanager za pronalazak i updateovanje trenutne lokacije
        //kao i slanje te lokacije na firebase
        val zoomLevel = 17f
        fusedLocationClient?.lastLocation?.addOnCompleteListener {
            if(it.result.latitude!=null && it.result.longitude!=null) {
                //mozda skloni ovo
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
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoomLevel))
        //map.addMarker(MarkerOptions().position(home))
        if(isPermissionGranted()) {
            enableMyLocation()
            trackLocation()
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    @SuppressLint("MissingPermission")
    private fun trackLocation(){
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    private fun setupLocationTracking(){
        if(isPermissionGranted()){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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


    /*private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A snippet is additional text that's displayed after the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Dropped Pin")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }*/

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    //bolje napravi permisije
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            enableMyLocation()
            trackLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lateinit var newLocation:Location
                for (location in locationResult.locations){
                    /*map.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    )*/
                    newLocation = location


                    //add firebase update
                }
                map.animateCamera(CameraUpdateFactory.newLatLng(
                    LatLng(
                        newLocation.latitude,
                        newLocation.longitude
                    )
                ))
                if(lastLocation == null || newLocation.latitude!=lastLocation?.latitude || newLocation.longitude!=lastLocation?.longitude ){
                    lastLocation=newLocation
                    MapsViewModel.updateLocationFirebase(DatabaseLocation(newLocation.latitude,newLocation.longitude))
                }

            }
        }


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



        //observer se ne pali na mapu, moras da sredis to, pogledaj kako sledeceg puta
        /*MapsViewModel.users.observe(viewLifecycleOwner) {
            if (this::map.isInitialized) {
                map.clear()
                for (i in it) {
                    val locat = LatLng(i.value.lat!!, i.value.lon!!)
                    //dodaj ime i opis kasnije
                    val mar = map.addMarker(
                        MarkerOptions()
                            .position(locat)
                            .flat(true)
                    )
                }
            }
        }*/


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
                trackLocation()
            }
        }
    }
}