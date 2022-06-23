package com.example.mosis_projekat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.databinding.ItemReviewBinding
import com.example.mosis_projekat.databinding.ItemWorkshopBinding
import com.example.mosis_projekat.firebase.databaseModels.Review

class ReviewsAdapter(private val reviews:List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ViewHolder {
        val itemBinding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewsAdapter.ViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textComment.setText(reviews[position].comment)
        holder.binding.textName.setText(reviews[position].userName)
        holder.binding.yourRatingBar.rating = reviews[position].rating
    }
}