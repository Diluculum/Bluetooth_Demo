package com.eightthreesix.catfisher.datastore

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.eightthreesix.catfisher.R

@SuppressLint("MissingPermission")
data class BTdevice_Container(val id: Int, val device: BluetoothDevice){
    var image: Int = R.drawable.sharp_bluetooth_24 /*TODO pick image based on device.bluetoothClass.deviceClass COMPARE TO BluetoothClass.Device.Major list*/
    val alias = device.alias
    val name = device.name
    val bondState = device.bondState
}
