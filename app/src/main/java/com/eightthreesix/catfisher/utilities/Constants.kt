package com.eightthreesix.catfisher.utilities

import android.bluetooth.BluetoothClass.Device.Major
import android.bluetooth.BluetoothClass.Device

//Bluetooth Settings
const val SCAN_PERIOD = 10000

const val REQUEST_ENABLE_BT = 5546

// TAGS
const val BLUETOOTH_TAG = "BluetoothFunction"
const val SPECIAL_TEST = "Specialized_Test"

//BluetoothClass.Device.Major list
const val AUDIO_VIDEO = Major.AUDIO_VIDEO
const val COMPUTER = Major.COMPUTER
const val HEALTH = Major.HEALTH
const val IMAGING = Major.IMAGING
const val MISC = Major.MISC
const val NETWORKING = Major.NETWORKING
const val PERIPHERAL = Major.PERIPHERAL
const val PHONE = Major.PHONE
const val TOY = Major.TOY
const val UNCATEGORIZED = Major.UNCATEGORIZED
const val WEARABLE = Major.WEARABLE

//BluetoothClass.Device TODO build list ONLY for special images. All else with have catchall
const val CAMCORDER = Device.AUDIO_VIDEO_CAMCORDER
const val CAR_AUDIO = Device.AUDIO_VIDEO_CAR_AUDIO
const val HANDSFREE = Device.AUDIO_VIDEO_HANDSFREE
const val HEADPHONES = Device.AUDIO_VIDEO_HEADPHONES
const val HIFI_AUDIO = Device.AUDIO_VIDEO_HIFI_AUDIO
const val LOUDSPKR = Device.AUDIO_VIDEO_LOUDSPEAKER
const val MICROPHONE = Device.AUDIO_VIDEO_MICROPHONE
const val PORTABLE_AUDIO = Device.AUDIO_VIDEO_PORTABLE_AUDIO
