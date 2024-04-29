package wifidirect.connection

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wifidirect.connection.model.ConnectionInfo
import wifidirect.connection.model.ConnectionType
import wifidirect.connection.model.Device
import wifidirect.connection.model.ScannedDevice
import wifidirect.connection.model.ThisDeviceInfo
import wifidirect.connection.model.WifiDirectConnectionInfo

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
    private val _thisDeviceInfo = MutableStateFlow<ThisDeviceInfo?>(null)

    fun getThisDeviceInfo()=_thisDeviceInfo.value

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
    val nearbyDevices: StateFlow<List<Device>> = _nearbyDevices.asStateFlow()
    private val _nearbyDeviceInfo = MutableStateFlow(ScannedDevice())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _nearbyDeviceInfo.collect { peers ->
                _nearbyDevices.value = peers.getDevice()
            }

        }
    }
    private fun ScannedDevice.getDevice() = scannedDevices.map { device ->
        Device(
            name = device.deviceName,
            isConnected = connectedDevices.contains(device),
            device = device
        )
    }


    init {
        requestThisDeviceInfo()
    }

    private fun requestThisDeviceInfo() {
        channel?.let {
            manager?.requestDeviceInfo(it) { device ->
                if (device != null) {
                    _thisDeviceInfo.update {
                        ThisDeviceInfo(
                            name = device.deviceName,
                            address = device.deviceAddress
                        )
                    }
                    log(device.deviceName)
                }
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
    /*
Tracking the connected devices' status enables other parts of the app to make decisions or take actions accordingly.
Storing information about the connection type, whether the device functions as a server or client.

*/

    private fun ConnectionInfo.updateInfo(info: WifiP2pInfo): ConnectionInfo {
        val isConnected = info.groupFormed
        val type: ConnectionType = if (isConnected) {
            if (info.isGroupOwner)
                ConnectionType.Server
            else
                ConnectionType.Client
        } else {
            ConnectionType.NotConnected
        }
        return this.copy(
            type = type,
            groupOwnerAddress = info.groupOwnerAddress,
            isConnected = type !== ConnectionType.NotConnected
        )
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
        requestThisDeviceInfo()//update the this device info
        manager?.requestGroupInfo(channel) { group ->
            _nearbyDeviceInfo.value = _nearbyDeviceInfo.value.updateConnectDevices(group)
            updateConnectionInfo()
        }
    }
   private fun ScannedDevice.updateConnectDevices(group: WifiP2pGroup?): ScannedDevice {
        // group != null ,if there are connected devices/network
        val connectedDevices: List<WifiP2pDevice> = if (group != null) {
            //if it is the group owner then will get the client list
            //otherwise the list is empty though is it is connected
            //if the device is not group owner then its connected with only
            //a single device that is the group owner
            val thisDeviceIsGroupOwner = group.isGroupOwner
            if (thisDeviceIsGroupOwner) {
                group.clientList.toList()
            } else {
                this.connectedDevices + group.owner
            }
        } else {
            emptyList()
        }
        return this.copy(connectedDevices = connectedDevices)
    }


    fun startScanning() {
        requestThisDeviceInfo()//update the this device info
        manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                requestScannedDevice()
            }

            override fun onFailure(reasonCode: Int) {
            }

        })
    }


    fun requestScannedDevice() {
        requestThisDeviceInfo()//update the this device info
        manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
            _nearbyDeviceInfo.value = _nearbyDeviceInfo.value.updateScannedDevices(peers)
        }

    }

    private fun ScannedDevice.updateScannedDevices(peers: WifiP2pDeviceList?): ScannedDevice {
        return if (peers != null) {
            this.copy(scannedDevices = peers.deviceList.toList())
        } else this
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionManager::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }


}

