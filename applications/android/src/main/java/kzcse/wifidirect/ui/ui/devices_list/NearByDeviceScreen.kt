package kzcse.wifidirect.ui.ui.devices_list

import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kzcse.wifidirect.CircularProgressBar
import kzcse.wifidirect.data_layer.connectivity.WifiAndBroadcastHandler
import kzcse.wifidirect.data_layer.connectivity.WifiAndBroadcastHandlerInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class NearByDeviceScreenModel(
    private val wifiManager: WifiAndBroadcastHandler =
        WifiAndBroadcastHandlerInstance.wifiAndBroadcastHandler
) : ViewModel() {
    companion object {
        private const val TAG = "NearByDeviceScreenLog: "
    }

    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled = _wifiEnabled.asStateFlow()
    fun onWifiStatusChangeRequest() {
        _wifiEnabled.update { !it }
    }

    private val _showProgressbar = MutableStateFlow(true)
    val showProgressbar = _showProgressbar.asStateFlow()

    val devices = wifiManager.nearByDevices
    init {
        viewModelScope.launch {
            devices.collect {
                _showProgressbar.value = it.isEmpty()
            }
        }
    }

    fun scanDevices() = wifiManager.scanDevice()

    fun connectTo(device: WifiP2pDevice) = wifiManager.connectTo(device)
    fun disconnectFrom(device: WifiP2pDevice) = wifiManager.disconnectAll()



}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NearByDeviceScreen(
    onConversionScreenOpen: (WifiP2pDevice) -> Unit = {}
) {
    val viewModel = remember {
        NearByDeviceScreenModel()
    }

    val wifiEnabled = viewModel.wifiEnabled.collectAsState().value


    LaunchedEffect(Unit) {
        viewModel.scanDevices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Nearby Devices", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.scanDevices()
                    }) {
                        Icon(
                            imageVector =
                            Icons.Filled.SavedSearch,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                    IconButton(onClick = viewModel::onWifiStatusChangeRequest) {
                        if (wifiEnabled) {
                            Icon(
                                imageVector =
                                Icons.Filled.WifiOff,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector =
                                Icons.Filled.Wifi,
                                contentDescription = null,
                                tint = Color.Blue
                            )

                        }

                    }

                }

            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.showProgressbar.collectAsState().value) {
                Box(
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                )
                {
                    CircularProgressBar()
                }

            } else {
                NearByDevices(
                    devices = viewModel.devices.collectAsState().value,
                    onConnectionRequest = {
                        viewModel.connectTo(it)
                    },
                    onDisconnectRequest = {
                        viewModel.disconnectFrom(it)
                    },
                    onConversionScreenRequest = onConversionScreenOpen
                )
            }


        }


    }


}


