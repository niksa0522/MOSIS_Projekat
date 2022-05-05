package com.example.mosis_projekat.Main.ui.friends

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.Main.MainActivity
import com.example.mosis_projekat.R
import com.example.mosis_projekat.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var sharing: Boolean = false
    private lateinit var bluetoothAdapter:BluetoothAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(FriendsViewModel::class.java)

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)
        ActivityCompat.requestPermissions(
            requireActivity(),
                getRequiredPermisions(),
            1
        )

        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.friends_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_friend_server -> AddFriend()
            R.id.action_add_friend_client -> GetFriend()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun AddFriend(){

        if(!CheckBluetooth() || sharing)
            return
        findNavController().navigate(R.id.action_nav_friends_to_bluetoothUsersFragment)

    }
    private fun CheckBluetooth():Boolean {
        val bluetoothManager: BluetoothManager? = getSystemService(requireContext(),BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager!!.getAdapter()
        if (bluetoothAdapter == null) {
            return false
        }
        val permission: String =  getRequiredPermisions()[0]
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                getRequiredPermisions(),
                1
            )
            Toast.makeText(requireContext(),"Dozvoli pristup Bluetooth-u i probaj ponovo",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun GetFriend(){

        if(!CheckBluetooth())
            return
        sharing = true
        val bluetoothManager: BluetoothManager? = getSystemService(requireContext(),BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
        val requestCode = 1;
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivityForResult(discoverableIntent, requestCode)
        val acc:AcceptThread=AcceptThread()
        acc.start()
    }


    fun getRequiredPermisions():Array<String> {
        val targetSDKVersion: Int = requireActivity().applicationInfo.targetSdkVersion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && targetSDKVersion >= Build.VERSION_CODES.S) {
            return arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSDKVersion >= Build.VERSION_CODES.Q) {
            return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            return arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread(){
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("MOSIS Projekat", UUID.fromString("9ee91d5c-3803-43ff-9cec-2ccc1e236613"))
        }

        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("BluetoothTAG", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("BluetoothTAG", "Could not close the connect socket", e)
            }
        }
        fun manageMyConnectedSocket(socket:BluetoothSocket){
            val conn: ConnectionThread = ConnectionThread(socket)
            conn.start()
        }
        private inner class ConnectionThread(mmSocket: BluetoothSocket) : Thread(){
            private val mmInStream: InputStream = mmSocket.inputStream
            private val mmOutStream: OutputStream = mmSocket.outputStream
            private val mmBuffer: ByteArray = ByteArray(1024)
            private val mmSocket = mmSocket

            override fun run() {
                var numBytes: Int
                while(true){
                    numBytes = try {
                        mmInStream.read(mmBuffer)
                    }catch (e: IOException) {
                        Log.d("BluetoothTag", "Input stream was disconnected", e)
                        break
                    }
                    if(mmBuffer.isNotEmpty())
                        break
                }
                val uid:String = "gotovo"
                val bytearray: ByteArray =  uid.toByteArray()

                try{
                    mmOutStream.write(bytearray)
                }catch (e: IOException) {
                    Log.d("BluetoothTag", "Input stream was disconnected", e)
                }
                val myuid = FirebaseAuth.getInstance().currentUser!!.uid
                val frienduid = String(mmBuffer)
                val database = Firebase.database("https://mosis-projekat-8393f-default-rtdb.europe-west1.firebasedatabase.app/")
                val ref = database.reference.child("friends")
                ref.child(myuid).child(frienduid).setValue(frienduid)
                ref.child(frienduid).child(myuid).setValue(myuid)
                mmSocket.close()
                sharing=false
            }
            fun cancel(){
                try{
                    mmSocket?.close()
                }catch (e: IOException) {
                    Log.d("BluetoothTag", "Socket could not be closed", e)
                }
            }
        }
    }

}