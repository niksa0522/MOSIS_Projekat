package com.example.mosis_projekat.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.databinding.ItemWorkshopBinding
import com.example.mosis_projekat.models.WorkshopWithID
import com.google.android.gms.maps.model.LatLng

class WorkshopListAdapter(private var workshopList:MutableList<WorkshopWithID>,private val listener:WorkshopListClickCallback) :
    RecyclerView.Adapter<WorkshopListAdapter.ViewHolder>() {


    interface WorkshopListClickCallback{
        fun OnItemClick(workshop:WorkshopWithID)
    }

    class ViewHolder(val binding: ItemWorkshopBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemWorkshopBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textName.text = workshopList[position].workshop.name
        holder.binding.textType.text = workshopList[position].workshop.type
        holder.binding.ratingBar.rating = workshopList[position].workshop.avgRating
        holder.itemView.setOnClickListener {
            listener.OnItemClick(workshopList[position])
        }
    }

    override fun getItemCount(): Int {
        return workshopList.size
    }

    fun sortRatingFalling(){
        workshopList = workshopList.sortedByDescending { it.workshop.avgRating }.toMutableList()

    }
    fun sortRatingRising(){
        workshopList =workshopList.sortedBy { it.workshop.avgRating }.toMutableList()
    }
    fun sortLocationFalling(currentLocation: LatLng){
        val distanceResults = mutableListOf<FloatArray>()
        for(i in workshopList.indices){
            val resultsi = FloatArray(3)
            val lli = workshopList[i].workshop.location
            Location.distanceBetween(
                lli!!.lat!!,
                lli!!.lon!!,
                currentLocation.latitude,
                currentLocation.longitude,
                resultsi
            )
            distanceResults.add(resultsi)
        }
        for (i in 0 until workshopList.size - 1) {
            for (j in i + 1 until workshopList.size) {
                if (distanceResults[i][0] > distanceResults[j][0]) {
                    val temp: WorkshopWithID = workshopList[i]
                    workshopList[i] = workshopList[j]
                    workshopList[j] = temp
                }
            }
        }
    }
    private fun getLocationDistancesToCurrentLoc(currentLocation: LatLng):MutableList<FloatArray>{
        val distanceResults = mutableListOf<FloatArray>()
        for(i in workshopList.indices){
            val resultsi = FloatArray(3)
            val lli = workshopList[i].workshop.location
            Location.distanceBetween(
                lli!!.lat!!,
                lli!!.lon!!,
                currentLocation.latitude,
                currentLocation.longitude,
                resultsi
            )
            distanceResults.add(resultsi)
        }
        return distanceResults
    }
    fun sortLocationRising(currentLocation: LatLng){
        val distanceResults =getLocationDistancesToCurrentLoc(currentLocation)
        for (i in 0 until workshopList.size - 1) {
            for (j in i + 1 until workshopList.size) {
                if (distanceResults[i][0] < distanceResults[j][0]) {
                    val temp: WorkshopWithID = workshopList[i]
                    workshopList[i] = workshopList[j]
                    workshopList[j] = temp
                }
            }
        }
    }
    fun sortPriceFalling(){
        workshopList = workshopList.sortedByDescending { it.workshop.price }.toMutableList()
    }
    fun sortPriceRising(){
        workshopList = workshopList.sortedBy { it.workshop.price }.toMutableList()

    }
}