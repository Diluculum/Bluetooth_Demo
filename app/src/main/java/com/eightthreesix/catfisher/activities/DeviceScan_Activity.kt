package com.eightthreesix.catfisher.activities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
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
import com.eightthreesix.catfisher.ui.theme.CatFisherTheme
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG
import com.eightthreesix.catfisher.R
import com.eightthreesix.catfisher.db.BluetoothDB

class DeviceScan_Activity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(RequestPermission()){ isGranted  ->
        if (isGranted){
            onPermissionGranted()
        }else{
            // throw error message
            Log.i(BLUETOOTH_TAG,"BT Permission Denied")
        }
    }
    private val startForResult = registerForActivityResult(StartActivityForResult()){ result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            //Permission granted -> Bluetooth ON
            Log.i(BLUETOOTH_TAG,"BT Enabled")
        }
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.i(BLUETOOTH_TAG,"BT Enable Cancelled")
        }
    }
    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = getSystemService(BluetoothManager::class.java)
        btAdapter = btManager.adapter

        if (btAdapter == null) { /*kick out error and disable functions*/ Log.i(BLUETOOTH_TAG,"bt adapter null")}

        setContent {
            CatFisherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // main compose here
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

    data class BtEntry(
        val name: String = "",
        val type: Int = -1
    )


}

@Composable
fun DeviceScan_Layout(context: Context){
    LazyColumn {
//        items/*(TODO dataList of available devices)*/{device ->
//            DeviceObject(image = device.image, deviceName = device.name)
//        }
    }
}

@Composable
fun DeviceObject(image: Int, deviceName: String){
    var upDirection by remember { mutableStateOf(true) }
    val imageSource = when(upDirection){
        true -> R.drawable.sharp_arrow_up_24
        false -> R.drawable.sharp_arrow_down_24
    }
    Card {
        Row {
            Icon(painter = painterResource(id = image), contentDescription = null)
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = deviceName)
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
        DeviceObject(image = R.drawable.sharp_bluetooth_24, deviceName = "rvrsBiasPC")
    }
}