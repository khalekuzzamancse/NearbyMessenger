package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
    onConversionOpen: (ip: String) -> Unit = {}
) {
    val viewModel = remember {
        DeviceListViewModel()
    }
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            println("SnackbarMessage:$msg")
            if (msg != null){
                hostState.showSnackbar(msg)
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

