package com.eightthreesix.catfisher.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG

class BluetoothScanReceiver: BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent){
        val action = intent.action
        when(action){
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                val deviceName = device!!.name
                Log.i(BLUETOOTH_TAG, "found Device: $deviceName")
            }
        }
    }
}