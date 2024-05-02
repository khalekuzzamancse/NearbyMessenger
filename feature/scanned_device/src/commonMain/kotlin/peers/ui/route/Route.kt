package peers.ui.route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.ScannnedDevice
import peers.ui.devices.NearByDevices

//@Suppress("Unused")
//@Composable
//fun NearByDevicesRoutePreview() {
//    var wifiEnabled by remember { mutableStateOf(false) }
//    var showProgressBar by remember { mutableStateOf(false) }
//    val scope = rememberCoroutineScope()
//
//    val devices = listOf(
//        NearByDevice(name = "Md Abdul", isConnected = true, deviceAddress = "1234"),
//        NearByDevice(name = "Mr Bean", isConnected = false, deviceAddress = "1234"),
//        NearByDevice(name = "Galaxy Tab", isConnected = false, deviceAddress = "1234"),
//        NearByDevice(name = "Samsung A5", isConnected = false, deviceAddress = "1234"),
//    )
//    NearByDevicesRoute(
//        devices = devices,
//        wifiEnabled = wifiEnabled,
//        showProgressbar = showProgressBar,
//        onDisconnectRequest = {},
//        onConnectionRequest = {},
//        onConversionScreenOpenRequest = {},
//        onScanDeviceRequest = {
//            scope.launch {
//                showProgressBar = true
//                delay(500)
//                showProgressBar = false
//            }
//        },
//        onWifiStatusChangeRequest = {
//            wifiEnabled = !wifiEnabled
//        },
//    )
//}

/**
 * Stateless
 */
@Composable
fun NearByDevicesRoute(
    modifier: Modifier = Modifier,
    thisDeviceName: String,
    devices: List<ScannnedDevice>,
    isScanning: Boolean,
    headerTitle:String,//property not state
    headerIcon:ImageVector,////property not state
    onScanDeviceRequest: () -> Unit,
    onConnectionRequest: (ScannnedDevice) -> Unit,
    onDisconnectRequest: (ScannnedDevice) -> Unit,
    onConversionScreenOpenRequest: (ScannnedDevice) -> Unit,
    onGroupConversationRequest: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThisDeviceNameSection(thisDeviceName = thisDeviceName)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(4.dp))
        GroupConversationCard(
            modifier=Modifier,
            connectedParticipants = devices.filter { it.connectionStatus==ConnectionStatus.Connected }.size,
            onClick = onGroupConversationRequest
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        DevicesHeaderSection(
            modifier = Modifier.fillMaxWidth(),
            title = headerTitle,
            icon = headerIcon,
            isScanning = isScanning,
            onScanDeviceRequest = onScanDeviceRequest
        )

            Column {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(Modifier)
                NearByDevices(
                    modifier = Modifier
                        .semantics { contentDescription = "NearByDevices List Route" }
                        .testTag("NearByDevicesList"),
                    devices = devices,
                    onConnectionRequest = onConnectionRequest,
                    onDisconnectRequest = onDisconnectRequest,
                    onConversionScreenRequest = onConversionScreenOpenRequest
                )
            }



    }


}

