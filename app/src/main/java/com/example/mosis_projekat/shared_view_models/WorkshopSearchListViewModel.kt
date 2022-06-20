package com.example.mosis_projekat.shared_view_models

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.Workshop
import com.example.mosis_projekat.models.WorkshopWithID
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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


    private val _workshopsWithID = MutableLiveData<MutableList<WorkshopWithID>>()
    val workshopsWithID: LiveData<MutableList<WorkshopWithID>> = _workshopsWithID

    fun setValues(wn:String, wd:Int?, wp:Float,wr:Float){
        workshopName=wn
        workshopDistance=wd
        workshopPrice=wp
        workshopRating=wr
        Log.d("WORKSHOPSTATS",workshopName)
        Log.d("WORKSHOPSTATS",workshopTypes.toString())
        Log.d("WORKSHOPSTATS",workshopDistance.toString())
        Log.d("WORKSHOPSTATS",workshopPrice.toString())
        Log.d("WORKSHOPSTATS",workshopRating.toString())
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