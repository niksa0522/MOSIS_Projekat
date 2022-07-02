package com.example.mosis_projekat.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.ActivityMainBinding
import com.example.mosis_projekat.helpers.PermissionHelper
import com.example.mosis_projekat.helpers.ServiceHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        if(PermissionHelper.isLocationPermissionGranted(this)) {
            startService()
            init()
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }
    private fun init(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)




        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_maps,R.id.nav_friends, R.id.nav_search,R.id.savedFragment,R.id.rankingsMainFragment,R.id.settingsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val tvName: TextView = binding.navView.getHeaderView(0).findViewById(R.id.tVUserName)
        val profilePic: ImageView = binding.navView.getHeaderView(0).findViewById(R.id.profilePic)
        val tvLogout: TextView = binding.tVlogout

        tvName.setText(auth.currentUser!!.displayName)
        Glide.with(this).load(auth.currentUser!!.photoUrl).into(profilePic)
        tvLogout.setOnClickListener{Logout()}
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            startService()
            init()
            //trackLocation()
        }
        else{
            Toast.makeText(this,"Servis za lokaciju ne radi, dozvoli permisiju u opcijama",Toast.LENGTH_SHORT).show()
            init()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

    private fun Logout(){

        ServiceHelper.stopService(this)
        auth.signOut()
        val i: Intent = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

    private fun startService(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val service = sharedPreferences.getBoolean("share_location",true)
        if(service){
            ServiceHelper.startService(this)
        }else{
            ServiceHelper.stopService(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser == null){
            val i: Intent = Intent(this, LoginActivity::class.java)
            ServiceHelper.stopService(this)
            auth.signOut()
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
    }
}