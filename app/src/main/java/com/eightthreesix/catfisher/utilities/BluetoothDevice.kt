package com.eightthreesix.catfisher.utilities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log

@SuppressLint("MissingPermission") // PermissionCheck executed but not recognized by IDE
fun BluetoothDevice.typeToText(context: Context): String {
    if (isPermissionsGranted(context)){
        when (this.type){
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> return "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> return "LE"
            BluetoothDevice.DEVICE_TYPE_DUAL -> return "Dual"
            BluetoothDevice.DEVICE_TYPE_UNKNOWN -> return "Unknown"
        }
    }
    return "!@#$"
}

@SuppressLint("MissingPermission")
fun BluetoothDevice.majorClassToText(context: Context): String {
    if (isPermissionsGranted(context)){
        when (this.bluetoothClass.majorDeviceClass){
            AUDIO_VIDEO -> return "Audio/Video"
            COMPUTER -> return "Computer"
            HEALTH -> return "Health"
            IMAGING -> return "Imagining"
            MISC -> return "Miscellaneous"
            NETWORKING -> return "Networking"
            PERIPHERAL -> return "Peripheral"
            PHONE -> return "Phone"
            TOY -> return "Toy"
            UNCATEGORIZED -> return "Uncategorized"
            WEARABLE -> return "Wearable"
        }
    } else {
        Log.e(SPECIAL_TEST,"permission not granted")
    }
    return "!@#$"
}