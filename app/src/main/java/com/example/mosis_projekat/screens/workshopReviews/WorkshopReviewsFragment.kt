package com.example.mosis_projekat.screens.workshopReviews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.R
import com.example.mosis_projekat.adapters.ReviewsAdapter
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel


class WorkshopReviewsFragment : Fragment() {

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()
    private val viewModel: WorkshopReviewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(sharedViewModel.selectedWorkshop.value!=null) {
            viewModel.getReviews(sharedViewModel.selectedWorkshop.value!!.id)
        }
        return inflater.inflate(R.layout.fragment_workshop_reviews, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        viewModel.reviews.observe(viewLifecycleOwner){
            recyclerView.adapter = ReviewsAdapter(it)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        )
        )
    }

}