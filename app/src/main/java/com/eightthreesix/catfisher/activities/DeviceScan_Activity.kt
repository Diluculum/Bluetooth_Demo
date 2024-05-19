package com.eightthreesix.catfisher.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.eightthreesix.catfisher.ui.theme.CatFisherTheme
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG
import com.eightthreesix.catfisher.R
import com.eightthreesix.catfisher.datastore.BTdevice_Container
import com.eightthreesix.catfisher.utilities.BluetoothPermissionDialog
import com.eightthreesix.catfisher.utilities.CheckBluetoothPermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

class DeviceScan_Activity : ComponentActivity() {
    val TAG: String = "DeviceScan_Activity"
//    private val sqlDriver: SqlDriver = AndroidSqliteDriver(BluetoothDB.Schema, applicationContext, "btPairedDevices.db")
//    private val queries = BluetoothDB(sqlDriver).btPairedDevicesQueries
    private var permissionRequests = 0
    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter
    private val deviceList_Paired: MutableList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableList()
    private val deviceList_Scanned: MutableList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableList()
    private val btscanreceiver = object : BroadcastReceiver() {
        private var id: Int = 0
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent){
            if (!isPermissionsGranted(context)) return
            val action = intent.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                    if (device == null) {Log.i(BLUETOOTH_TAG,"device is null"); return}
                    val deviceName = device.name
                    var duplicate = false
                    Log.i(BLUETOOTH_TAG, "Found Device: $deviceName")
                    for (item in deviceList_Scanned){//TODO change to compare based on address
                        if (item.device == device){
                            duplicate = true
                            Log.w(BLUETOOTH_TAG,"item is duplicate")
                            break
                        }
                    }
                    if (!duplicate){
                        Log.i(BLUETOOTH_TAG,"adding to list")
                        deviceList_Scanned.add(BTdevice_Container(id = ++id, device = device))
                        Log.d(BLUETOOTH_TAG,"broadcastreceiver id = $id")
                    }
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

    private fun isPermissionsGranted(context: Context): Boolean {
        val btPermissionGranted = (
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        )
        return btPermissionGranted
    }

    @SuppressLint("MissingPermission")// Permission is checked at launch and program fails if != granted
    private fun acquirePairedDevices(){
        val pairedDevices = btAdapter.bondedDevices
        var count = 0
        var duplicate = false
        for (device in pairedDevices!!.iterator()){
            for (btdevice in deviceList_Paired){
                if (btdevice.device == device) {
                    duplicate = true
                    break
                }
            }
            if(!duplicate)
                deviceList_Paired.add(BTdevice_Container(id = ++count, device = device))
        }
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

@Composable
fun DeviceSectionHeader(text: String, padding: Int){
    Row (modifier = Modifier.padding(horizontal = 16.dp, vertical = padding.dp)){
        Text(text = text, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun DeviceObject(btDevice: BTdevice_Container){
    var upDirection by remember { mutableStateOf(true) }
    val imageSource = when(upDirection){
        true -> R.drawable.sharp_arrow_up_24
        false -> R.drawable.sharp_arrow_down_24
    }
    Card (modifier = Modifier
        .padding(5.dp)
        .wrapContentHeight()
        .fillMaxWidth()) {
        Column (modifier = Modifier.requiredHeight(40.dp), Arrangement.Center){
            Row (modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(painter = painterResource(id = btDevice.image), contentDescription = null)
                    Spacer(modifier = Modifier.size(10.dp))
                    btDevice.alias?.let { Text(text = it) }
//                    Spacer(modifier = Modifier.size(20.dp))
                }
                Row {
                    Icon(painter = painterResource(id = imageSource), contentDescription = null,
                        modifier = Modifier.clickable(onClick = {upDirection = !upDirection}))
                    Spacer(modifier = Modifier.width(16.dp))
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