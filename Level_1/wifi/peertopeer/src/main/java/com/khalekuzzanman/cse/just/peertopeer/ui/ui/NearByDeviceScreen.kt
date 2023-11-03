package com.khalekuzzanman.cse.just.peertopeer.ui.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.khalekuzzanman.cse.just.peertopeer.data_layer.WifiAndBroadcastHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NearByDeviceScreenModel {
    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled = _wifiEnabled.asStateFlow()
    fun onWifiStatusChangeRequest() {
        _wifiEnabled.update { !it }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
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
        NearByDeviceScreenModel()
    }
    val wifiEnabled = state.wifiEnabled.collectAsState().value

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


            //    DeviceList(devices = wifiManager.scannedDevice.collectAsState().value)
            NearByDevices(
                devices = listOf("Md Abul Kalam", "Mr Bean", "Galaxy Tab", "Sumsung A5"),
                connectedDeviceIndex = 0
            )

        }


    }


}


