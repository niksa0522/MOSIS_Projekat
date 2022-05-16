package com.example.mosis_projekat.Main.ui.maps

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
import com.example.mosis_projekat.Main.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databaseModels.DatabaseLocation
import com.example.mosis_projekat.databaseModels.User
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
            GetLocations(database,uid,context)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }


    fun dp(value: Float,context: Context): Int {
        return if (value == 0f) {
            0
        } else Math.ceil((context.getResources().getDisplayMetrics().density * value).toDouble()).toInt()
    }

    fun createBitmap(bitmap: Bitmap,context: Context): Bitmap?{
        var result: Bitmap? = null
        try {
            result = Bitmap.createBitmap(dp(62f,context), dp(76f,context), Bitmap.Config.ARGB_8888)
            result.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(result)
            val drawable: Drawable = context.getResources().getDrawable(R.drawable.livepin)
            drawable.setBounds(0, 0, dp(62f,context), dp(76f,context))
            drawable.draw(canvas)
            val roundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            val bitmapRect = RectF()
            canvas.save()
            //val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar)
            //Bitmap bitmap = BitmapFactory.decodeFile(path.toString()); /*generate bitmap here if your image comes from any url*/
            if (bitmap != null) {
                val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                val matrix = Matrix()
                val scale: Float = dp(52f,context) / bitmap.width.toFloat()
                matrix.postTranslate(dp(5f,context).toFloat(), dp(5f,context).toFloat())
                matrix.postScale(scale, scale)
                roundPaint.setShader(shader)
                shader.setLocalMatrix(matrix)
                bitmapRect[dp(5f,context).toFloat(), dp(5f,context).toFloat(), dp(52f + 5f,context).toFloat()] = dp(52f + 5f,context).toFloat()
                canvas.drawRoundRect(bitmapRect, dp(26f,context).toFloat(), dp(26f,context).toFloat(), roundPaint)
            }
            canvas.restore()
            try {
                canvas.setBitmap(null)
            } catch (e: Exception) {
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return result
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
                        val bitmap: Bitmap? = createBitmap(resource,context)
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap!!))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                })
            //napravi da marker izgleda lepo*/
        }
    }


    fun GetLocations(database: FirebaseDatabase,uid:String,context:Context){
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
        }
        return false
    }
    fun getKey(map: Map<String, Marker>, target: Marker): String? {
        for ((key, value) in map)
        {
            if (target == value) {
                return key
            }
        }
        return null
    }

}
