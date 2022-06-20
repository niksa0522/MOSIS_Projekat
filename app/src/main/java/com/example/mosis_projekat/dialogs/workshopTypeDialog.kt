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
import androidx.recyclerview.widget.RecyclerView
import com.example.mosis_projekat.adapters.WorkshopTypeAdapter
import com.example.mosis_projekat.databinding.FragmentDialogWorkshopsBinding

class workshopTypeDialog : DialogFragment() {

    private var _binding: FragmentDialogWorkshopsBinding? = null
    private val binding get() = _binding!!

    private var type:String? = null
    private var checkedList:ArrayList<String>? = null
    private lateinit var adapter:WorkshopTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogWorkshopsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        type = arguments?.getString("type","")
        checkedList = arguments?.getStringArrayList("chosen")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textWorkshopType.text = type
        adapter = WorkshopTypeAdapter(requireContext(),checkedList)
        binding.recyclerWorkshopTypes.adapter=adapter
        binding.recyclerWorkshopTypes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerWorkshopTypes.addItemDecoration( DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        ))
        binding.btnConfirm.setOnClickListener {
            val result = adapter.returnChecked()
            setFragmentResult(type!!, bundleOf("list" to result))
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
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