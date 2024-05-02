package navigation.navgraph

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import chatui.viewmodel.ChatViewModel
import peers.ui.misc.DevicesConnectionInfo
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.NearByDeviceScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    thisDeviceName: String,
    deviceListViewModel: DeviceListViewModel,
    chatViewModel: ChatViewModel,
    chatScreenTitle: String,
    wifiEnabled: Boolean,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onGroupConversationRequest: () -> Unit,
) {
    val windowSize = calculateWindowSizeClass().widthSizeClass
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            _DeviceListScreen(
                thisDeviceName = thisDeviceName,
                viewModel = deviceListViewModel,
                wifiEnabled = wifiEnabled,
                onGroupFormed = onGroupFormed,
                onGroupConversationRequest = onGroupConversationRequest
            )


        }

        else -> {
                _DeviceListNConversationScreen(
                    thisDeviceName = thisDeviceName,
                    deviceListViewModel = deviceListViewModel,
                    chatViewModel = chatViewModel,
                    chatScreenTitle = chatScreenTitle,
                    wifiEnabled = wifiEnabled,
                    onGroupFormed = onGroupFormed,
                    onGroupConversationRequest = onGroupConversationRequest
                )


        }
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("ComposableNaming")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun _DeviceListNConversationScreen(
    thisDeviceName: String,
    deviceListViewModel: DeviceListViewModel,
    chatViewModel: ChatViewModel,
    chatScreenTitle: String,
    wifiEnabled: Boolean,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onGroupConversationRequest: () -> Unit,
) {

    val windowSize = calculateWindowSizeClass().widthSizeClass
    val context = LocalContext.current
    val scannedDeviceWeight =
        if (windowSize == WindowWidthSizeClass.Expanded) 0.35f else 0.5f//On medium take 50%,on Expanded takes 35%
    Row {
        Box(Modifier.weight(scannedDeviceWeight)) {
            _DeviceListScreen(
                thisDeviceName = thisDeviceName,
                viewModel = deviceListViewModel,
                wifiEnabled = wifiEnabled,
                onGroupFormed = onGroupFormed,
                onGroupConversationRequest = {
                    //Since already conservation is opened in right side,so need  not propagate it to up
                    //But  the item is already clickable,so google play console will detect the broken functionality
                    //that is why showing a toast
                    Toast.makeText(context, "Already Opened", Toast.LENGTH_SHORT).show()
                }
            )

        }
        Spacer(Modifier.width(16.dp))
        Box(Modifier.weight(1f - scannedDeviceWeight).fillMaxHeight().background(Color.Red)) {
            ConversationScreen(
                chatViewModel = chatViewModel
            )
        }

    }
}

@SuppressLint("ComposableNaming")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
internal fun _DeviceListScreen(
    modifier: Modifier = Modifier,
    thisDeviceName: String,
    viewModel: DeviceListViewModel,
    wifiEnabled: Boolean,
    onGroupFormed: (DevicesConnectionInfo) -> Unit,
    onGroupConversationRequest: () -> Unit
) {
    NearByDeviceScreen(
        modifier = Modifier,
        viewModel = viewModel,
        onGroupFormed = onGroupFormed,
        thisDeviceName = thisDeviceName,
        onConversionOpen = {},
        onGroupConversationRequest = onGroupConversationRequest
    )


}

