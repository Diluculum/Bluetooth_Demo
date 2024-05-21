package com.eightthreesix.catfisher.compose.structures

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eightthreesix.catfisher.R
import com.eightthreesix.catfisher.datastore.BTdevice_Container

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
        .padding(1.dp)
        .wrapContentHeight()
        .fillMaxWidth()) {
        val objectHeight = 60.dp
        Column (modifier = Modifier.requiredHeight(objectHeight), Arrangement.Center){
            Row (modifier = Modifier
                .height(objectHeight / 2)
                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(painter = painterResource(id = btDevice.image), contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    btDevice.alias?.let { Text(text = it, fontSize = 17.sp) }
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

@Composable
fun DeviceOptions(){
    val objectHeight = 60.dp
    Card(modifier = Modifier
        .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.requiredHeight(objectHeight), Arrangement.Center
        ) {
            Row {
                functionIcon(name = "Unpair", image = R.drawable.sharp_unpair_24) {

                }
                functionIcon(name = "Disconnect", image = R.drawable.bluetooth_disabled_24) {

                }
            }
        }
    }
}

@Composable
fun functionIcon(name: String, image: Int, onClick: () -> Unit){
    Column {
        Icon(painter = painterResource(id = image), contentDescription = null, modifier = Modifier.clickable {
            onClick
        })
        Text(text = name, modifier = Modifier.size(11.dp))
    }
}
