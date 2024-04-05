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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.eightthreesix.catfisher.ui.theme.CatFisherTheme
import com.eightthreesix.catfisher.utilities.BLUETOOTH_TAG
import com.eightthreesix.catfisher.R

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
                    Greeting("Android")
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
}

@Composable
fun DeviceScan_Layout(context: Context){

}

@Composable
fun DeviceObject(image: Int, deviceName: String){
    Card {
        Row {
            Icon(painter = painterResource(id = image), contentDescription = null)
            Text(text = deviceName)
            Icon(painter = painterResource(id = R.drawable.sharp_arrow_up_24), contentDescription = null)
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CatFisherTheme {
        DeviceObject(image = R.drawable.sharp_bluetooth_24, deviceName = "rvrsBiasPC")
    }
}