package com.example.mosis_projekat.services

import android.R.attr.delay
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mosis_projekat.R
import com.example.mosis_projekat.activities.MainActivity
import com.example.mosis_projekat.firebase.databaseModels.DatabaseLocation
import com.example.mosis_projekat.firebase.databaseModels.Workshop
import com.example.mosis_projekat.helpers.PermissionHelper
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class LocationService: Service() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth:FirebaseAuth
    private val CHANNEL_ID = "LocationService"
    private lateinit var fusedLocationService: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var uid: String? = null
    private lateinit var locationManager: LocationManager
    private lateinit var listener: LocationListener
    private var workshopList = mutableListOf<String>()
    private lateinit var queryListener: GeoQueryEventListener
    private lateinit var geoQuery: GeoQuery
    var latitude = 0.0
    var longitude = 0.0

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        firebaseDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        databaseReference = firebaseDatabase.reference.child("locations")
        fusedLocationService = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth
        uid = auth.uid

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lateinit var newLocation: Location
                for (location in locationResult.locations){
                    newLocation = location
                }

                if(lastLocation == null || newLocation.latitude!=lastLocation?.latitude || newLocation.longitude!=lastLocation?.longitude ){
                    if(uid!=null && uid!="") {
                        lastLocation = newLocation
                        updateLocationFirebase(
                            DatabaseLocation(
                                newLocation.latitude,
                                newLocation.longitude
                            )
                        )
                        //showNotification(newLocation.latitude, newLocation.longitude)
                    }
                }

            }
        }
        listener = LocationListener {
                newLocation ->
            updateLocationFirebase(
                DatabaseLocation(
                    newLocation.latitude,
                    newLocation.longitude
                )
            )
            getWorkshopsNearby(newLocation.latitude,newLocation.longitude)
        }
        queryListener = object:GeoQueryEventListener{
            override fun onKeyEntered(key: String, location: GeoLocation?) {
                if(!workshopList.contains(key)){
                    workshopList.add(key!!)
                    sendNotificationAboutWorkshop(key)
                }
            }

            override fun onKeyExited(key: String?) {
                if(!workshopList.contains(key)){
                    workshopList.remove(key!!)
                }
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {

            }

            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError?) {
            }

        }
        val rtDB = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = rtDB.reference.child("geoFireWorkshops")
        val geoFire = GeoFire(ref)
        geoQuery = geoFire.queryAtLocation(GeoLocation(0.0, 0.0),0.0)
        geoQuery.addGeoQueryEventListener(queryListener)


        //showNotification(lat,long)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        getCurrentLocation(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setSmallIcon(R.drawable.ic_baseline_map_24)
            .build()
        startForeground(1, notification)

        return START_STICKY
    }

    private fun sendNotification(workshopName:String){
        scheduleNotification(workshopName)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelWorkshops()
        }

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID+1)
            .setContentTitle("Workshop Nearby")
            .setContentText("Workshop: ${workshopName} is nearby")
            .setSmallIcon(R.drawable.ic_baseline_bookmark_24)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define

            notify(2, notification)
        }*/


    }

    private fun sendNotificationAboutWorkshop(key:String){
        val database = FirebaseFirestore.getInstance()
        database.collection("workshops").document(key).get().addOnSuccessListener {
            val workshop = it.toObject(Workshop::class.java)
            if(workshop!=null) {
                sendNotification(workshop.name!!)
            }
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
    private fun createNotificationChannelWorkshops(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID+1, "Workshop Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
    private fun scheduleNotification(name:String){
        val notificationIntent = Intent(this, NotificationPublisher::class.java)
        notificationIntent.putExtra("text", name)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val futureInMillis: Long = SystemClock.elapsedRealtime() + 500
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureInMillis,pendingIntent)
    }

    private fun getWorkshopsNearby(lat:Double,lon:Double){
        geoQuery.center=GeoLocation(lat,lon)
        geoQuery.radius=0.25
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(context: Context){
        if(PermissionHelper.isLocationPermissionGranted(context)){
            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            /*fusedLocationService.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())*/

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = true
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(criteria, true)

            if(provider!=null){
                locationManager.requestLocationUpdates(provider,1,0.1f
                ,listener)
            }
        }
        else{
            Toast.makeText(context,"Permisije za lokaciju nisu dostupne, dozvoli lokaciju u opcijama, pa upali servis iz ponovo sa ekrana Podesavanja",Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateLocationFirebase(location:DatabaseLocation){
        databaseReference.child(uid!!).setValue(location)
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        if(PermissionHelper.isLocationPermissionGranted(this)) {
            locationManager.removeUpdates(listener)
        }
        Log.d("BGSERVICE","UGASIO SE")
        super.onDestroy()
    }




}