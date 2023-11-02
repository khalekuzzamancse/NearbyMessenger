package com.khalekuzzanman.cse.just.peertopeer.ui.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khalekuzzanman.cse.just.peertopeer.data_layer.WifiAndBroadcastHandler

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun ScanDevice() {
    val context = LocalContext.current
    val wifiManager = WifiAndBroadcastHandler(context)



    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Button(onClick = {

            wifiManager.scanDevice()

        }) {
            Text(text = "Scan")
        }
        DeviceList(devices = wifiManager.scannedDevice.collectAsState().value)

    }

    LaunchedEffect(Unit) {
        wifiManager.registerBroadcast()

    }


}

@Composable
fun DeviceList(
    devices: List<String>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        devices.forEach {
            Text(text = it)
        }

    }

}