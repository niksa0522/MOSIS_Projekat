package com.example.mosis_projekat.shared_view_models

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.activities.MainActivity
import com.example.mosis_projekat.firebase.databaseModels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class LoginRegistrationViewModel : ViewModel() {



    private lateinit var auth: FirebaseAuth

    private val _picture = MutableLiveData<Bitmap>()

    val picture: LiveData<Bitmap> = _picture

    private val _fName = MutableLiveData<String>()

    val fName: LiveData<String> = _fName

    private val _lName = MutableLiveData<String>()

    val lName: LiveData<String> = _lName

    private val _password = MutableLiveData<String>()

    val password: LiveData<String> = _password

    private val _username = MutableLiveData<String>()

    val username: LiveData<String> = _username

    private val _phoneNum = MutableLiveData<String>()

    val phoneNum: LiveData<String> = _phoneNum

    init {
        auth = Firebase.auth
    }

    fun setPicture(picture:Bitmap){
        _picture.value=picture
    }
    fun onFNameTextChanged(p0: Editable?){
        _fName.value = p0.toString()
    }
    fun onLNameTextChanged(p0: Editable?){
        _lName.value = p0.toString()
    }
    fun onUsernameTextChanged(p0: Editable?){
        _username.value = p0.toString()
    }
    fun onPasswordTextChanged(p0: Editable?){
        _password.value = p0.toString()
    }
    fun onPhoneNumTextChanged(p0: Editable?){
        _phoneNum.value = p0.toString()
    }



     fun createAccount(activity: FragmentActivity){
         if(checkData(false,activity)) {
             val email = "${username.value}@mosisProjekat.com"
             auth.createUserWithEmailAndPassword(email, password.value!!)
                 .addOnCompleteListener(activity) { task ->
                     if (task.isSuccessful) {
                         val user = auth.currentUser
                         UploadInfo(activity)
                     } else {
                         Toast.makeText(activity, "Creation Failed!", Toast.LENGTH_SHORT).show()
                     }
                 }
         }
    }

    fun login(activity: FragmentActivity){
        if(checkData(true,activity)) {
            val email = "${username.value}@mosisProjekat.com"
            auth.signInWithEmailAndPassword(email, password.value!!)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val i: Intent = Intent(activity, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        activity.startActivity(i)
                        activity.finish()
                    } else {
                        Toast.makeText(activity, "Log In Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun UploadInfo(activity: FragmentActivity){
        val userID: String = auth.currentUser?.uid ?: ""
        if(userID == "")
            Toast.makeText(activity, "Ovo nije trebalo da se desi!",Toast.LENGTH_SHORT).show()
        //Prvo se upload-uje slika
        var storage = Firebase.storage
        var imageRef: StorageReference? = storage.reference.child("users").child(userID).child("${username.value}.jpg")
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
                val user = User(fName.value,lName.value,imageUrl,_phoneNum.value)
                val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
                val userRef = database.reference.child("users").child(userID).setValue(user)
                database.reference.child("ranks").child(userID).setValue(0)
                val profileUpdate = userProfileChangeRequest {
                    displayName = "${fName.value} ${lName.value}"
                    photoUri = Uri.parse(imageUrl)
                }

                auth.currentUser!!.updateProfile(profileUpdate)
                val i: Intent = Intent(activity, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(i)
                activity.finish()
            }
        }



    }
    private fun checkData(login: Boolean, activity: FragmentActivity):Boolean{
        if(username.value == null || username.value == "")
        {
            Toast.makeText(activity, "Unesi Korisnicko Ime!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password.value == null || password.value == "")
        {
            Toast.makeText(activity, "Unesi Lozinku!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!login){
            if(fName.value == null || fName.value == "")
            {
                Toast.makeText(activity, "Unesi Ime!", Toast.LENGTH_SHORT).show()
                return false
            }
            if(lName.value == null || lName.value == "")
            {
                Toast.makeText(activity, "Unesi Prezime!", Toast.LENGTH_SHORT).show()
                return false
            }
            if(phoneNum.value == null || phoneNum.value == "")
            {
                Toast.makeText(activity, "Unesi Telefon!", Toast.LENGTH_SHORT).show()
                return false
            }
            if(picture.value == null){
                Toast.makeText(activity, "Potrebna je slika!", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }


}