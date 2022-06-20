package com.example.mosis_projekat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.ItemWorkshopTypeBinding

class WorkshopTypeAdapter(private val context:Context,private val chosen:ArrayList<String>?) :
    RecyclerView.Adapter<WorkshopTypeAdapter.ViewHolder>() {

    private val list = context.resources.getStringArray(R.array.workshops_array).toList()
    private var checkedList: MutableList<Boolean> = mutableListOf()

    init {
        for(l in list) {
            checkedList.add(false)
        }
        if(chosen!=null){
            for(l in chosen){
                val i =list.indexOf(l)
                checkedList[i]=true
            }
        }
    }

    fun returnChecked():List<String>{
        val checkedStringList = mutableListOf<String>()
        for(i in checkedList.indices){
            if(checkedList[i]){
                checkedStringList.add(list[i])
            }
        }
        return checkedStringList
    }



    class ViewHolder(val binding:ItemWorkshopTypeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemWorkshopTypeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.workshopType.text = list[position]
        holder.binding.workshopChecked.isChecked=checkedList[position]
        holder.binding.workshopChecked.setOnCheckedChangeListener { compoundButton, b ->
            checkedList[position] = b
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}