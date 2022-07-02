package com.example.mosis_projekat.screens.registration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.shared_view_models.LoginRegistrationViewModel
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentRegisterBinding
import com.example.mosis_projekat.helpers.PermissionHelper


class RegisterFragment : Fragment() {

    private val ViewModel: LoginRegistrationViewModel by activityViewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register,container,false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = ViewModel

        val usernameET = binding.etUsername
        val passwordET = binding.etPassword
        val fName = binding.etFName
        val lName = binding.etLName
        val phoneNum = binding.etphoneNum

        InitData(usernameET,passwordET,fName,lName,phoneNum)

        binding.btnReg.setOnClickListener{ViewModel.createAccount(requireActivity())}
        binding.tvLogin.setOnClickListener{findNavController().navigate(R.id.action_registerFragment_to_loginFragment)}
        binding.btnAddPicture.setOnClickListener{takePicture()}
        ViewModel.picture.observe(viewLifecycleOwner) { newPicture ->
            binding.profilePic.setImageBitmap(newPicture)
        }


    }
    //TODO dodaj permision pitanje

    fun InitData(usernameET:EditText,passwordET:EditText,FName:EditText,LName:EditText, phoneNum:EditText){
        usernameET.setText(ViewModel.username.value ?: "")
        passwordET.setText(ViewModel.password.value ?: "")
        FName.setText(ViewModel.fName.value ?: "")
        LName.setText(ViewModel.lName.value ?: "")
        phoneNum.setText(ViewModel.phoneNum.value ?: "")
        if(ViewModel.picture.value!=null)
            binding.profilePic.setImageBitmap(ViewModel.picture.value)
    }
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result-> if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            val picture: Bitmap = data?.extras?.get("data") as Bitmap
            ViewModel.setPicture(picture)
        }
    }

    fun takePicture(){
        if(PermissionHelper.isCameraPermissionGranted(requireContext())) {
            val cameraIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            takePicture()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}