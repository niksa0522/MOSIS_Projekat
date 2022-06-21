package com.example.mosis_projekat.screens.workshopList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mosis_projekat.R
import com.example.mosis_projekat.adapters.WorkshopListAdapter
import com.example.mosis_projekat.databinding.FragmentWorkshopListBinding
import com.example.mosis_projekat.models.WorkshopWithID
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel

class WorkshopListFragment : Fragment(),WorkshopListAdapter.WorkshopListClickCallback {

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
            val list = mutableListOf<WorkshopWithID>()
            list.addAll(it)
            adapter = WorkshopListAdapter(list,this)
            binding.recyclerWorkshops.adapter = adapter
        }
        binding.recyclerWorkshops.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerWorkshops.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        )
        )
        setHasOptionsMenu(true)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun OnItemClick(workshop:WorkshopWithID) {
        sharedViewModel.setSelected(workshop)
        findNavController().navigate(R.id.action_workshopListFragment_to_workshopMainFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.workshop_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.location_rising -> {
                adapter.sortLocationRising(sharedViewModel.currentLocation!!)
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            R.id.location_falling -> {
                adapter.sortLocationFalling(sharedViewModel.currentLocation!!)
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            R.id.workprice_rising -> {
                adapter.sortPriceRising()
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            R.id.workprice_falling -> {
                adapter.sortPriceFalling()
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            R.id.rating_rising -> {
                adapter.sortRatingRising()
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            R.id.rating_falling -> {
                adapter.sortRatingFalling()
                adapter.notifyDataSetChanged()
                binding.recyclerWorkshops.layoutManager?.scrollToPosition(0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}