package com.example.mosis_projekat.loginRegistration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.loginRegistration.models.LoginRegistrationViewModel
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentLoginBinding
import com.example.mosis_projekat.databinding.FragmentRegisterBinding

class LoginFragment : Fragment() {


    private val ViewModel: LoginRegistrationViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container,false)
        val root = binding?.root
       return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = ViewModel
        val usernameET = binding.etUsername
        val passwordET = binding.etPassword

        InitData(usernameET,passwordET)

        binding.btnLogin.setOnClickListener{ViewModel.login(requireActivity())}
        binding.tvReg.setOnClickListener{findNavController().navigate(R.id.action_loginFragment_to_registerFragment)}


    }

    fun InitData(usernameET: EditText, passwordET: EditText){
        usernameET.setText(ViewModel.username.value ?: "")
        passwordET.setText(ViewModel.password.value ?: "")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}