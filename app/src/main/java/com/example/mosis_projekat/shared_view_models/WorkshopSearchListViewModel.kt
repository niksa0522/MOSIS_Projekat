package com.example.mosis_projekat.shared_view_models

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.Review
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.firebase.databaseModels.Workshop
import com.example.mosis_projekat.firebase.realtimeDB.RealtimeHelper
import com.example.mosis_projekat.models.WorkshopWithID
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import java.util.*

class WorkshopSearchListViewModel : ViewModel() {

    var workshopTypes = ArrayList<String>()
    var workshopName:String = ""
    var workshopDistance:Int? = 0
    var workshopPrice: Float = 0f
    var workshopRating: Float = 0f
    var currentLocation: LatLng?=null

    private var workshopList = mutableListOf<Workshop?>()
    private var workshopIdList = mutableListOf<String?>()
    //private var selectedWorkshop:WorkshopWithID? = null

    private val _selectedWorkshop = MutableLiveData<WorkshopWithID>()
    val selectedWorkshop: LiveData<WorkshopWithID> = _selectedWorkshop;

    private val _currentUserReview = MutableLiveData<Review?>()
    val currentUserReview: LiveData<Review?> = _currentUserReview


    private val _workshopsWithID = MutableLiveData<MutableList<WorkshopWithID>>()
    val workshopsWithID: LiveData<MutableList<WorkshopWithID>> = _workshopsWithID

    fun setValues(wn:String, wd:Int?, wp:Float,wr:Float){
        workshopName=wn
        workshopDistance=wd
        workshopPrice=wp
        workshopRating=wr
    }

    fun setSelected(workshopWithID: WorkshopWithID){
        _selectedWorkshop.value = workshopWithID
        findCurrentUserReview()
    }
    fun getSelected():WorkshopWithID?{
        return _selectedWorkshop.value
    }
    fun setSelected(workshopID: String){
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("workshops")
        collection.document(workshopID).get().addOnSuccessListener {
            val workshop = it.toObject(Workshop::class.java)
            if(workshop!=null) {
                _selectedWorkshop.value = WorkshopWithID(workshop, workshopID)
                findCurrentUserReview()
            }
        }
    }

    fun setReview(comment:String,rating:Float,oldRating:Float,isNew:Boolean){
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        if(uid!=null){
            if(_selectedWorkshop.value!=null){
                val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
                mDatabase.reference.child("users").child(uid).get().addOnSuccessListener {
                    val user = it.getValue(User::class.java)
                    if(user!=null) {
                        val review = Review(user.fname +" "+user.lname,comment,rating)
                        _currentUserReview.value=review
                        val db = FirebaseFirestore.getInstance()
                        val workshopRef = db.collection("workshops").document(selectedWorkshop.value!!.id)
                        val ratingsRef = workshopRef.collection("reviews").document(uid)
                                db.runTransaction {
                                    val snapshot = it.get(workshopRef)


                                    var reviewNum = snapshot.get("numRatings",Int::class.java)!!
                                    var avgRating = snapshot.get("avgRating",Float::class.java)!!
                                    val uploaderUID = snapshot.getString("uploaderUID")!!
                                    val oldRatingTotal = avgRating * reviewNum
                                    var newRatingTotal = 0f
                                    if(isNew){
                                        reviewNum++
                                        newRatingTotal = (oldRatingTotal+rating)/reviewNum
                                        RealtimeHelper.updateRating(uid,5)
                                        RealtimeHelper.updateRating(uploaderUID!!,3)
                                    }
                                    else{
                                        newRatingTotal = (oldRatingTotal-oldRating+rating)/reviewNum
                                    }
                                    it.update(workshopRef,"numRatings",reviewNum)
                                    it.update(workshopRef,"avgRating",newRatingTotal)
                                    it.set(ratingsRef,review)
                                    newRatingTotal
                                }.addOnSuccessListener {
                                    val workshop = _selectedWorkshop.value!!.copy()
                                    workshop.workshop.avgRating=it
                                    _selectedWorkshop.value=workshop
                                }
                    }
                }

            }
        }
    }
    fun deleteReview(rating:Float){
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        if(uid!=null){
            if(_selectedWorkshop.value!=null){
                val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
                mDatabase.reference.child("users").child(uid).get().addOnSuccessListener {
                    val user = it.getValue(User::class.java)
                    if(user!=null) {
                        val db = FirebaseFirestore.getInstance()
                        val workshopRef = db.collection("workshops").document(selectedWorkshop.value!!.id)
                        _currentUserReview.value=null
                        val ratingsRef = workshopRef.collection("reviews").document(uid)
                        workshopRef.get().addOnSuccessListener {
                            val workshop = it.toObject(Workshop::class.java)
                            if(workshop!=null){
                                db.runTransaction {
                                    var reviewNum = workshop.numRatings
                                    reviewNum--
                                    val oldRatingTotal = workshop.avgRating * workshop.numRatings
                                    var newRatingTotal = (oldRatingTotal-rating)/reviewNum
                                    workshop.numRatings=reviewNum
                                    workshop.avgRating=newRatingTotal
                                    it.set(workshopRef,workshop)
                                    it.delete(ratingsRef)
                                    RealtimeHelper.updateRating(uid,-5)
                                    RealtimeHelper.updateRating(workshop.uploaderUID!!,-2)
                                    newRatingTotal
                                }.addOnSuccessListener {
                                    val workshop = _selectedWorkshop.value!!.copy()
                                    workshop.workshop.avgRating=it
                                    _selectedWorkshop.value=workshop
                                }
                            }
                        }

                    }
                }

            }
        }
    }
    fun findCurrentUserReview(){
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid
        if(uid!=null){
            if(_selectedWorkshop.value!=null){
                val db = FirebaseFirestore.getInstance()
                val collection = db.collection("workshops").document(selectedWorkshop.value!!.id).collection("reviews").document(uid)
                    .get().addOnCompleteListener(){
                        val document = it.result
                        if(document.exists()){
                            val review = document.toObject(Review::class.java)
                            _currentUserReview.value = review!!
                        }
                        else{
                            _currentUserReview.value=null
                        }
                    }
            }
        }
    }

