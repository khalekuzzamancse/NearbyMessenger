package chatbynearbyapi.devices

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import peers.ui.devices.ScannnedDevice
import peers.ui.misc.SnackBarDecorator
import peers.ui.route.NearByDevicesRoute

@SuppressLint("ComposableNaming")
@Composable
internal fun _NearByDeviceScreen(
    modifier: Modifier,
    viewModel: DeviceListViewModel,
    thisDeviceName: String,
    onConversionOpen: (ScannnedDevice) -> Unit,
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
            isScanning = viewModel.isScanning.collectAsState().value,
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
            onGroupConversationRequest = onGroupConversationRequest,
            headerTitle = "NearBy Devices",
            headerIcon = Icons.Filled.Devices
        )
    }
}

