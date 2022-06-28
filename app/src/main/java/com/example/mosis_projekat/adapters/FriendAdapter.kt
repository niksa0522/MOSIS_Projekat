package com.example.mosis_projekat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mosis_projekat.databinding.ItemFriendBinding
import com.example.mosis_projekat.databinding.ItemReviewBinding
import com.example.mosis_projekat.firebase.databaseModels.Review
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.models.UserWithID

class FriendAdapter(private val friends:List<UserWithID>,private val listener:FriendClickListener) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    interface FriendClickListener{
        fun onFriendClick(uid:String)
    }

    class ViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendAdapter.ViewHolder {
        val itemBinding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FriendAdapter.ViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(friends[position].user.imageUrl).into(holder.binding.imageProfile)
        holder.binding.textName.text = "${friends[position].user.fname} ${friends[position].user.lname}"
        holder.itemView.setOnClickListener {
            listener.onFriendClick(friends[position].id)
        }
    }
}