package com.khalekuzzanman.cse.just.peertopeer.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun DeviceListPreview() {
    Column {
        NearByDevices(
            devices = listOf("Md Abdul Kala", "Mr Bean", "Galaxy Tab", "Sumsung A5"),
            connectedDeviceIndex = 0
        )
    }


}

@Preview
@Composable
fun DeviceListPreview2() {
    Column {
        NearByDevices(
            devices = listOf("Md Abul Kalam", "Mr Bean", "Galaxy Tab", "Sumsung A5"),
            connectedDeviceIndex = 0
        )
        ConnectedDeviceInfo(
            onClose = { },
            onDisconnectRequest = { },
            isConnected = true
        )


    }


}

@Preview
@Composable
fun DeviceListPreview3() {
    Column {
        NearByDevices(
            devices = listOf("Md Abul Kalam", "Mr Bean", "Galaxy Tab", "Sumsung A5"),
            connectedDeviceIndex = 0
        )
        ConnectedDeviceInfo(
            onClose = { },
            onDisconnectRequest = { },
            isConnected = false
        )

    }


}


@Composable
fun NearByDevices(
    devices: List<String>,
    connectedDeviceIndex: Int = -1,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    var connected by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 1.dp

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            devices.forEachIndexed { index, name ->
                val isConnected = connectedDeviceIndex == index
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isConnected) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.Filled.Wifi, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = name, style = MaterialTheme.typography.titleMedium)
                        if (isConnected) {
                            Text(text = "Connected", style = MaterialTheme.typography.labelSmall)
                        }

                    }
                    IconButton(onClick = {
                        showDialog = true
                        connected = isConnected
                    }) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                    }
                }

            }

        }
        if (showDialog) {
            ConnectedDeviceInfo(
                onClose = { showDialog = false },
                onDisconnectRequest = { showDialog = false },
                isConnected = connected
            )
        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedDeviceInfo(
    isConnected: Boolean,
    onClose: () -> Unit,
    onDisconnectRequest: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onClose,
        modifier = Modifier
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Device Name: ")
                Text(text = "IP Address: ")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    if (isConnected) {
                        TextButton(onClick = onDisconnectRequest) {
                            Text(text = "Disconnect")
                        }
                    } else {
                        TextButton(onClick = onClose) {
                            Text(text = "Cancel")
                        }
                    }

                }
            }

        }


    }


}
