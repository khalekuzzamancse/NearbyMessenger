package chatbynearbyapi.devices

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import peers.ui.devices.NearByDevice
import peers.ui.misc.SnackBarDecorator
import peers.ui.route.NearByDevicesRoute

@Composable
fun NearByDeviceScreen(
    modifier: Modifier,
    viewModel: DeviceListViewModel,
    thisDeviceName: String,
    onConversionOpen: (NearByDevice) -> Unit,
    onGroupConversationRequest: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackBarMessage = viewModel.errorMessage.collectAsState().value

    SnackBarDecorator(
        message = snackBarMessage,
    ) { scaffoldPadding ->
        NearByDevicesRoute(
            modifier = modifier.padding(scaffoldPadding),
            thisDeviceName = thisDeviceName,
            devices = viewModel.nearbyDevices.collectAsState(emptyList()).value,
            wifiEnabled = true,//TODO :Fix it later
            showProgressbar = false,//= true,//TODO :Fix it later
            isScanning = true,
            onDisconnectRequest = {

            },
            onConnectionRequest = {
                viewModel.connect(it.id)
            },
            onConversionScreenOpenRequest = {
                onConversionOpen(it)
            },
            onScanDeviceRequest = {
                coroutineScope.launch {
                    viewModel.scan()
                }
            },
            onWifiStatusChangeRequest = {},
            onGroupConversationRequest = onGroupConversationRequest
        )
    }
}

