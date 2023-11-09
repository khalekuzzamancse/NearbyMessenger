package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiAndBroadcastHandler(
    context: Context,
) {

    private val myWifiManager = MyWifiManager(context)
    val scannedDevice = myWifiManager.scannedDevice
    val connectionInfo=myWifiManager.connectionInfo
    val connectedClients=myWifiManager.connectedClients


    private val broadcastManager = WifiDirectBroadcastManager(
        context = context,
        onStateChangeAction = {

        },
        onConnectionChangeAction = {
            myWifiManager.refreshClients()

        }
    )

    fun registerBroadcast() {
        broadcastManager.register()
    }

    fun unregisterBroadcast() {
        broadcastManager.unregister()
    }

    fun scanDevice() {
        myWifiManager.scanDevice()

    }
    fun connectTo(device: WifiP2pDevice){
        myWifiManager.connectWith(device)
    }


}
//devices ->
//val device = devices.firstOrNull()
//if (device != null)
//myWifiManager.connectWith(device)