    fun getWorkshops(){
        _workshopsWithID.value = mutableListOf()
        workshopList = mutableListOf<Workshop?>()
        workshopIdList = mutableListOf<String?>()
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("workshops")
        val querys = mutableListOf<Query>()
        if(workshopName!=""){
            val tempQuery = collection.whereArrayContains("keywords",
                workshopName.lowercase(Locale.getDefault())
            )
            querys.add(tempQuery)
        }
        if(workshopTypes.isNotEmpty()){
            var tempQuery = collection.whereIn("type",workshopTypes)
            querys.add(tempQuery)
        }
        if(workshopPrice!=0f){
            val tempQuery = collection.whereLessThanOrEqualTo("price",workshopPrice)
            querys.add(tempQuery)
        }
        if(workshopRating!=0f){
            val tempQuery = collection.whereGreaterThanOrEqualTo("avgRating",workshopRating)
            querys.add(tempQuery)
        }
        if(querys.isEmpty()){
            val queryFinal = collection
            queryFinal.get().addOnCompleteListener {
                if(it.isSuccessful){
                    for(document in it.result){
                        workshopList.add(document.toObject(Workshop::class.java))
                        workshopIdList.add(document.id)
                    }
                    trimByDistance()
                    val finalList = mutableListOf<WorkshopWithID>()
                    for(i in workshopIdList.indices){
                        finalList.add(WorkshopWithID(workshopList[i]!!,workshopIdList[i]!!))
                    }
                    _workshopsWithID.value=finalList
                }
            }
        }
        else{
            val taskList = mutableListOf<Task<QuerySnapshot>>()
            for(q in querys){
                val tempTask = q.get()
                taskList.add(tempTask)
            }
            val completetTasks = Tasks.whenAllSuccess<Object>(taskList).addOnSuccessListener {
                var queryWorkshops: MutableList<Workshop?> = mutableListOf()
                var queryWorkshopsId: MutableList<String?> = mutableListOf()
                val recoveredWorkshops: MutableList<Workshop?> = mutableListOf()
                val recoveredWorkshopsId: MutableList<String?> = mutableListOf()
                var firstArray = true
                for (o in it) {
                    val snapshot = o as QuerySnapshot
                    for (documentSnapshot in snapshot) {
                        val workshop = documentSnapshot.toObject(Workshop::class.java)
                        if (firstArray) {
                            queryWorkshops.add(workshop)
                            queryWorkshopsId.add(documentSnapshot.id)
                        } else if (recoveredWorkshops.contains(workshop)) {
                            queryWorkshops.add(workshop)
                            queryWorkshopsId.add(documentSnapshot.id)
                        }
                    }
                    firstArray = false
                    recoveredWorkshops.clear()
                    recoveredWorkshopsId.clear()
                    recoveredWorkshops.addAll(queryWorkshops)
                    recoveredWorkshopsId.addAll(queryWorkshopsId)
                    queryWorkshops = mutableListOf()
                    queryWorkshopsId = mutableListOf()
                }
                workshopList.addAll(recoveredWorkshops)
                workshopIdList.addAll(recoveredWorkshopsId)
                trimByDistance()
                val finalList = mutableListOf<WorkshopWithID>()
                for(i in workshopIdList.indices){
                    finalList.add(WorkshopWithID(workshopList[i]!!,workshopIdList[i]!!))
                }
                _workshopsWithID.value=finalList
            }
        }
    }

    private fun trimByDistance(){
        if(currentLocation!=null && workshopDistance!=null && workshopDistance!=0) {
            val size = workshopIdList.size-1
            for (i in size downTo 0) {
                val dist = FloatArray(1)
                Location.distanceBetween(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude,
                    workshopList[i]!!.location!!.lat!!,
                    workshopList[i]!!.location!!.lon!!, dist
                )
                if(dist[0]> workshopDistance!!){
                    workshopIdList.removeAt(i)
                    workshopList.removeAt(i)
                }
            }
        }
    }
}