package com.example.mosis_projekat.screens.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.helpers.RankHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RankingMainViewModel : ViewModel() {
    private val _rank = MutableLiveData<String>()
    val rank: LiveData<String> = _rank

    fun getRank(){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth = Firebase.auth
        val uid = auth.uid
        if(uid!=null){
            mDatabase.reference.child("ranks").child(uid).get().addOnSuccessListener {
                val rank  = it.getValue(Int::class.java)
                if(rank!=null){
                    _rank.value = RankHelper.getRank(rank)
                }
            }
        }
    }
}