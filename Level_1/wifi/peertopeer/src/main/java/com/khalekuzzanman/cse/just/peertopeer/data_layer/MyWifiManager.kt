package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    private val _scannedDevice = MutableStateFlow(emptyList<WifiP2pDevice>())
    val scannedDevice = _scannedDevice.asStateFlow()
    private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    val connectionInfo = _connectionInfo.asStateFlow()
    private val _connectedClients = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
    val connectedClients = _connectedClients.asStateFlow()



    fun refreshClients() {
        manager?.requestGroupInfo(channel) {
            it?.let { info ->
                _connectedClients.value = info.clientList.toList()
            }
        }
    }



    fun scanDevice() {
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                requestScannedDevice()
            }

            override fun onFailure(reasonCode: Int) {
            }

        })
    }

    init {
        manager?.requestConnectionInfo(
            channel
        ) {info ->
            _connectionInfo.value = info
        }


    }

    private fun requestScannedDevice() {
        manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            if (peers != null) {
                val devices = peers.deviceList.toList()
                _scannedDevice.value = devices


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
