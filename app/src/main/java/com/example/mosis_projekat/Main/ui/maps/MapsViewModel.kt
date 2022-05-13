package com.example.mosis_projekat.Main.ui.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.Main.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databaseModels.DatabaseLocation
import com.example.mosis_projekat.databaseModels.DatabaseLocationWithUID
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MapsViewModel : ViewModel() {

    private val TAG = MainActivity::class.java.simpleName

    private val usersMap: MutableMap<String,DatabaseLocation> = mutableMapOf()
    private val friendsMap: MutableMap<String,DatabaseLocation> = mutableMapOf()

    private val _users = MutableLiveData<MutableMap<String,DatabaseLocation>>()
    private val _friends = MutableLiveData<MutableMap<String,DatabaseLocation>>()
    val friends: LiveData<MutableMap<String,DatabaseLocation>> = _friends
    val users: LiveData<MutableMap<String,DatabaseLocation>> = _users
    private var friendsList = mutableListOf<String>()




    init{
        _users.value = mutableMapOf<String,DatabaseLocation>()
        _friends.value = mutableMapOf<String,DatabaseLocation>()
        GetFriends()
    }



    fun setMapStyle(map: GoogleMap,context: Context){
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
    fun GetFriends(){
        val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth: FirebaseAuth = Firebase.auth
        val uid = auth.currentUser?.uid ?: return
        val friendList = database.reference.child("friends").child(uid).get().addOnSuccessListener {
            val listChildren = it.children
            for(l in listChildren)
                l.key?.let { it1 -> friendsList.add(it1) }
            GetLocations(database,uid)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun GetLocations(database: FirebaseDatabase,uid:String){
        database.reference.child("locations").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val loc = snapshot.getValue<DatabaseLocation>()
                val key = snapshot.key
                if((key!!) != uid) {
                    if (friendsList.contains(snapshot.key)) {
                        friendsMap.set(key, loc!!)
                        _friends.value=friendsMap
                    } else {
                        usersMap.set(key, loc!!)
                        _users.value=usersMap
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val loc = snapshot.getValue<DatabaseLocation>()
                val key = snapshot.key
                if((key!!) != uid) {
                    if (friendsList.contains(key)) {
                        friendsMap.set(key, loc!!)
                        _friends.value=friendsMap
                    } else {
                        usersMap.set(key, loc!!)
                        _users.value=usersMap
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val key = snapshot.key
                if ((key!!) != uid) {
                    if (friendsList.contains(key)) {
                        friendsMap.remove(key)
                        _friends.value=friendsMap
                    } else {
                        usersMap.remove(key)
                        _users.value=usersMap
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}
