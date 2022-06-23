package com.example.mosis_projekat.screens.maps

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mosis_projekat.activities.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.firebase.databaseModels.DatabaseLocation
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.firebase.databaseModels.Workshop
import com.example.mosis_projekat.helpers.BitmapHelper
import com.example.mosis_projekat.models.WorkshopWithID
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MapsViewModel : ViewModel(), GoogleMap.OnMarkerClickListener {

    private val TAG = MainActivity::class.java.simpleName

    /*private val usersMap: MutableMap<String,DatabaseLocation> = mutableMapOf()
    private val friendsMap: MutableMap<String,DatabaseLocation> = mutableMapOf()*/

    /*private val _users = MutableLiveData<MutableMap<String,DatabaseLocation>>()
    private val _friends = MutableLiveData<MutableMap<String,DatabaseLocation>>()
    val friends: LiveData<MutableMap<String,DatabaseLocation>> = _friends
    val users: LiveData<MutableMap<String,DatabaseLocation>> = _users*/
    private var friendsList = mutableListOf<String>()

    private lateinit var map: GoogleMap
    private val usersMap: MutableMap<String, Marker> = mutableMapOf()
    private val friendsMap: MutableMap<String, Marker> = mutableMapOf()
    private val workshopMap: MutableMap<WorkshopWithID,Marker> = mutableMapOf()
    private val storage = Firebase.storage
    private lateinit var navController: NavController




    /*init{
        _users.value = mutableMapOf<String,DatabaseLocation>()
        _friends.value = mutableMapOf<String,DatabaseLocation>()
        GetFriends()
    }*/


    fun setMap(map:GoogleMap,context: Context, navController: NavController){
        this.map=map
        this.navController=navController
        setMapStyle(context)
        map.setMaxZoomPreference(18f)
        map.setMinZoomPreference(14f)
        GetFriends(context)
        map.setOnMarkerClickListener(this)
        getWorkshopLocations(context)
    }


    fun setMapStyle(context: Context){
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    fun updateLocationFirebase(loc: DatabaseLocation){
        val auth: FirebaseAuth = Firebase.auth
        val uid = auth.currentUser?.uid ?: return
        val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.reference.child("locations").child(uid).setValue(loc)
    }
    fun GetFriends(context: Context){
        val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth: FirebaseAuth = Firebase.auth
        val uid = auth.currentUser?.uid ?: return
        val friendList = database.reference.child("friends").child(uid).get().addOnSuccessListener {
            val listChildren = it.children
            for(l in listChildren)
                l.key?.let { it1 -> friendsList.add(it1) }
            GetUserLocations(database,uid,context)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }


    fun setupFriendMarker(uid:String,database: FirebaseDatabase, context: Context, m:Marker) {
        val userRef = database.reference.child("users").child(uid).get()
        userRef.addOnCompleteListener {
            val user = it.result.getValue<User>()
            //stavi da on click otvara novi fragment ili popup sa user info
            Glide.with(context).asBitmap().load(user?.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bitmap: Bitmap? = BitmapHelper.createBitmap(resource,context)
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap!!))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                })
            //napravi da marker izgleda lepo*/
        }
    }


    private fun GetUserLocations(database: FirebaseDatabase, uid:String, context:Context){
        database.reference.child("locations").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val loc = snapshot.getValue<DatabaseLocation>()
                val key = snapshot.key
                if((key!!) != uid) {
                    val m: Marker? = map.addMarker(MarkerOptions().position(LatLng(loc!!.lat!!, loc.lon!!)))
                    if (friendsList.contains(snapshot.key)) {
                        friendsMap.set(key, m!!)
                        setupFriendMarker(key,database,context,m)
                    }
                    else {
                        usersMap.set(key, m!!)

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val loc = snapshot.getValue<DatabaseLocation>()
                val key = snapshot.key
                if((key!!) != uid) {
                    if (friendsList.contains(key)) {
                        val m: Marker? = friendsMap.get(key)
                        if(m!=null) {
                            m.apply {
                                position= LatLng(loc!!.lat!!, loc.lon!!)
                            }
                        }
                        else{
                            val m: Marker? = map.addMarker(MarkerOptions().position(LatLng(loc!!.lat!!, loc.lon!!)))
                            friendsMap.set(key, m!!)
                            setupFriendMarker(key,database,context,m)
                            //dodaj da izgleda lepo
                        }
                        /*friendsMap.set(key, loc!!)
                        _friends.value=friendsMap*/
                    } else {
                        val m: Marker? = usersMap.get(key)
                        if(m!=null) { // ako marker postoji
                            m.apply {
                                position= LatLng(loc!!.lat!!, loc.lon!!)
                            }
                        }
                        else{ //ako ne postoji dodaj
                            val m: Marker? = map.addMarker(MarkerOptions().position(LatLng(loc!!.lat!!, loc.lon!!)))
                            usersMap.set(key, m!!)
                        }
                        /*usersMap.set(key, loc!!)
                        _users.value=usersMap*/
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val key = snapshot.key
                if ((key!!) != uid) {
                    if (friendsList.contains(key)) {
                        friendsMap.get(key)?.remove()
                        friendsMap.remove(key)
                    } else {
                        usersMap.get(key)?.remove()
                        usersMap.remove(key)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onMarkerClick(m: Marker): Boolean {

        if(friendsMap.containsValue(m)){
            val key = getKey(friendsMap,m)
            val bundle = Bundle()
            bundle.putString("uid",key)
            navController.navigate(R.id.action_nav_maps_to_viewFriendFragment,bundle)
        }else if(workshopMap.containsValue(m)){
            //otvori workshop
            val bundle = Bundle()
            val key = getWorkshopKey(workshopMap,m)
            bundle.putString("id",key)
            navController.navigate(R.id.action_nav_maps_to_workshopMainFragment,bundle)
        }
        return false
    }
    private fun getKey(map: Map<String, Marker>, target: Marker): String? {
        for ((key, value) in map)
        {
            if (target == value) {
                return key
            }
        }
        return null
    }
    fun getWorkshopKey(map: Map<WorkshopWithID, Marker>, target: Marker): String? {
        for ((key, value) in map)
        {
            if (target == value) {
                return key.id
            }
        }
        return null
    }



    fun getWorkshopLocations(context: Context){
        val database =FirebaseFirestore.getInstance()
        database.collection("workshops").addSnapshotListener { value, error ->
            if(error!=null){
             return@addSnapshotListener
            }
            val workshops = ArrayList<Workshop>()
            for(doc in value!!){
                val workshop = doc.toObject<Workshop>()
                workshops.add(workshop)
                val m: Marker? = map.addMarker(MarkerOptions().position(LatLng(workshop.location?.lat!!, workshop.location?.lon!!)).
                icon(BitmapDescriptorFactory.defaultMarker(getColor(workshop))))
                workshopMap.set(WorkshopWithID(workshop,doc.id),m!!)
            }
        }
    }
    private fun getColor(w:Workshop):Float{
        return when(w.type){
            "Mehanicar" -> BitmapDescriptorFactory.HUE_BLUE
            "Limarija" -> BitmapDescriptorFactory.HUE_GREEN
            "Farba" -> BitmapDescriptorFactory.HUE_RED
            "Vulkanizer" -> BitmapDescriptorFactory.HUE_ORANGE
            "Pranje i Ciscenje" ->BitmapDescriptorFactory.HUE_MAGENTA
            else -> BitmapDescriptorFactory.HUE_YELLOW
        }
    }

}
