package peers.ui.devices_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    devices: List<NearByDevice>,
    wifiEnabled: Boolean,
    showProgressbar: Boolean,
    onScanDeviceRequest: () -> Unit,
    onWifiStatusChangeRequest: () -> Unit,
    onConnectionRequest: (NearByDevice) -> Unit,
    onDisconnectRequest: (NearByDevice) -> Unit,
    onConversionScreenOpenRequest: (NearByDevice) -> Unit,
    onGroupConversationRequest: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DeviceNameSection(thisDeviceName = thisDeviceName)
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(4.dp))
        GroupConversationCard(
            modifier=Modifier,
            connectedParticipants = devices.filter { it.isConnected }.size,
            onClick = onGroupConversationRequest
        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        _WifiDirectDevicesHeader(Modifier.fillMaxWidth(), onScanDeviceRequest)
        if (showProgressbar) {
            _OnLoadingContent(
                modifier = Modifier
                    .semantics { contentDescription = "Route on Loading" }
                    .testTag("OnLoadingContent")
            )
        } else {
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


}

@Composable
private fun _OnLoadingContent(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        _CircularProgressBar()
    }

}

@Composable
private fun _WifiDirectDevicesHeader(
    modifier: Modifier,
    onScanDeviceRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Wifi-Direct Devices", style = MaterialTheme.typography.titleMedium)
        _ScanButton(Modifier, onScanDeviceRequest)
    }

}

@Composable
fun _ScanButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .semantics { contentDescription = "Scan Device " }
            .testTag("ScanDeviceButton"),
    ) {
        Icon(
            imageVector = Icons.Filled.SavedSearch,
            contentDescription = null,
            tint = Color.Red
        )
    }

}

@Composable
private fun _CircularProgressBar(size: Dp = 64.dp) {
    CircularProgressIndicator(
        modifier = Modifier.width(size),
        color = MaterialTheme.colorScheme.surfaceVariant,
        trackColor = MaterialTheme.colorScheme.secondary,
    )
}
