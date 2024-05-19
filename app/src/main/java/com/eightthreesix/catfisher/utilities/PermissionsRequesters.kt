package com.eightthreesix.catfisher.utilities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.eightthreesix.catfisher.R
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPermissionDialog(
    multiplePermissionsState: MultiplePermissionsState,
    permissionText: String
) {
    AlertDialog(
        title = { Text(text = "Bluetooth Permission Request")},
        text = { Text(text = permissionText) },
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }
            ) {
                Text(text = "Request")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckBluetoothPermissions (
    multiplePermissionsState: MultiplePermissionsState,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: (text: String) -> Unit
){
    if (multiplePermissionsState.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        onPermissionDenied(
            getShowGivenPermissionsText(
                multiplePermissionsState.revokedPermissions,
                multiplePermissionsState.shouldShowRationale,
                stringResource(id = R.string.rationale_text)
            )
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getShowGivenPermissionsText(
    permissions: List<PermissionState>,
    showRationale: Boolean,
    rationaleText: String
): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if (showRationale) {
            " IMPORTANT! Please grant all bluetooth permissions for the app to function properly."
        } else {
            " DENIED! The app cannot function."
        }
    )
    textToShow.append(rationaleText)

    return textToShow.toString()
}