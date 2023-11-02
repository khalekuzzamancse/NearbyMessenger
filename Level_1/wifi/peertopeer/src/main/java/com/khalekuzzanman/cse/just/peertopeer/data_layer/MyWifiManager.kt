package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MyWifiManager(
    context: Context
) {
    private val tag = "PeerToPeerApp: -> "
    private val manager: WifiP2pManager? =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    private var channel: WifiP2pManager.Channel? =
        manager?.initialize(context, context.mainLooper, null)

    private val _scannedDevice = MutableStateFlow(emptyList<String>())
    val scannedDevice = _scannedDevice.asStateFlow()


    fun scanDevice() {
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                requestScannedDevice()
            }

            override fun onFailure(reasonCode: Int) {
            }

        })
    }

   private fun requestScannedDevice() {
        manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            if (peers != null) {
                _scannedDevice.value = peers.deviceList.toList().map { it.deviceName }

            }
        }
    }

    fun connectWith(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        channel.also { channel ->
            manager?.connect(
                channel,
                config,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(tag, "Connected:${device.deviceName}")
                    }

                    override fun onFailure(reason: Int) {
                        Log.d(tag, "Connected:Fail")
                    }
                }
            )
        }
    }
}