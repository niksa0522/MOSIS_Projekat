package com.example.mosis_projekat.screens.workshopReviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mosis_projekat.firebase.databaseModels.Review
import com.google.firebase.firestore.FirebaseFirestore

class WorkshopReviewsViewModel : ViewModel() {

    private val _reviews= MutableLiveData<MutableList<Review>>()
    val reviews: LiveData<MutableList<Review>> = _reviews

    fun getReviews(workshopID:String){
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("workshops").document(workshopID).collection("reviews").get().addOnSuccessListener {
            val list= mutableListOf<Review>()
            for(d in it.documents){
                val review = d.toObject(Review::class.java)
                list.add(review!!)
            }
            _reviews.value=list
        }
    }

}