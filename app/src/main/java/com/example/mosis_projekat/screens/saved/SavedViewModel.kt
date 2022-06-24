package com.example.mosis_projekat.screens.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.Workshop
import com.example.mosis_projekat.models.WorkshopWithID
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SavedViewModel : ViewModel() {
    private val _workshopList = MutableLiveData<MutableList<WorkshopWithID>>()
    val workshopList: LiveData<MutableList<WorkshopWithID>> = _workshopList

    fun getWorkshops(){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null){
            val workshopList = mutableListOf<WorkshopWithID>()
            mDatabase.reference.child("saved").child(uid).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for( c in snapshot.children){
                        if(c.key!=null) {
                            db.collection("workshops").document(c.key!!).get()
                                .addOnSuccessListener {
                                    val workshop = it.toObject(Workshop::class.java)
                                    if(workshop!=null) {
                                        workshopList.add(
                                            WorkshopWithID(
                                                workshop,
                                                it.id
                                            )
                                        )
                                        _workshopList.value=workshopList
                                    }
                                }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
}