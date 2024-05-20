package com.eightthreesix.catfisher.activities

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eightthreesix.catfisher.ui.theme.CatFisherTheme
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG
import com.eightthreesix.catfisher.datastore.BTdevice_Container
import com.eightthreesix.catfisher.utilities.BluetoothPermissionDialog
import com.eightthreesix.catfisher.utilities.CheckBluetoothPermissions
import com.eightthreesix.catfisher.utilities.isPermissionsGranted
import com.eightthreesix.catfisher.compose.structures.DeviceObject
import com.eightthreesix.catfisher.compose.structures.DeviceSectionHeader
import com.eightthreesix.catfisher.utilities.SPECIAL_TEST
import com.eightthreesix.catfisher.utilities.majorClassToText
import com.eightthreesix.catfisher.utilities.typeToText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class DeviceScan_Activity : ComponentActivity() {
    val TAG: String = "DeviceScan_Activity"
//    private val sqlDriver: SqlDriver = AndroidSqliteDriver(BluetoothDB.Schema, applicationContext, "btPairedDevices.db")
//    private val queries = BluetoothDB(sqlDriver).btPairedDevicesQueries
    private var itemID: Int = 0
    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter
    private val deviceList_Paired: SnapshotStateList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableStateList()
    private val deviceList_Scanned: SnapshotStateList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableStateList()
    private val btscanreceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent){
            if (!isPermissionsGranted(context)) return
            val action = intent.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                    if (device == null) {Log.i(BLUETOOTH_TAG,"device is null"); return}
                    var duplicate = false
                    Log.i(SPECIAL_TEST, "Found Device: [${device}] ${device.name} \n${device.majorClassToText(context)},\n${device.typeToText(context)}(${device.type}), \nadr: ${device.address}  ScannedDeviceList: ${deviceList_Scanned.size}")
                    var debugInfo = "Scanned Devices:\n"
                    for (item in deviceList_Scanned){
                        debugInfo += "${device.name}--${device.type}  <-->  ${item.name}-${item.device.type}\n"
                        if (item.device.address == device.address){
                            duplicate = true
                            break
                        }
                    }
                    Log.w(BLUETOOTH_TAG,debugInfo)
//                    debugInfo = "Paired Devices:\n"
                    if (!duplicate){
                        for (item in deviceList_Paired){
//                            debugInfo += "${device.name}--${device.type}  <-->  ${item.name}-${item.device.type}\n"
                            if (item.device.address == device.address){
                                duplicate = true
                                break
                            }
                        }
                        if(!duplicate){
                            Log.w(BLUETOOTH_TAG,"device NOT duplicate: ${device.name}")
                            deviceList_Scanned.add(BTdevice_Container(id = ++itemID, device = device))
                        }
                    }else{
                        Log.w(BLUETOOTH_TAG,"device IS duplicate")
                    }
                    Log.w(BLUETOOTH_TAG,"ScannedDeviceList: ${deviceList_Scanned.size}")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i(BLUETOOTH_TAG,"Discovery Started")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i(BLUETOOTH_TAG,"Discovery Finished")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")// Permission is checked at launch and program fails if != granted
    private fun acquirePairedDevices(){
        val pairedDevices = btAdapter.bondedDevices
        var duplicate = false
        for (device in pairedDevices!!.iterator()){
            for (btdevice in deviceList_Paired){
                if (btdevice.device == device) {
                    duplicate = true
                    break
                }
            }
            if(!duplicate)
                deviceList_Paired.add(BTdevice_Container(id = ++itemID, device = device))
        }
    }

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatFisherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showBtPermissionDialog by rememberSaveable { mutableStateOf(false) }
                    var rationaleTextState by rememberSaveable { mutableStateOf("") }
                    var permissionState = rememberMultiplePermissionsState(
                        listOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                        )
                    )
                    CheckBluetoothPermissions(
                        multiplePermissionsState = permissionState,
                        onPermissionGranted = {
                            showBtPermissionDialog = false
                            btManager = getSystemService(BluetoothManager::class.java)
                            btAdapter = btManager.adapter

                            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                            registerReceiver(btscanreceiver, filter)
                            btAdapter.startDiscovery()
                            DeviceScan_Layout()
                        },
                        onPermissionDenied = {
                            rationaleTextState = it
                            showBtPermissionDialog = true
                        })
                    if(showBtPermissionDialog)
                        BluetoothPermissionDialog(permissionState, permissionText = rationaleTextState)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if (isPermissionsGranted(this))
            btAdapter.cancelDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(btscanreceiver)
        revokeSelfPermissionsOnKill(mutableListOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT))
    }

    @Composable
    fun DeviceScan_Layout(){
        acquirePairedDevices()
        LazyColumn {
            item {
                DeviceSectionHeader(text = "Paired Devices", padding = 3)
            }
            items(deviceList_Paired, key = {it.id}) { device ->
                device.alias?.let {
                    Log.d(TAG,"(${deviceList_Paired.size}) deviceList_Paired key: ${device.id}  name: ${device.name}")
                    Spacer(modifier = Modifier.height(2.dp))
                    DeviceObject(device)
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
            item {
                DeviceSectionHeader(text = "Scanned Devices", padding = 3)
            }
            items(deviceList_Scanned, key = {it.id}) { device ->
                device.alias?.let {
                    Log.d(TAG,"deviceList_Scanned key: ${device.id}")
                    Spacer(modifier = Modifier.height(2.dp))
                    DeviceObject(device)
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CatFisherTheme {
//        DeviceObject(image = R.drawable.sharp_bluetooth_24, deviceName = "rvrsBiasPC")
    }
}