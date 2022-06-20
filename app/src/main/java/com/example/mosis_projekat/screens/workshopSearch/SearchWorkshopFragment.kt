package com.example.mosis_projekat.screens.workshopSearch

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentDialogWorkshopsBinding
import com.example.mosis_projekat.databinding.FragmentSearchWorkshopBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

class SearchWorkshopFragment : Fragment() {

    private var _binding: FragmentSearchWorkshopBinding? = null
    private val binding get() = _binding!!

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchWorkshopBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.workshopTypeInvisible.setOnClickListener {
            setFragmentResultListener("Choose Workshop"){ requestKey, bundle ->
                val result = bundle.getStringArrayList("list")
                if(result!=null){
                    sharedViewModel.workshopTypes=result
                }
                binding.etWorkshopType.setText(result.toString().substring(1,result.toString().length-1))
                Log.d("WORKSHOPLIST",result.toString());
            }
            val bundle=Bundle()
            bundle.putString("type","Choose Workshop")
            bundle.putStringArrayList("chosen",sharedViewModel.workshopTypes)
            findNavController().navigate(R.id.action_searchWorkshopFragment_to_workshopTypeDialog,bundle)
        }
        binding.btnClearPrice.setOnClickListener{
            binding.priceBar.rating=0f
        }
        binding.btnClearRating.setOnClickListener {
            binding.ratingBar.rating=0f
        }
        binding.btnSearch.setOnClickListener {
            val name = binding.etWorkshopName.text.toString()
            val distance = binding.etWorkshopDistance.text.toString().toIntOrNull()
            val price = binding.priceBar.rating
            val rating = binding.ratingBar.rating
            sharedViewModel.setValues(name,distance,price,rating)
            setupLocationTracking()
        }
    }

    private fun setupLocationTracking(){
        if(isPermissionGranted()){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            getLocationAndGoToSearch()
        }else{
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocationAndGoToSearch(){
        fusedLocationClient?.lastLocation?.addOnCompleteListener {
            if(it.result.latitude!=null && it.result.longitude!=null) {
                sharedViewModel.currentLocation=LatLng(it.result.latitude,it.result.longitude)
                sharedViewModel.getWorkshops()
                findNavController().navigate(R.id.action_nav_search_to_workshopListFragment)
            }
        }
    }
    private val requestPermissionLauncherFLC = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            setupLocationTracking()
        }
    }
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}