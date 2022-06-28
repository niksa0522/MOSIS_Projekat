package com.example.mosis_projekat.screens.viewFriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mosis_projekat.databinding.FragmentViewFriendBinding


class ViewFriendFragment : Fragment() {


    private var _binding: FragmentViewFriendBinding? = null

    private val ViewModel: ViewFriendViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentViewFriendBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val uid = arguments?.getString("uid")
        if(uid!=null && uid!="")
            ViewModel.setUID(uid,requireContext())


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewModel.picture.observe(viewLifecycleOwner) { newPicture ->
            binding.profilePic.setImageBitmap(newPicture)
        }
        ViewModel.Name.observe(viewLifecycleOwner){
            binding.name.setText(it)
        }
        ViewModel.phoneNum.observe(viewLifecycleOwner){
            binding.PhoneNum.setText(it)
        }
        ViewModel.rank.observe(viewLifecycleOwner){
            binding.scoreAndRank.text=it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}