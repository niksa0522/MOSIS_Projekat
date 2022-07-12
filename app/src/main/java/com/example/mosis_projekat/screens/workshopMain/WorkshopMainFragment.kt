package com.example.mosis_projekat.screens.workshopMain

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.mosis_projekat.adapters.WorkshopPagerAdapter
import com.example.mosis_projekat.databinding.FragmentWorkshopMainBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class WorkshopMainFragment : Fragment() {

    private var _binding: FragmentWorkshopMainBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkshopMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id = arguments?.getString("id")
        if(id!=null && id!=""){
            sharedViewModel.setSelected(id)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewPager = binding.viewPager
        val pagerAdapter = WorkshopPagerAdapter(this)
        viewPager.adapter=pagerAdapter
        viewPager.isUserInputEnabled=false
        (activity as AppCompatActivity).supportActionBar?.title = ""
        sharedViewModel.selectedWorkshop.observe(viewLifecycleOwner){
            (activity as AppCompatActivity).supportActionBar?.title = it.workshop.name +" | "+it.workshop.type
        }
        if(sharedViewModel.selectedWorkshop.value!=null){
            (activity as AppCompatActivity).supportActionBar?.title = sharedViewModel.selectedWorkshop.value!!.workshop.name +" | "+sharedViewModel.selectedWorkshop.value!!.workshop.type
        }


        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout,viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = pagerAdapter.fragmentNames[i]
        }.attach()
        setHasOptionsMenu(false)

    }

    override fun onResume() {
        super.onResume()
        if(sharedViewModel.selectedWorkshop.value!=null){
            (activity as AppCompatActivity).supportActionBar?.title = sharedViewModel.selectedWorkshop.value!!.workshop.name +" | "+sharedViewModel.selectedWorkshop.value!!.workshop.type
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MAINFRAGMENT","ONDESTROY")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}