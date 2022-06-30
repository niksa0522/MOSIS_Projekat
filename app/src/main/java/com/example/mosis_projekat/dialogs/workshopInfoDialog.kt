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
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mosis_projekat.adapters.WorkshopTypeAdapter
import com.example.mosis_projekat.databinding.FragmentDialogWorkshopInfoBinding
import com.example.mosis_projekat.databinding.FragmentDialogWorkshopsBinding

class workshopInfoDialog : DialogFragment() {

    private var _binding: FragmentDialogWorkshopInfoBinding? = null
    private val binding get() = _binding!!

    private var info:String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogWorkshopInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        info = arguments?.getString("info","")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textWorkshopInfo.text = info
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }

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