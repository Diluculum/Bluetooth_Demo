package com.eightthreesix.catfisher.datastore

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.eightthreesix.catfisher.R
import com.eightthreesix.catfisher.utilities.AUDIO_VIDEO
import com.eightthreesix.catfisher.utilities.COMPUTER
import com.eightthreesix.catfisher.utilities.HEALTH
import com.eightthreesix.catfisher.utilities.NETWORKING
import com.eightthreesix.catfisher.utilities.PHONE
import com.eightthreesix.catfisher.utilities.UNCATEGORIZED
import com.eightthreesix.catfisher.utilities.WEARABLE

@SuppressLint("MissingPermission")
data class BTdevice_Container(val id: Int, val device: BluetoothDevice){
    var image: Int = UNCATEGORIZED
        get() {
            when (device.bluetoothClass.majorDeviceClass) {
                AUDIO_VIDEO -> return R.drawable.sharp_audio_video_24
                COMPUTER -> return R.drawable.sharp_computer_24
                HEALTH -> return R.drawable.sharp_medical_services_24
                NETWORKING -> return R.drawable.sharp_networking_24
                PHONE -> return R.drawable.baseline_phone_24
                WEARABLE -> return R.drawable.sharp_wearable_24
                else -> return R.drawable.sharp_bluetooth_24
            }
        }
        /*TODO pick image based on device.bluetoothClass.deviceClass COMPARE TO BluetoothClass.Device.Major list*/
    val alias = device.alias
    val name = device.name
    val bondState = device.bondState
}
