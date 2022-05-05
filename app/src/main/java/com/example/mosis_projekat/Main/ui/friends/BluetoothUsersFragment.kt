package com.example.mosis_projekat.Main.ui.friends

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.mosis_projekat.R

/**
 * A fragment representing a list of Items.
 */
class BluetoothUsersFragment : Fragment() {

    private var columnCount = 1
    private var bluetoothDevices = mutableListOf<BluetoothDevice>()
    private lateinit var adapter: MyBTUserRecyclerViewAdapter
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bluetooth_users_list, container, false)

        //bluetooth manager i adapter
        val bluetoothManager: BluetoothManager? =
            ContextCompat.getSystemService(requireContext(), BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.getAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(),"Ovo nije trebalo da se desi",Toast.LENGTH_SHORT).show()
        }
        val pairedDevices = bluetoothAdapter.bondedDevices
        if(pairedDevices != null) {
            for (p in pairedDevices)
                bluetoothDevices.add(p)
        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
            }
            adapter = MyBTUserRecyclerViewAdapter(bluetoothDevices, bluetoothAdapter,findNavController())
            view.adapter = adapter
        }


        bluetoothAdapter!!.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity?.registerReceiver(receiver,filter)

        return view
    }

    private val receiver = object : BroadcastReceiver(){
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    bluetoothDevices.add(device)
                    adapter.notifyItemInserted(bluetoothDevices.size-1)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("MissingPermission")
    override fun onDestroyView() {
        activity?.unregisterReceiver(receiver)
        bluetoothAdapter.cancelDiscovery()
        super.onDestroyView()
    }
}