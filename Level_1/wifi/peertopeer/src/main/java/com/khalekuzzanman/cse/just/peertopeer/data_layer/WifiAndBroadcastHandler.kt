package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiAndBroadcastHandler(
    context: Context,
) {

    private val myWifiManager = MyWifiManager(context)
    val scannedDevice = myWifiManager.scannedDevice
    private val broadcastManager = WifiDirectBroadcastManager(
        context = context,
        onStateChangeAction = {
            scanDeviceAndConnect1st()
        }
    )

    fun registerBroadcast() {
        broadcastManager.register()
    }

    fun unregisterBroadcast() {
        broadcastManager.unregister()
    }

    fun scanDevice() {

    }

    private fun scanDeviceAndConnect1st() {
        myWifiManager.scanDevice()

    }

}
//devices ->
//val device = devices.firstOrNull()
//if (device != null)
//myWifiManager.connectWith(device)

