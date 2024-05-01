package chatbywifidirect.devices

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.NearByDevice
import peers.ui.misc.DevicesConnectionInfo
import peers.ui.misc.SnackBarDecorator
import peers.ui.route.NearByDevicesRoute

@Composable
internal fun _NearByDeviceScreen(
    modifier: Modifier,
    thisDeviceName:String,
    viewModel: DeviceListViewModel,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onConversionOpen: (NearByDevice) -> Unit,
    onGroupConversationRequest:()->Unit,
) {
    val snackBarMessage = viewModel.message.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.connectionInfo.collect { info ->
            val ownerIP = info.groupOwnerIP
            if (ownerIP != null) {
                onGroupFormed(
                    DevicesConnectionInfo(
                        groupOwnerIP = ownerIP,
                        isGroupOwner = info.isGroupOwner,
                        isConnected = info.isConnected,
                        groupOwnerName = info.groupOwnerName
                    )
                )
            }
        }
    }
    SnackBarDecorator(
        message = snackBarMessage,
    ) { scaffoldPadding ->
        NearByDevicesRoute(
            modifier = modifier.padding(scaffoldPadding),
            thisDeviceName=thisDeviceName,
            devices = viewModel.nearbyDevices.collectAsState(emptyList()).value.map {
                NearByDevice(
                    name = it.name,
                    id = it.address,
                    connectionStatus = if (it.isConnected)ConnectionStatus.Connected else ConnectionStatus.NotConnected
                )
            },
            onDisconnectRequest = {
                viewModel.disconnectAll()
            },
            onConnectionRequest = {
                viewModel.connectTo(it.id)
            },
            onConversionScreenOpenRequest = {
                onConversionOpen(it)
            },
            isScanning = viewModel.isDeviceScanning.collectAsState().value,
            onScanDeviceRequest = {

                    viewModel.scanDevices()
            },
            onGroupConversationRequest = onGroupConversationRequest,
            headerIcon = Icons.Default.WifiFind,
            headerTitle = "Wifi Direct Devices"
        )
        LaunchedEffect(Unit) {
                viewModel.scanDevices()
        }
    }


}

