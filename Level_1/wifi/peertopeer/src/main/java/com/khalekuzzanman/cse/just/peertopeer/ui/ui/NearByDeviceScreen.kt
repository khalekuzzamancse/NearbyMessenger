package com.khalekuzzanman.cse.just.peertopeer.ui.ui

import CommunicationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khalekuzzanman.cse.just.peertopeer.Client
import com.khalekuzzanman.cse.just.peertopeer.Server
import com.khalekuzzanman.cse.just.peertopeer.data_layer.WifiAndBroadcastHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NearByDeviceScreenModel(
    wifiManager: WifiAndBroadcastHandler
) {
    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled = _wifiEnabled.asStateFlow()
    fun onWifiStatusChangeRequest() {
        _wifiEnabled.update { !it }
    }

    private var connectedClients = wifiManager.connectedClients

    private var scannedDevice = wifiManager.scannedDevice

    var devices = MutableStateFlow<List<NearByDevice>>(emptyList())

    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            scannedDevice.collect {
                devices.value = it.map { device ->
                    NearByDevice(
                        name = device.deviceName,
                        isConnected = connectedClients.value.contains(device)
                    )
                }
            }
        }
        scope.launch {
            connectedClients.collect {
                devices.value = scannedDevice.value.map { device ->
                    NearByDevice(
                        name = device.deviceName,
                        isConnected = it.contains(device)
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun NearByDeviceScreen() {
    //
    val context = LocalContext.current
    val wifiManager = remember {
        WifiAndBroadcastHandler(context)
    }

    val state = remember {
        NearByDeviceScreenModel(wifiManager)
    }
    val wifiEnabled = state.wifiEnabled.collectAsState().value

    val connectedClients = wifiManager.connectedClients.collectAsState().value
    var devices by remember {
        mutableStateOf(emptyList<NearByDevice>())
    }

    var communicationManager: CommunicationManager? = remember {
        null
    }



    connectedClients.forEach {
        Log.i("ScannedDevice", it.toString())
    }


    LaunchedEffect(Unit) {
        wifiManager.registerBroadcast()
        wifiManager.scanDevice()

        wifiManager.connectedClients.collect {
            devices = wifiManager.scannedDevice.value.map { device ->
                NearByDevice(name = device.deviceName, isConnected = it.contains(device))
            }
        }
    }
    LaunchedEffect(Unit) {
        wifiManager.scannedDevice.collect {
            devices = it.map { device ->
                NearByDevice(
                    name = device.deviceName,
                    isConnected = connectedClients.contains(device)
                )
            }
        }
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
                    IconButton(onClick = state::onWifiStatusChangeRequest) {
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

            FlowRow {
                Button(onClick = {
                    val info = wifiManager.connectionInfo.value
                    if (info != null) {
                        communicationManager = CommunicationManager(info)
                        communicationManager?.listenReceived()

                    }

                }) {
                    Text(text = CommunicationManager.connectionType.collectAsState().value.toString())
                }
                Button(onClick = {
                    communicationManager?.sendData()

                }) {
                    Text(text = "Send Data")
                }

            }

            wifiManager.connectionInfo.collectAsState().value?.let { connectionInfo ->
                val isHost = connectionInfo.groupFormed && connectionInfo.isGroupOwner

                if (isHost) {
                    Log.i("ConnectionInfo: ", "Host")
                    Log.i("ConnectionInfo: ", connectionInfo.groupOwnerAddress.toString())

                } else {
                    Log.i("ConnectionInfo: ", "Client")
                    connectionInfo.groupOwnerAddress?.let {
                        it.hostAddress?.let { it1 -> Log.i("ConnectionInfo: ", it1) }
                    }


                }

            }

            NearByDevices(
                devices = state.devices.collectAsState().value,
            )


        }


    }


}


