package com.example.mosis_projekat.firebase.realtimeDB

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object RealtimeHelper {
    fun updateRating(uid:String,toUpdate:Int){
        val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val rating = mDatabase.reference.child("ranks").child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val rank = it.getValue(Int::class.java)!!
                mDatabase.reference.child("ranks").child(uid).setValue(rank+toUpdate)
            }else{
                mDatabase.reference.child("ranks").child(uid).setValue(toUpdate)
            }
        }

    }
    fun deleteLocation(){
        val mDatabase = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null && uid!=""){
            mDatabase.reference.child("locations").child(uid).removeValue()
        }
    }
}