package com.example.mosis_projekat.screens.workshopList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mosis_projekat.R
import com.example.mosis_projekat.adapters.WorkshopListAdapter
import com.example.mosis_projekat.databinding.FragmentSearchWorkshopBinding
import com.example.mosis_projekat.databinding.FragmentWorkshopListBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel

class WorkshopListFragment : Fragment() {

    private val viewModel: WorkshopListViewModel by viewModels()
    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()

    private var _binding: FragmentWorkshopListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WorkshopListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkshopListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.workshopsWithID.observe(viewLifecycleOwner){
            adapter = WorkshopListAdapter(it)
            binding.recyclerWorkshops.adapter = adapter
        }
        binding.recyclerWorkshops.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerWorkshops.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}