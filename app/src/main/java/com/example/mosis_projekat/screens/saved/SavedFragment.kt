package com.example.mosis_projekat.screens.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.adapters.WorkshopListAdapter
import com.example.mosis_projekat.models.WorkshopWithID
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel


class SavedFragment : Fragment(),WorkshopListAdapter.WorkshopListClickCallback {

    private val sharedViewModel:WorkshopSearchListViewModel by activityViewModels()
    private val viewModel: SavedViewModel by viewModels()
    private lateinit var adapter: WorkshopListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel.getWorkshops()
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerWorkshops = view.findViewById<RecyclerView>(R.id.workshop_list)
        viewModel.workshopList.observe(viewLifecycleOwner){
            val list = mutableListOf<WorkshopWithID>()
            list.addAll(it)
            adapter = WorkshopListAdapter(list,this)
            recyclerWorkshops.adapter = adapter
        }
        recyclerWorkshops.layoutManager=LinearLayoutManager(requireContext())
        recyclerWorkshops.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        ))
    }

    override fun OnItemClick(workshop: WorkshopWithID) {
        sharedViewModel.setSelected(workshop)
        findNavController().navigate(R.id.action_savedFragment_to_workshopMainFragment)
    }


}