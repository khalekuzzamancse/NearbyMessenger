package kzcse.wifidirect.ui.ui.devices_list

import android.net.wifi.p2p.WifiP2pDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun DeviceListPreview() {
    Column {
        NearByDevices(
            devices = listOf("Md Abdul Kala", "Mr Bean", "Galaxy Tab", "Samsung A5"),
            connectedDeviceIndex = 0
        )
    }


}

@Preview
@Composable
fun DeviceListPreview2() {
    Column {
        NearByDevices(
            devices = listOf("Md Abdul Kala", "Mr Bean", "Galaxy Tab", "Samsung A5"),
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
            devices = listOf("Md Abdul Kala", "Mr Bean", "Galaxy Tab", "Samsung A5"),
            connectedDeviceIndex = 0
        )
        ConnectedDeviceInfo(
            onClose = { },
            onDisconnectRequest = { },
            isConnected = false
        )

    }


}

data class NearByDevice(
    val name: String,
    val isConnected: Boolean = false,
    val device: WifiP2pDevice
)

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

@Composable
fun NearByDevices(
    devices: List<NearByDevice>,
    onConnectionRequest: (WifiP2pDevice) -> Unit,
    onDisconnectRequest: (WifiP2pDevice) -> Unit,
    onConversionScreenRequest:  (WifiP2pDevice) -> Unit={},
) {
    var showDialog by remember {
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
            devices.forEach {
                EachDevice(
                    it = it,
                    onConnectClick = {
                        onConnectionRequest(it.device)
                    },
                    onDeviceInfoClick = { showDialog = true },
                    onDisconnectRequest = {
                        onDisconnectRequest(it.device)
                    },
                    onClick = {
                        onConversionScreenRequest(it.device)
                    }
                )


            }

        }
        if (showDialog) {
            ConnectedDeviceInfo(
                onClose = { showDialog = false },
                onDisconnectRequest = { showDialog = false },
                isConnected = false
            )
        }


    }


}

@Composable
fun EachDevice(
    it: NearByDevice,
    onConnectClick: () -> Unit = {},
    onDeviceInfoClick: () -> Unit = {},
    onDisconnectRequest: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (it.isConnected) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        } else {
            Icon(imageVector = Icons.Filled.Wifi, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = it.name, style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                ConnectionStatus(
                    isConnected = it.isConnected,
                    onDisconnectRequest =onDisconnectRequest,
                    onConnectClick = onConnectClick
                )

            }
        }

        IconButton(onClick = onDeviceInfoClick) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        }

    }
}

@Composable
fun ConnectionStatus(
    modifier: Modifier = Modifier,
    isConnected: Boolean,
    onDisconnectRequest: () -> Unit = {},
    onConnectClick: () -> Unit = {}
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isConnected) "Connected" else "Not Connected",
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = if (isConnected) "Disconnect?" else "Connect?",
            style = MaterialTheme.typography.labelSmall.copy(
                if(isConnected) Color.Blue else Color.Red
            ),
            modifier = Modifier.clickable {
                if (isConnected) onDisconnectRequest()
                else onConnectClick()
            }
        )

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
