package com.example.mosis_projekat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.ItemReviewBinding
import com.example.mosis_projekat.databinding.ItemUserRankBinding
import com.example.mosis_projekat.firebase.databaseModels.User
import com.example.mosis_projekat.helpers.RankHelper
import com.example.mosis_projekat.models.UserWithRank

class UserRanksListAdapter(private val users:List<UserWithRank>, private val listener:UserClickInterface) :
    RecyclerView.Adapter<UserRanksListAdapter.ViewHolder>() {

    interface UserClickInterface{
        fun onItemClicked(uid:String)
    }

    class ViewHolder(val binding: ItemUserRankBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemUserRankBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserRanksListAdapter.ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textRank.text = holder.itemView.context.getString(R.string.rankInfo,users[position].rank,RankHelper.getRank(users[position].rank))
        holder.binding.textName.text = "${users[position].user.fname} ${users[position].user.lname}"
        holder.itemView.setOnClickListener {
            listener.onItemClicked(users[position].id)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}