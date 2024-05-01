package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.NearByDevice
import peers.ui.misc.DevicesConnectionInfo
import peers.ui.misc.SnackBarDecorator
import peers.ui.route.NearByDevicesRoute


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NearByDeviceScreen(
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
            isScanning = true,
            onScanDeviceRequest = {

                    viewModel.scanDevices()
            },
            onGroupConversationRequest = onGroupConversationRequest,
            headerIcon = Icons.Default.DeviceUnknown,
            headerTitle = "No Title"
        )
        LaunchedEffect(Unit) {
                viewModel.scanDevices()
        }
    }


}

