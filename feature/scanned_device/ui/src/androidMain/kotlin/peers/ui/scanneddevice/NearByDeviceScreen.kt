package peers.ui.scanneddevice

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import peers.ui.devices_list.NearByDevice
import peers.ui.devices_list.NearByDevicesRoute


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NearByDeviceScreen(
    modifier: Modifier,
    thisDeviceName:String,
    viewModel: DeviceListViewModel,
    wifiEnabled: Boolean,
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
    _SnackBarDecorator(
        message = snackBarMessage,
    ) { scaffoldPadding ->
        NearByDevicesRoute(
            modifier = modifier.padding(scaffoldPadding),
            thisDeviceName=thisDeviceName,
            devices = viewModel.nearbyDevices.collectAsState(emptyList()).value.map {
                NearByDevice(
                    name = it.name,
                    deviceAddress = it.address,
                    isConnected = it.isConnected
                )
            },
            wifiEnabled = viewModel.isNetworkOn.collectAsState(true).value,
            showProgressbar = viewModel.isDeviceScanning.collectAsState(true).value,
            onDisconnectRequest = {
                viewModel.disconnectAll()
            },
            onConnectionRequest = {
                viewModel.connectTo(it.deviceAddress)
            },
            onConversionScreenOpenRequest = {
                onConversionOpen(it)
            },
            onScanDeviceRequest = {
                if (wifiEnabled)
                    viewModel.scanDevices()
            },
            onWifiStatusChangeRequest = viewModel::onNetworkStatusChangeRequest,
            onGroupConversationRequest = onGroupConversationRequest
        )
        LaunchedEffect(Unit) {
            if (wifiEnabled)
                viewModel.scanDevices()
        }
    }


}

@SuppressLint("ComposableNaming")
@Composable
private fun _SnackBarDecorator(
    message: String?,
    content: @Composable (PaddingValues) -> Unit
) {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        if (message != null)
            hostState.showSnackbar(message = message, duration = SnackbarDuration.Long)
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState)
        }
    ) { scaffoldPadding ->
        content(scaffoldPadding)
    }
}

