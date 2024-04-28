package peers.ui.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp


//@Composable
//fun PeersListPreview() {
//    val devices = listOf(
//        NearByDevice(name = "Md Abdul", isConnected = true, deviceAddress = "1234"),
//        NearByDevice(name = "Mr Bean", isConnected = false, deviceAddress = "1234"),
//        NearByDevice(name = "Galaxy Tab", isConnected = false, deviceAddress = "1234"),
//        NearByDevice(name = "Samsung A5", isConnected = false, deviceAddress = "1234"),
//    )
//    NearByDevices(
//        devices = devices,
//        onConnectionRequest = {},
//        onConversionScreenRequest = {},
//        onDisconnectRequest = {}
//    )
//
//}
//


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
                EachDevice(
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
                device = device,
                onClose = onDetailsDismiss
            )
        }
    }


}

