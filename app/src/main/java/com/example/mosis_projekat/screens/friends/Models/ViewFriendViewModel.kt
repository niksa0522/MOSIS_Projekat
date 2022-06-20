package com.example.mosis_projekat.screens.friends.Models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mosis_projekat.firebase.databaseModels.User
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ViewFriendViewModel : ViewModel() {

    private val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")

    private var uid: String? = null
    private val _picture = MutableLiveData<Bitmap>()

    val picture: LiveData<Bitmap> = _picture

    private val _Name = MutableLiveData<String>()

    val Name: LiveData<String> = _Name

    private val _phoneNum = MutableLiveData<String>()

    val phoneNum: LiveData<String> = _phoneNum

    //dodaj rank kasnije



    fun setUID(uid: String, context: Context){
        this.uid = uid
        GetData(context)
    }

    private fun GetData(context: Context){
        database.reference.child("users").child(uid!!).get().addOnCompleteListener {
            val user = it.result.getValue<User>()
            _Name.value = user?.fname +" " + user?.lname
            _phoneNum.value = user?.phoneNum ?: ""
            Glide.with(context).asBitmap().load(user?.imageUrl).into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    _picture.value = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
        }
    }

}