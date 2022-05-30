package com.example.mosis_projekat.Main.ui.maps.workshops

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databaseModels.DatabaseLocation
import com.example.mosis_projekat.databinding.FragmentAddWorkshopBinding
import java.io.FileDescriptor
import java.io.IOException


class AddWorkshopFragment : Fragment() {


    private var _binding: FragmentAddWorkshopBinding? = null

    private val AddViewModel: AddWorkshopViewModel by viewModels()

    private var lat:Double? = 0.0;
    private var long:Double? = 0.0;

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AddWorkshopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentAddWorkshopBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lat = arguments?.getDouble("lat")
        long = arguments?.getDouble("long")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val items = listOf("Mehanicar","Limarija","Farba","Vulkanizer","Pranje i Ciscenje")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, items)
        binding.autoCompleteWorkshopType.setAdapter(adapter)

        viewModel.picture.observe(viewLifecycleOwner){
            binding.profilePic.setImageBitmap(it)
        }

        binding.imageAddPicture.setOnClickListener {
            selectPicture()
        }
        binding.imageTakePicture.setOnClickListener {
            takePicture()
        }
        binding.buttonFinish.setOnClickListener {
            addWorkshop()
        }



    }

    private fun addWorkshop(){
        val name = binding.etWorkshopName.text.toString()
        val description = binding.etWorkshopOpis.text.toString()
        val type = binding.autoCompleteWorkshopType.text.toString()
        val phoneNumber = binding.etWorkshopPhoneNum.text.toString()
        val price = binding.ratingBar.rating
        if(name==""){
            Toast.makeText(context,"Unesi Ime Radionice",Toast.LENGTH_SHORT).show()
            return
        }
        if(description==""){
            Toast.makeText(context,"Unesi Opis Radionice",Toast.LENGTH_SHORT).show()
            return
        }
        if(type==""){
            Toast.makeText(context,"Unesi Broj Telefona Radionice",Toast.LENGTH_SHORT).show()
            return
        }
        if(phoneNumber==""){
            Toast.makeText(context,"Unesi Tip Radionice",Toast.LENGTH_SHORT).show()
            return
        }
        if(viewModel.picture.value==null){
            Toast.makeText(context,"Unesi Sliku Radionice",Toast.LENGTH_SHORT).show()
            return
        }
        val location = DatabaseLocation(lat,long)
        viewModel.addWorkshop(name,phoneNumber,type,description,price, location)
    }

//TODO dodaj permision pitanje

    val resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if(result.resultCode == Activity.RESULT_OK){
        val data: Intent? = result.data
        val picture: Bitmap = data?.extras?.get("data") as Bitmap
        viewModel.setPicture(picture)
        }
    }
    val resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if(result.resultCode == Activity.RESULT_OK) {
        val data: Intent? = result.data
        val uri: Uri? = data?.data
        if (uri != null) {
            val picture: Bitmap? = uriToBitmap(uri)
            if(picture!=null) {
                viewModel.setPicture(picture)
            }
        }
    }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun takePicture(){
        val cameraIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCamera.launch(cameraIntent)
    }
    private fun selectPicture(){
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncherGallery.launch(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}