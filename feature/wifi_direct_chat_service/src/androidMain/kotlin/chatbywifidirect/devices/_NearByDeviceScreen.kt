package chatbywifidirect.devices

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import peers.ui.devices.ScannnedDevice
import peers.ui.misc.DevicesConnectionInfo
import peers.ui.misc.SnackBarDecorator
import peers.ui.route.NearByDevicesRoute
@Suppress("ComposableNaming")
@Composable
internal fun _NearByDeviceScreen(
    modifier: Modifier,
    thisDeviceName:String,
    viewModel: DeviceListViewModel,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onConversionOpen: (ScannnedDevice) -> Unit,
    onGroupConversationRequest:()->Unit,
) {
    val snackBarMessage = viewModel.message.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.connectionInfo.collect { info ->
            if (info != null) {
                val ownerIP =info.groupOwnerIp
                onGroupFormed(
                    DevicesConnectionInfo(
                        groupOwnerIP = ownerIP,
                        isGroupOwner = info.isThisDeviceGroupOwner,
                        isConnected = true,
                        //TODO: since group is formed so connected(for client),check for the group owner,is it connected or not with the client
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
            devices = viewModel.nearbyDevices.collectAsState(emptyList()).value,
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

