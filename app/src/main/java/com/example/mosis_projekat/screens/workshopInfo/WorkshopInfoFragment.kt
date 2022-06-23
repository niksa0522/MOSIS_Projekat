package com.example.mosis_projekat.screens.workshopInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentWorkshopInfoBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel


class WorkshopInfoFragment : Fragment() {

    private var _binding: FragmentWorkshopInfoBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()
    private val viewModel: WorkshopInfoViewModel by viewModels()

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
        sharedViewModel.selectedWorkshop.observe(viewLifecycleOwner){
            binding.textPhone.text = resources.getString(R.string.phoneNumberInfo,it.workshop.phone!!)
            binding.ratingMoney.rating = it.workshop.price
            binding.ratingBar.rating = it.workshop.avgRating
            Glide.with(this).load(it.workshop.imageUrl).into(binding.profilePic)
            viewModel.checkIfSaved(it.id)
        }
        sharedViewModel.currentUserReview.observe(viewLifecycleOwner){
            if(it!=null){
                binding.textYourOpinion.text = it.comment
                binding.yourRatingBar.rating = it.rating
                binding.ratingBar.onRatingBarChangeListener=null
                binding.btnMore.visibility=View.VISIBLE
                binding.ratingBar.setIsIndicator(true)
            }else{
                binding.textYourOpinion.text = resources.getText(R.string.share_your_opinion)
                binding.yourRatingBar.rating = 0f
                setUpRatingChanged()
                binding.btnMore.visibility=View.INVISIBLE
                binding.ratingBar.setIsIndicator(false)
            }
        }
        viewModel.isSaved.observe(viewLifecycleOwner){
            if(it){
                binding.imgSaved.setImageResource(R.drawable.ic_baseline_bookmark_24)
            }else{
                binding.imgSaved.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
            }
        }
        binding.constraintFavorite.setOnClickListener {
            if(sharedViewModel.selectedWorkshop.value!=null) {
                viewModel.clickOnSaved(sharedViewModel.selectedWorkshop.value!!.id)
            }
        }
        binding.constraintNavigate.setOnClickListener {
            val selectedWorkshop = sharedViewModel.selectedWorkshop.value
            if(selectedWorkshop!=null) {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${selectedWorkshop.workshop.location!!.lat},${selectedWorkshop.workshop.location!!.lon}")
                val mapIntent = Intent(Intent.ACTION_VIEW,gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(activity, "Nemate instalirane google mape", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            //TODO dodaj info negde
        }
        binding.btnMore.setOnClickListener {
            val popupMenu = PopupMenu(context, binding.btnMore)
            popupMenu.menuInflater.inflate(R.menu.review_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.modify -> {
                        if(sharedViewModel.currentUserReview.value!=null){
                            val review = sharedViewModel.currentUserReview.value!!
                            val bun = Bundle()
                            bun.putFloat("rating",review.rating)
                            bun.putString("comment",review.comment)
                            findNavController().navigate(R.id.action_workshopMainFragment_to_ratingDialog,bun)
                        }
                        true
                    }
                    R.id.delete -> {
                        if(sharedViewModel.currentUserReview.value!=null){
                            sharedViewModel.deleteReview(sharedViewModel.currentUserReview.value!!.rating)
                        }
                        true
                    }
                    else -> true
                }
            }
            popupMenu.show()
        }





    }

    private fun setUpRatingChanged(){
        binding.yourRatingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            val bun = Bundle()
            bun.putFloat("rating",fl)
            bun.putBoolean("isNew",true)
            findNavController().navigate(R.id.action_workshopMainFragment_to_ratingDialog,bun)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}