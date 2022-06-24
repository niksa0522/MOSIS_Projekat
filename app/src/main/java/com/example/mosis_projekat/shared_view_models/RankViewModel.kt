package com.example.mosis_projekat.shared_view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.helpers.RankHelper
import com.example.mosis_projekat.models.UserWithRank
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class RankViewModel: ViewModel() {
    private val _rank = MutableLiveData<String>()
    val rank: LiveData<String> = _rank

    private val _globalRanks = MutableLiveData<MutableList<UserWithRank>>()
    val globalRanks:LiveData<MutableList<UserWithRank>> = _globalRanks

    private val _friendRanks = MutableLiveData<MutableList<UserWithRank>>()
    val friendRanks:LiveData<MutableList<UserWithRank>> = _friendRanks


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
    fun getRanks(){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val list = mutableListOf<UserWithRank>()
        mDatabase.reference.child("ranks").orderByValue().limitToFirst(20).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    if(postSnapshot.key!=null) {
                        mDatabase.reference.child("users").child(postSnapshot.key!!).get().addOnSuccessListener {
                            val user = it.getValue(User::class.java)
                            if(user!=null){
                                list.add(UserWithRank(user,postSnapshot.key!!,postSnapshot.getValue(Int::class.java)!!))
                                _globalRanks.value=list
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun getFriendRanks(){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth: FirebaseAuth = Firebase.auth
        val uid = auth.currentUser?.uid ?: return
        val friendsList = mutableListOf<String>()
        mDatabase.reference.child("friends").child(uid).get().addOnSuccessListener {
            val listChildren = it.children
            for(l in listChildren)
                l.key?.let { it1 -> friendsList.add(it1) }
            getRanksInList(friendsList)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    private fun getRanksInList(list:MutableList<String>){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val userList = mutableListOf<UserWithRank>()
        mDatabase.reference.child("ranks").orderByValue().addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    if(postSnapshot.key!=null && list.contains(postSnapshot.key)) {
                        mDatabase.reference.child("users").child(postSnapshot.key!!).get().addOnSuccessListener {
                            val user = it.getValue(User::class.java)
                            if(user!=null){
                                userList.add(UserWithRank(user,postSnapshot.key!!,postSnapshot.getValue(Int::class.java)!!))
                                _friendRanks.value=userList
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