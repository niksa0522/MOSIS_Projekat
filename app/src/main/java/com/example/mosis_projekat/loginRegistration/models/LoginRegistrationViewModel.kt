package com.example.mosis_projekat.loginRegistration.models

import android.graphics.Bitmap
import android.text.Editable
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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



     fun createAccount(activity: FragmentActivity){
        val email = "${username.value}@mosisProjekat.com"
        auth.createUserWithEmailAndPassword(email,password.value!!)
            .addOnCompleteListener(activity) {
                    task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    //val i: Intent = Intent(this,MainClass::class.java)
                    //startActivity(i)
                }
                else{
                    Toast.makeText(activity, "Creation Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(activity: FragmentActivity){
        val email = "${username.value}@mosisProjekat.com"
        auth.signInWithEmailAndPassword(email,password.value!!)
            .addOnCompleteListener(activity){  task->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    //val i: Intent = Intent(this,MainClass::class.java)
                    //startActivity(i)
                }
                else{
                    Toast.makeText(activity, "Log In Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }


}