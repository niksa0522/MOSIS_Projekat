package com.example.mosis_projekat.Main.ui.maps.workshops

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.Main.MainActivity
import com.example.mosis_projekat.databaseModels.DatabaseLocation
import com.example.mosis_projekat.databaseModels.User
import com.example.mosis_projekat.databaseModels.Workshop
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddWorkshopViewModel : ViewModel() {


    private lateinit var auth: FirebaseAuth

    private val _picture =  MutableLiveData<Bitmap>()
    val picture:LiveData<Bitmap> = _picture

    init {
        auth = Firebase.auth
    }

    fun setPicture(picture: Bitmap){
        _picture.value=picture
    }

    fun addWorkshop(
        name:String, phoneNum:String, type:String, description:String, price: Float,
        location:DatabaseLocation){
        val userID: String = auth.currentUser?.uid ?: ""
        val workshop = Workshop(name=name,phone = phoneNum,type = type,description = description,price = price,uploaderUID = userID,
            location = location)
        workshop.uploaderUID=userID
        val database =FirebaseFirestore.getInstance()
        database.collection("workshops").add(workshop).addOnCompleteListener{
            if(it.isSuccessful){
                val key = it.result.id
                var storage = Firebase.storage
                var imageRef: StorageReference? = storage.reference.child("workshops").child("${key}.jpg")
                val baos = ByteArrayOutputStream()
                val bitmap = picture.value
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef!!.putBytes(data)
                val urlTask = uploadTask.continueWithTask{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        //kada je slika uploadovana uzima se njen url i uploaduje se user
                        val imageUrl = task.result.toString()
                        database.collection("workshops").document(key).update("imageUrl",imageUrl)
                    }
                }
            }
        }
        //Prvo postavi u firebase, pokupi key, pa onda upload sliku i stavi sta vec treba

        //Prvo se upload-uje slika

    }



}