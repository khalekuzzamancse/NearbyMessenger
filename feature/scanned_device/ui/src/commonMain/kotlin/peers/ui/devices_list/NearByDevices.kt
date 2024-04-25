package peers.ui.devices_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun PeersListPreview() {
    val devices = listOf(
        NearByDevice(name = "Md Abdul", isConnected = true, ip = "1234"),
        NearByDevice(name = "Mr Bean", isConnected = false, ip = "1234"),
        NearByDevice(name = "Galaxy Tab", isConnected = false, ip = "1234"),
        NearByDevice(name = "Samsung A5", isConnected = false, ip = "1234"),
    )
    NearByDevices(
        devices = devices,
        onConnectionRequest = {},
        onConversionScreenRequest = {},
        onDisconnectRequest = {}
    )

}



@Composable
fun NearByDevices(
    modifier: Modifier=Modifier,
    devices: List<NearByDevice>,
    onConnectionRequest: (NearByDevice) -> Unit,
    onDisconnectRequest: (NearByDevice) -> Unit,
    onConversionScreenRequest: (NearByDevice) -> Unit,
) {
    var clickedDevice by remember { mutableStateOf<NearByDevice?>(null) }
    val onDetailsDismiss: () -> Unit = {
        clickedDevice = null
    }
    var showDisconnectConfirmDialog by remember { mutableStateOf(false) }
    var disconnectedDevice by remember { mutableStateOf<NearByDevice?>(null) }
    if (showDisconnectConfirmDialog){
        DisconnectDialog(
            onConfirm = {
                disconnectedDevice?.let {
                    onDisconnectRequest(it)
                }
                showDisconnectConfirmDialog=false
            },
            onCancel = {
                showDisconnectConfirmDialog=false
            }
        )
    }
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shadowElevation = 1.dp

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            devices.forEach {
                _EachDevice(
                    device = it,
                    onConnectClick = {
                        onConnectionRequest(it)
                    },
                    onDeviceInfoClick = {
                        clickedDevice = it
                    },
                    onDisconnectRequest = {
                        disconnectedDevice=it
                        showDisconnectConfirmDialog=true
                    },
                    onClick = {
                        onConversionScreenRequest(it)
                    }
                )


            }

        }
        clickedDevice?.let { device ->
            DeviceDetails(
                modifier=Modifier.testTag("DeviceDetailsDialogue"),
                onClose = onDetailsDismiss,
                onDisconnectRequest = onDetailsDismiss,
                device = device
            )
        }
    }


}

@Composable
private fun _EachDevice(
    device: NearByDevice,
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
        if (device.isConnected) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        } else {
            Icon(imageVector = Icons.Filled.Wifi, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                _ConnectionStatus(
                    isConnected = device.isConnected,
                    onDisconnectRequest = onDisconnectRequest,
                    onConnectClick = onConnectClick
                )

            }
        }

        IconButton(
            onClick = onDeviceInfoClick,
            modifier = Modifier
                .semantics { contentDescription="Device Info" }
                .testTag("DeviceInfoButton")

        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
        }

    }
}
//Making internal so that can access from test module
@Composable
private fun _ConnectionStatus(
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
                if (isConnected) Color.Blue else Color.Red
            ),
            modifier = Modifier.clickable {
                if (isConnected) onDisconnectRequest()
                else onConnectClick()
            }
        )

    }

}
