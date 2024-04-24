package peers.ui.devices_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable

fun NearByDevicesRoutePreview() {
    var wifiEnabled by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val devices = listOf(
        NearByDevice(name = "Md Abdul", isConnected = true, ip = "1234"),
        NearByDevice(name = "Mr Bean", isConnected = false, ip = "1234"),
        NearByDevice(name = "Galaxy Tab", isConnected = false, ip = "1234"),
        NearByDevice(name = "Samsung A5", isConnected = false, ip = "1234"),
    )
    NearByDevicesRoute(
        devices = devices,
        wifiEnabled = wifiEnabled,
        showProgressbar = showProgressBar,
        onDisconnectRequest = {},
        onConnectionRequest = {},
        onConversionScreenOpenRequest = {},
        onScanDeviceRequest = {
            scope.launch {
                showProgressBar = true
                delay(500)
                showProgressBar = false
            }
        },
        onWifiStatusChangeRequest = {
            wifiEnabled = !wifiEnabled
        }
    )
}

/**
 * Stateless
 */
@Composable
fun NearByDevicesRoute(
    modifier: Modifier=Modifier,
    devices: List<NearByDevice>,
    wifiEnabled: Boolean,
    showProgressbar: Boolean,
    onScanDeviceRequest: () -> Unit,
    onWifiStatusChangeRequest: () -> Unit,
    onConnectionRequest: (NearByDevice) -> Unit,
    onDisconnectRequest: (NearByDevice) -> Unit,
    onConversionScreenOpenRequest: (NearByDevice) -> Unit,
) {
    Scaffold(
        topBar = {
            _TopBar(
                wifiEnabled = wifiEnabled,
                onScanDeviceRequest = onScanDeviceRequest,
                onWifiStatusChangeRequest = onWifiStatusChangeRequest
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = modifier
                .padding(scaffoldPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showProgressbar) {
                _OnLoadingContent(
                    modifier = Modifier.padding(scaffoldPadding)
                        .semantics { contentDescription = "Route on Loading" }
                        .testTag("OnLoadingContent")
                )
            } else {
                NearByDevices(
                    modifier = Modifier.padding(scaffoldPadding)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _TopBar(
    wifiEnabled: Boolean,
    onScanDeviceRequest: () -> Unit,
    onWifiStatusChangeRequest: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = "Nearby Devices", style = MaterialTheme.typography.titleLarge)
        },
        actions = {
            IconButton(
                onClick = onScanDeviceRequest,
                modifier = Modifier
                    .semantics { contentDescription = "Scan Device " }
                    .testTag("ScanDeviceButton"),
            ) {
                Icon(
                    imageVector = Icons.Filled.SavedSearch,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
            IconButton(
                onClick = onWifiStatusChangeRequest,
                modifier = Modifier
                    .testTag("WifiStatusChangeButton")
            ) {
                if (wifiEnabled) {
                    Icon(
                        imageVector = Icons.Filled.WifiOff,
                        contentDescription = "wifi off",
                        tint = Color.Red,
                        modifier = Modifier
                            .testTag("WifiOffIcon")
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Wifi,
                        contentDescription = "wifi on",
                        tint = Color.Blue,
                        modifier = Modifier
                            .testTag("WifiOnIcon")
                    )

                }

            }

        }

    )

}

@Composable
private fun _CircularProgressBar(size: Dp = 64.dp) {
    CircularProgressIndicator(
        modifier = Modifier.width(size),
        color = MaterialTheme.colorScheme.surfaceVariant,
        trackColor = MaterialTheme.colorScheme.secondary,
    )
}
