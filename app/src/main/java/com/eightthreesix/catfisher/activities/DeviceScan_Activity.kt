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
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.eightthreesix.catfisher.ui.theme.CatFisherTheme
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG
import com.eightthreesix.catfisher.R
import com.eightthreesix.catfisher.datastore.BTdevice_Container
import com.eightthreesix.catfisher.db.BluetoothDB
import com.eightthreesix.catfisher.receivers.BluetoothScanReceiver

class DeviceScan_Activity : ComponentActivity() {
//    private val sqlDriver: SqlDriver = AndroidSqliteDriver(BluetoothDB.Schema, applicationContext, "btPairedDevices.db")
//    private val queries = BluetoothDB(sqlDriver).btPairedDevicesQueries
    private var btPermissionGranted = false
    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter
    private val deviceList_Paired: MutableList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableList()
    private val deviceList_Scanned: MutableList<BTdevice_Container> = emptyList<BTdevice_Container>().toMutableList()
    private val btscanreceiver = object : BroadcastReceiver() {
        private var id: Int = 0
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent){
            if (!btPermissionGranted) return
            val action = intent.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                    if (device == null) {Log.i(BLUETOOTH_TAG,"device is null"); return}
                    val deviceName = device!!.name
                    Log.i(BLUETOOTH_TAG, "Found Device: $deviceName")
                    deviceList_Scanned.add(BTdevice_Container(id = ++id, device = device))
                }
            }
        }
    }
    private val permissionLauncher = registerForActivityResult(RequestPermission()){ isGranted  ->
        if (isGranted){
            btPermissionGranted = isGranted
            onPermissionGranted()
            Log.i(BLUETOOTH_TAG,"BT Permission Allowed")
        }else{
            // throw error message
            Log.i(BLUETOOTH_TAG,"BT Permission Denied")
        }
    }
    @SuppressLint("MissingPermission")
    private val startForResult = registerForActivityResult(StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            //Permission granted -> Bluetooth ON
            Log.i(BLUETOOTH_TAG,"BT Enabled")
            var count = 0
            if(btPermissionGranted){
                while(!btAdapter.startDiscovery()){
                    if (count++ > 3) break
                }
            }
        }
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.i(BLUETOOTH_TAG,"BT Enable Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        if (btAdapter == null) { /*kick out error and disable functions*/
            Log.i(BLUETOOTH_TAG,"bt adapter null")}

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(btscanreceiver, filter)

        setContent {
            CatFisherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeviceScan_Layout(context = this)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(btscanreceiver)
    }

    private fun onPermissionGranted() {
        if(btEnabled()){
            startForResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }else{
            Log.i(BLUETOOTH_TAG,"BT is Active")
        }
    }

    private fun btEnabled(): Boolean {
        return true.equals(btAdapter.isEnabled)
    }

    @SuppressLint("MissingPermission")// Permission is checked at launch and program fails if != granted
    private fun aquirePairedDevices(){
        val pairedDevices = btAdapter.bondedDevices
        var count = 0
        for (device in pairedDevices!!.iterator()){
            deviceList_Paired.add(BTdevice_Container(id = ++count, device = device))
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanForDevices() {
        if (btPermissionGranted){
            btAdapter.startDiscovery()
        }
    }

    @Composable
    fun ScannedDevices_Layout(){
        LazyColumn {
            items(deviceList_Scanned, key = {it.id}) { device ->
                device.alias?.let { DeviceObject(device) }
            }
        }
    }

    @Composable
    fun DeviceScan_Layout(context: Context){
        aquirePairedDevices()
        Text(text = "Paired Devices", modifier = Modifier.size(13.dp))
        LazyColumn {//TODO arrangeable list of paired devices utilizing btPairedDevices.sq
            items(deviceList_Paired, key = { it.id }){ device ->
                device.alias?.let { DeviceObject(device) }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Scanned Devices", modifier = Modifier.size(13.dp))
        ScannedDevices_Layout()
    }
}


@Composable
fun DeviceObject(btDevice: BTdevice_Container){
    var upDirection by remember { mutableStateOf(true) }
    val imageSource = when(upDirection){
        true -> R.drawable.sharp_arrow_up_24
        false -> R.drawable.sharp_arrow_down_24
    }
    Card {
        Row {
            Icon(painter = painterResource(id = btDevice.image), contentDescription = null)
            Spacer(modifier = Modifier.size(10.dp))
            btDevice.alias?.let { Text(text = it) }
            Spacer(modifier = Modifier.size(20.dp))
            Icon(painter = painterResource(id = imageSource), contentDescription = null,
                modifier = Modifier.clickable(onClick = {upDirection = !upDirection}))
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