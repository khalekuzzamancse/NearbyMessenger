package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
    viewModel: DeviceListViewModel,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onConversionOpen: (ip: String) -> Unit = {},
) {


    viewModel.connectionInfo
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            if (msg != null)
                hostState.showSnackbar(msg)
        }
        viewModel.connectionInfo.collect { connectionInfo ->
            println("ConnectionInfoLog:$connectionInfo")
        }
    }
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
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState)
        }
    ) {
        NearByDevicesRoute(
            modifier = Modifier.padding(it),
            devices = viewModel.nearbyDevices.collectAsState(emptyList()).value.map {
                NearByDevice(
                    name = it.name,
                    ip = it.address,
                    isConnected = it.isConnected
                )
            },
            wifiEnabled = viewModel.isNetworkOn.collectAsState(true).value,
            showProgressbar = viewModel.isDeviceScanning.collectAsState(true).value,
            onDisconnectRequest = {
                viewModel.disconnectAll()
            },
            onConnectionRequest = {
                viewModel.connectTo(it.ip)
            },
            onConversionScreenOpenRequest = {
                onConversionOpen(it.ip)
            },
            onScanDeviceRequest = viewModel::scanDevices,
            onWifiStatusChangeRequest = viewModel::onNetworkStatusChangeRequest
        )
        LaunchedEffect(Unit) {
            viewModel.scanDevices()
        }

    }


}

@Composable
private fun SnackBarDecorator() {

}

