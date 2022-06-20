package com.example.mosis_projekat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.ItemWorkshopBinding
import com.example.mosis_projekat.databinding.ItemWorkshopTypeBinding
import com.example.mosis_projekat.models.WorkshopWithID

class WorkshopListAdapter(private val workshopList:List<WorkshopWithID>) :
    RecyclerView.Adapter<WorkshopListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemWorkshopBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemWorkshopBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textName.text = workshopList[position].workshop.name
        holder.binding.textType.text = workshopList[position].workshop.type
        holder.binding.ratingBar.rating = workshopList[position].workshop.avgRating
    }

    override fun getItemCount(): Int {
        return workshopList.size
    }
}