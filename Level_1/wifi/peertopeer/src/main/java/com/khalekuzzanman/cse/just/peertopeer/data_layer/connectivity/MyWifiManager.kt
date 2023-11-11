package com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MyWifiManager(
    context: Context
) {

    companion object {
        private const val TAG = "MyWifiManagerClass: "

    }

    private val manager: WifiP2pManager? =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    private var channel: WifiP2pManager.Channel? =
        manager?.initialize(context, context.mainLooper, null)

    private val _scannedDevice = MutableStateFlow(emptyList<WifiP2pDevice>())
    val scannedDevice = _scannedDevice.asStateFlow()
    private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    val connectionInfo = _connectionInfo.asStateFlow()

    //connected client is the other device except itself
    private val _connectedDevices = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
    val connectedDevices = _connectedDevices.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _connectedDevices.collect {
                Log.d(TAG, "ConnectedDevices:${it}")
            }

        }

    }
    fun disconnectDevice() {

        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Disconnect:successful disconnect")
                updateConnectedDeviceInfo()
            }

            override fun onFailure(reason: Int) {
            }
        })
    }



    fun updateConnectedDeviceInfo() {
        manager?.requestGroupInfo(channel) { group ->
            val isGroupFormed = group != null //if there are connected devices/network
            if (isGroupFormed) {
                //if it is the group owner then will get the client list
                //otherwise the list is empty though is it is connected
                //if the device is not group owner then its connected with only
                //a single device that is the group owner
                val thisDeviceIsGroupOwner = group.isGroupOwner
                if (thisDeviceIsGroupOwner) {
                    _connectedDevices.value = group.clientList.toList()
                } else {
                    _connectedDevices.value = _connectedDevices.value + group.owner
                }
            }
            else{
                _connectedDevices.value = emptyList()
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
    ) { info ->
        _connectionInfo.value = info
    }


}

fun requestScannedDevice() {
    // Log.d(TAG, "requestScannedDevice()")
    manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
        if (peers != null) {
            val devices = peers.deviceList.toList()
            _scannedDevice.value = devices
            //    Log.d(TAG, "Scanned devices:\n$devices")

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
//                        Log.d(TAG, "Connected:${device.deviceName}")

                }

                override fun onFailure(reason: Int) {
//                        Log.d(TAG, "Connected:Fail")
//
                }
            }
        )
    }
}


}
