package com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
we need to know the wifi status thought the whole    application at any given time,
that is why we keep a single instance
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object WifiAndBroadcastHandlerInstance {
    lateinit var wifiAndBroadcastHandler: WifiAndBroadcastHandler
    fun create(context: Context) {
        wifiAndBroadcastHandler = WifiAndBroadcastHandler(context)
    }

}

/*
/*
Through out the whole application we need to the manage it,
but all application may not observe it such as all the screens may not want to listen the boroadcast,
so do not make it directly singleton but you can less reference of it
 */

 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiAndBroadcastHandler(
    context: Context,
) {

    private val appWifiManager = AppWifiManager(context)
    val connectionInfo = appWifiManager.connectionInfo


    //WifiManager is used to enable or disable the wifi
    private val wifiManager=context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _isWifiEnabled=MutableStateFlow(wifiManager.isWifiEnabled)
    val isWifiEnabled=_isWifiEnabled.asStateFlow()
    val nearByDevices=appWifiManager.nearbyDevices

    fun updateConnectedDeviceInfo()=appWifiManager.updateConnectedDeviceInfo()


    companion object {
        private const val TAG = "WifiAndBroadcastHandlerClass: "
    }


    private val broadcastManager = WifiDirectBroadcastManager(
        context = context,
        onStateChangeAction = {
            Log.d(TAG, "onStateChange")
        },
        onConnectionChangeAction = {
            Log.d(TAG, "onConnectionChange")
           appWifiManager.updateConnectedDeviceInfo()
        },
        onPeersChangeAction = {
          //  Log.d(TAG, " onPeersChange")
        },
        onThisDeviceChangeAction = {
            Log.d(TAG, "  onThisDeviceChange")
        },
        onWifiStateChangedAction = {wifiState->

            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {
                   _isWifiEnabled.value=true
                }
                WifiManager.WIFI_STATE_DISABLED -> {
                    _isWifiEnabled.value=false
                }
            }
        }

        )
    fun disconnectAll()=appWifiManager.disconnect()

    fun registerBroadcast() {
        broadcastManager.register()
    }

    fun unregisterBroadcast() {
        broadcastManager.unregister()
    }

    fun scanDevice() {
        appWifiManager.startScanning()

    }

    fun connectTo(device: WifiP2pDevice) {
        appWifiManager.connectWith(device)
    }


}
//devices ->
//val device = devices.firstOrNull()
//if (device != null)
//myWifiManager.connectWith(device)

