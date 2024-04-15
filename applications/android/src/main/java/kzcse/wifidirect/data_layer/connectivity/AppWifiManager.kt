package kzcse.wifidirect.data_layer.connectivity

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kzcse.wifidirect.ui.ui.devices_list.NearByDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
Feature handling by this class:
Scanning for Nearby Devices
Retrieving Scanned Device List
Disconnecting from a Device or Group
Connecting to a Device
Updating Information about Connected Devices
Keeping Connection status

 */

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AppWifiManager(
    context: Context
) {

    companion object {
        private const val TAG = "AppWifiManagerLog: "

    }

    //for managing wifi direct
    private val manager: WifiP2pManager? =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    private var channel: WifiP2pManager.Channel? =
        manager?.initialize(context, context.mainLooper, null)

    //keeping important states
    //connected client is the other devices except the group owner
    //keeping connection state

    // private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    //val connectionInfo = _connectionInfo.asStateFlow()
    private val _connInfo = MutableStateFlow(ConnectionInfo())
    val connectionInfo = _connInfo.asStateFlow()


    private val _nearbyDevices = MutableStateFlow<List<NearByDevice>>(emptyList())
    val nearbyDevices = _nearbyDevices.asStateFlow()


    private val _nearbyDeviceInfo = MutableStateFlow(ScannedDevice())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _nearbyDeviceInfo.collect { peers ->
                _nearbyDevices.value = peers.getDevice()
                Log.d(TAG, "Nearby: ${peers.getDevice()}")
            }

        }
    }

    fun updateConnectionInfo() {
        manager?.requestConnectionInfo(
            channel
        ) { info ->
            _connInfo.value = _connInfo.value.updateInfo(info)
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
                        updateConnectionInfo()

                    }

                    override fun onFailure(reason: Int) {
//                        Log.d(TAG, "Connected:Fail")
                        updateConnectionInfo()
//
                    }
                }
            )
        }
    }

    fun disconnect() {
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "Disconnect:successful disconnect")
                updateConnectedDeviceInfo()
                startScanning()
                updateConnectionInfo()
            }

            override fun onFailure(reason: Int) {
                updateConnectionInfo()
            }
        })
    }


    fun updateConnectedDeviceInfo() {
        manager?.requestGroupInfo(channel) { group ->
            _nearbyDeviceInfo.value = _nearbyDeviceInfo.value.updateConnectDevices(group)
            updateConnectionInfo()
        }
    }


    fun startScanning() {
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                requestScannedDevice()
            }

            override fun onFailure(reasonCode: Int) {
            }

        })
    }


    fun requestScannedDevice() {
        // Log.d(TAG, "requestScannedDevice()")
        manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            _nearbyDeviceInfo.value = _nearbyDeviceInfo.value.updateScannedDevices(peers)
        }

    }


}

