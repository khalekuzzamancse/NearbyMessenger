package wifidirect.connection

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 *  It handle :
 * * Scanning for Nearby Devices
 * * Retrieving Scanned Device List
 * * Disconnecting from a Device or Group
 * * Connecting to a Device
 * * Updating Information about Connected Devices
 * * Keeping Connection status
 */

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class ConnectionManager(context: Context) {

    //for managing wifi direct
    private val manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    private var channel = manager?.initialize(context, context.mainLooper, null)

    //keeping important states
    //connected client is the other devices except the group owner
    //keeping connection state

    // private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    //val connectionInfo = _connectionInfo.asStateFlow()
    private val _wifiDirectInfoConnectionInfo = MutableStateFlow(WifiDirectConnectionInfo())
    val wifiDirectConnectionInfo = _wifiDirectInfoConnectionInfo.asStateFlow()
    private val _connectionInfo = MutableStateFlow(ConnectionInfo())
    val connectionInfo = _connectionInfo.asStateFlow()
    private val _nearbyDevices = MutableStateFlow<List<Device>>(emptyList())
    val nearbyDevices: Flow<List<Device>> = _nearbyDevices.asStateFlow()
    private val _nearbyDeviceInfo = MutableStateFlow(ScannedDevice())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _nearbyDeviceInfo.collect { peers ->
                _nearbyDevices.value = peers.getDevice()
            }

        }
    }


    fun updateConnectionInfo() {
        manager?.requestConnectionInfo(channel) { wifiP2PInfo ->
            wifiP2PInfo?.let { info ->
                val newInfo = WifiDirectConnectionInfo(
                    groupOwnerIP = info.groupOwnerAddress?.hostAddress,//if not connected any devised then groupOwnerAddress==null
                    isGroupOwner = info.isGroupOwner,
                    isConnected = info.groupFormed,
//                    groupOwnerName = info.groupOwnerAddress?.hostName //FIX bug why causes null pointer execution or   android.os.NetworkOnMainThreadException
                    groupOwnerName = null
                )
//                log("$newInfo")
                _wifiDirectInfoConnectionInfo.update { newInfo }
                _connectionInfo.value = _connectionInfo.value.updateInfo(info)
            }
        }
    }


    /**
     * @param deviceAddress Taking as  String to decouple with some
     * custom or built in data type such as [WifiP2pDevice]
     */
    fun connectWith(deviceAddress: String) {
        val config = WifiP2pConfig()
        config.deviceAddress = deviceAddress
        channel.also { channel ->
            manager?.connect(
                channel,
                config,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        updateConnectionInfo()
                    }

                    override fun onFailure(reason: Int) {
                        updateConnectionInfo()
                    }
                }
            )
        }
    }

    fun disconnect() {
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
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
        manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            _nearbyDeviceInfo.value = _nearbyDeviceInfo.value.updateScannedDevices(peers)
        }

    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionManager::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }


}

