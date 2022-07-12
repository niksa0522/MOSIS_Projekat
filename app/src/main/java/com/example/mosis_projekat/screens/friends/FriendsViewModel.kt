package com.example.mosis_projekat.screens.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.models.UserWithID
import com.example.mosis_projekat.models.UserWithRank
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class FriendsViewModel : ViewModel() {

    private val _friends = MutableLiveData<MutableList<UserWithID>>()
    val friends:LiveData<MutableList<UserWithID>> = _friends

    fun getFriends(){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val auth: FirebaseAuth = Firebase.auth
        val uid = auth.currentUser?.uid ?: return
        var friendsList = mutableListOf<String>()
        mDatabase.reference.child("friends").child(uid).addValueEventListener((object:
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                friendsList = mutableListOf<String>()
                val listChildren = snapshot.children
                for(l in listChildren)
                    l.key?.let { it1 -> friendsList.add(it1) }
                getFriendsInList(friendsList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }))
        /*mDatabase.reference.child("friends").child(uid).get().addOnSuccessListener {
            val listChildren = it.children
            for(l in listChildren)
                l.key?.let { it1 -> friendsList.add(it1) }
            getFriendsInList(friendsList)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }*/
    }
    private fun getFriendsInList(list:MutableList<String>){
        val mDatabase =
            FirebaseDatabase.getInstance("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
        val userList = mutableListOf<UserWithID>()
        for(l in list){
            mDatabase.reference.child("users").child(l).get().addOnSuccessListener {
                val user = it.getValue(User::class.java)
                if(user!=null){
                    userList.add(UserWithID(user,l))
                    _friends.value=userList
                }
            }
        }
    }

}