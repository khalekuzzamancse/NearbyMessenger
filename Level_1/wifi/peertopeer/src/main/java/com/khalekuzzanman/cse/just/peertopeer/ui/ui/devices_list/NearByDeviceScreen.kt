package com.khalekuzzanman.cse.just.peertopeer.ui.ui.devices_list

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khalekuzzanman.cse.just.peertopeer.CircularProgressBar
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandler
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandlerInstance
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class NearByDeviceScreenModel(
    private val wifiManager: WifiAndBroadcastHandler
) {
    companion object {
        private const val TAG = "NearByDeviceScreenLog: "
    }

    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled = _wifiEnabled.asStateFlow()
    fun onWifiStatusChangeRequest() {
        _wifiEnabled.update { !it }
    }

    var devices = wifiManager.nearByDevices



    private var socketManager: SocketManager? = null

    fun onConnectionRequest() {
        val info = wifiManager.connectionInfo.value
        socketManager = SocketManager(info)
        socketManager?.listenReceived()

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun NearByDeviceScreen(
    onConversionScreenOpen: (WifiP2pDevice) -> Unit = {}
) {
    val wifiManager = remember {
        WifiAndBroadcastHandlerInstance.wifiAndBroadcastHandler

    }

    val viewModel = remember {
        NearByDeviceScreenModel(wifiManager)
    }
    val showProgressbar = viewModel.devices.collectAsState().value.isEmpty()
    val wifiEnabled = viewModel.wifiEnabled.collectAsState().value


    LaunchedEffect(Unit) {
        wifiManager.registerBroadcast()
        wifiManager.scanDevice()

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
                        wifiManager.scanDevice()
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


            if (showProgressbar) {
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
                        wifiManager.connectTo(it)
                    },
                    onDisconnectRequest = {
                        wifiManager.disconnectAll()
                    },
                    onConversionScreenRequest = onConversionScreenOpen
                )
            }


        }


    }


}


