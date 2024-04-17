package kzcse.wifidirect.ui.ui.devices_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import peers.devices_list.NearByDevice
import peers.devices_list.NearByDevicesRoute


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NearByDeviceScreen(
    onConversionOpen: (ip:String) -> Unit = {}
) {
    val viewModel = remember {
        DeviceListViewModel()
    }
    NearByDevicesRoute(
        devices = viewModel.nearbyDevices.collectAsState().value.map {
            NearByDevice(
                name = it.name,
                ip = it.device.deviceAddress,
                isConnected = it.isConnected
            )
        },
        wifiEnabled = viewModel.wifiEnabled.collectAsState().value,
        showProgressbar = viewModel.isDeviceScanning.collectAsState().value,
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
        onWifiStatusChangeRequest = viewModel::onWifiStatusChangeRequest
    )
        LaunchedEffect(Unit) {
            viewModel.scanDevices()
        }


}


