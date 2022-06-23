package com.example.mosis_projekat.screens.workshopInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WorkshopInfoViewModel : ViewModel() {

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved


    fun checkIfSaved(workshopID:String){
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null){
            val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
            mDatabase.reference.child("saved").child(uid).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    _isSaved.value = snapshot.hasChild(workshopID)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }
    fun clickOnSaved(workshopID: String){
        if(_isSaved.value!=null) {
            if (_isSaved.value == true){
                removeSaved(workshopID)
            }else{
                addSaved(workshopID)
            }
        }
    }
    private fun removeSaved(workshopID: String){
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null){
            val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
            mDatabase.reference.child("saved").child(uid).child(workshopID).removeValue()
            _isSaved.value=false
        }
    }
    private fun addSaved(workshopID: String){
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null){
            val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
            mDatabase.reference.child("saved").child(uid).child(workshopID).setValue("")
            _isSaved.value=true
        }
    }

}