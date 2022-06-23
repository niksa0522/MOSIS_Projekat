package com.example.mosis_projekat.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mosis_projekat.adapters.WorkshopTypeAdapter
import com.example.mosis_projekat.databinding.FragmentDialogRatingBinding
import com.example.mosis_projekat.databinding.FragmentDialogWorkshopsBinding
import com.example.mosis_projekat.shared_view_models.WorkshopSearchListViewModel

class RatingDialog: DialogFragment() {

    private var _binding: FragmentDialogRatingBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: WorkshopSearchListViewModel by activityViewModels()

    private var rating:Float = 0f
    private var comment:String = ""
    private var isNew:Boolean=false
    private var oldRating = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogRatingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        comment = arguments?.getString("comment","")!!
        rating = arguments?.getFloat("rating",0f)!!
        isNew = arguments?.getBoolean("isNew",false)!!
        oldRating=rating

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yourRatingBar.rating = rating
        binding.textYourOpinion.setText(comment)
        binding.btnConfirm.setOnClickListener {
            sharedViewModel.setReview(binding.textYourOpinion.text.toString(),binding.yourRatingBar.rating,rating,isNew)
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        //TODO UI je scuffed promeni to

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}