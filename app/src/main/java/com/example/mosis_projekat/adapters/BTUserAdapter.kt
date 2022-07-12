package com.example.mosis_projekat.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mosis_projekat.databinding.FragmentBluetoothUsersBinding
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

interface OnThreadClose{
    fun OnThreadDone(activity: Activity)
}

class BTUserAdapter(
    private val values: List<BluetoothDevice>,
    private val adapter: BluetoothAdapter,
    private val navController: NavController,
    private val activity: Activity
) : RecyclerView.Adapter<BTUserAdapter.ViewHolder>(),OnThreadClose {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentBluetoothUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.name
        holder.contentView.text = item.address
        holder.itemView.setOnClickListener { ConnectToDevice(values.get(position),activity) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentBluetoothUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    private fun ConnectToDevice(device:BluetoothDevice,activity: Activity){
        val conn: ConnectThread =ConnectThread(device,this,activity)
        conn.start()
    }
    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice,private val callback: OnThreadClose,private val activity: Activity): Thread(){
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("9ee91d5c-3803-43ff-9cec-2ccc1e236613"))
        }

        @SuppressLint("MissingPermission")
        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            adapter.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket, callback,activity)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("BluetoothTag", "Could not close the client socket", e)
            }
        }
        fun manageMyConnectedSocket(socket: BluetoothSocket,callback: OnThreadClose,activity: Activity){
            val conn: ConnectionThread = ConnectionThread(socket, callback,activity)
            conn.start()

        }
        private inner class ConnectionThread(mmSocket: BluetoothSocket,private val callback: OnThreadClose,private val activity: Activity) : Thread(){
            private val mmInStream: InputStream = mmSocket.inputStream
            private val mmOutStream: OutputStream = mmSocket.outputStream
            private val mmBuffer: ByteArray = ByteArray(1024)

            override fun run() {
                var numBytes: Int
                val uid:String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val bytearray: ByteArray =  uid.toByteArray()

                try{
                    mmOutStream.write(bytearray)
                }catch (e: IOException) {
                    Log.d("BluetoothTag", "Input stream was disconnected", e)
                }
                while(true){
                    numBytes = try {
                        mmInStream.read(mmBuffer)
                    }catch (e: IOException) {
                        Log.d("BluetoothTag", "Input stream was disconnected", e)
                        break
                    }
                    if(String(mmBuffer) == "gotovo")
                        break
                }
                mmSocket?.close()
                callback.OnThreadDone(activity)
                //navController.popBackStack()
            }
            fun cancel() {
                try{
                    mmSocket?.close()
                }catch (e: IOException) {
                    Log.d("BluetoothTag", "Socket could not be closed", e)
                }
            }
        }
    }

    override fun OnThreadDone(activity: Activity) {
        //navController.popBackStack()
        activity.runOnUiThread {
            Toast.makeText(activity,"Prijatelj dodat, vrati se na prethodni ekran",Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(context,"Prijatelj dodat, vrati se na prethodni ekran",Toast.LENGTH_SHORT).show()
    }

}