package com.example.mosis_projekat.screens.workshopInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentWorkshopInfoBinding
import com.example.mosis_projekat.databinding.FragmentWorkshopMainBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel


class WorkshopInfoFragment : Fragment() {

    private var _binding: FragmentWorkshopInfoBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkshopInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentWorkshop=sharedViewModel.getSelected()!!
        binding.textPhone.text = resources.getString(R.string.phoneNumberInfo,currentWorkshop.workshop.phone!!)
        binding.ratingMoney.rating = currentWorkshop.workshop.price
        binding.ratingBar.rating = currentWorkshop.workshop.avgRating
        binding.constraintFavorite.setOnClickListener {
            //TODO napravi da favourite radi
        }
        binding.constraintNavigate.setOnClickListener {
            //TODO napravi da navigate otvara mapu i ide do lokacije
            //TODO dodaj info negde
        }

        Glide.with(this).load(currentWorkshop.workshop.imageUrl).into(binding.profilePic)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}