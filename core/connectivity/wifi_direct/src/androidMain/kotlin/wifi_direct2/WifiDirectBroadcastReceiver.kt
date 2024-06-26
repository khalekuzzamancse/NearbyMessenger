package wifi_direct2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.MacAddress
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import wifi_direct2.callback.ActionListenerImpl
import wifi_direct2.callback.ConnectionInfoListenerImpl
import wifi_direct2.callback.PeerListListenerImpl
import wifi_direct2.model.ConnectionInfo
import wifi_direct2.model.Peer
import wifi_direct2.model.PeerStatus
import kotlin.coroutines.resume

/**
 * - A generic broadcast receiver simply requires you to provide a list of relevant intents,
 * and it will handle the broadcasting process for you
 * - Uses
 * ```kotlin
 *    manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
 *    channel = manager.initialize(this, mainLooper, null)
 * ```
 */
@Suppress("FunctionName")
class WifiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : BroadcastReceiver() {

    private val _peers = MutableStateFlow<List<Peer>>(emptyList())
    val peers = _peers.asStateFlow()

    private val _isP2pEnabled = MutableStateFlow(false)
    val isP2pEnabled = _isP2pEnabled.asStateFlow()

    /** Only available when group is formed means the device are connected*/
    private val _connectionInfo = MutableStateFlow<ConnectionInfo?>(null)
    val connectionInfo = _connectionInfo.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering = _isDiscovering.asStateFlow()

    //


    /**
     * - In order to successful find the devices,all devices need to start discovery process
     * Keep in mind that this only initiates peer discovery. The discoverPeers() method starts the discovery process and then
     * immediately returns. The system notifies you if the peer discovery process is successfully initiated by calling methods
     * in the provided action listener.Also, discovery remains active until a connection is initiated or a P2P group is formed.
     * */
    suspend fun startDiscovery(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            manager.discoverPeers(
                channel,
                ActionListenerImpl(
                    _onSuccess = {
                        continuation.resume(Result.success(Unit))
                    },
                    _onFailure = {
                        continuation.resume(Result.failure(Throwable()))
                    }
                )
            )
        }
    }


    /**
     * - It will just initiate the connection,and return if the initiation is success or not
     * - The device is connected successfully or not will be notified via  broadcast on the [ConnectionInfoListenerImpl]
     * @param deviceAdd is the deviceAddress
     *
     */
    suspend fun initiateConnection(deviceAdd: String): Result<Unit> {
        val config = WifiP2pConfig().apply {
            deviceAddress = deviceAdd
            wps.setup = WpsInfo.PBC
        }
        return suspendCancellableCoroutine { continuation ->
            manager.connect(channel, config, ActionListenerImpl(
                _onSuccess = {
                    // Connection attempt succeeded; notification will come from the BroadcastReceiver so Ignore for now
                    continuation.resume(Result.success(Unit))
                },
                _onFailure = {
                    continuation.resume(Result.failure(it))
                }
            ))
        }
    }

    suspend fun disconnectRequest(): Result<Unit> {
        return removeGroup()
    }

    /**
     * - Will remove the entire group so all client  will be disconnected
     * - TODO: To disconnect a particular client use the [removeClient]
     */
    private suspend fun removeGroup(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            manager.removeGroup(channel, ActionListenerImpl(
                _onSuccess = {
                    // Connection attempt succeeded; notification will come from the BroadcastReceiver so Ignore for now
                    continuation.resume(Result.success(Unit))
                },
                _onFailure = {
                    continuation.resume(Result.failure(it))
                }
            ))
        }
    }

    /**
     * TODO : Refactor it later,if want to disconnect only a single client
     */
    private suspend fun removeClient(peerAddress: MacAddress): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            manager.removeClient(channel, peerAddress, ActionListenerImpl(
                _onSuccess = {
                    // Connection attempt succeeded; notification will come from the BroadcastReceiver so Ignore for now
                    continuation.resume(Result.success(Unit))
                },
                _onFailure = {
                    continuation.resume(Result.failure(it))
                }
            ))
        }
    }

    /**
     * If each of the devices in your group supports Wi-Fi direct, you don't need to explicitly ask for the group's password when connecting.
     * To allow a device that doesn't support Wi-Fi Direct to join a group,however, you need to retrieve this password by calling requestGroupInfo()
     */
    fun requestGroupInfo() {
        manager.requestGroupInfo(channel) { group ->
            val groupPassword = group.passphrase
        }
    }

    /**
     * - This will request for a group creation,the group is successfully created or not will be known by
     * requestGroupInfo
     *- If you want the device running your app to serve as the group owner for a network that includes legacy devices—that is,
     *  devices that don't support Wi-Fi Direct—you follow the same sequence of steps as in the Connect to a Peer section,
     *  except you create a new WifiP2pManager.ActionListener using createGroup() instead of connect().
     * The callback handling within the WifiP2pManager.ActionListener is the same, as shown in the following code snippet:
     * - After you create a group, you can call requestGroupInfo() to retrieve details about the peers on the network, including device names and connection statuses.
     * - Note: If all the devices in a network support Wi-Fi Direct, you can use the connect() method on each device because the method then creates
     * the group and selects a group owner automatically.
     *
     */
    suspend fun createGroup(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            manager.createGroup(channel, ActionListenerImpl(
                _onSuccess = {
                    //  attempt succeeded;
                    continuation.resume(Result.success(Unit))
                },
                _onFailure = {
                    continuation.resume(Result.failure(it))
                }
            ))
        }

    }


    private val connectionListener = ConnectionInfoListenerImpl(
        onConnectionInfoAvailable = { connectionInfo ->
            _connectionInfo.update { connectionInfo }
            log("Group Owner IP: $connectionInfo")
        }
    )

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wi-Fi Direct mode is enabled or not, alert the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                //Since broadcast so it is observable,when wifi or wifi direct is enabled or disable it will execute automatically
                val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                _isP2pEnabled.update { isWifiP2pEnabled }
                log("Wifi P2P enabled:$isWifiP2pEnabled")
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Request available peers from the wifi p2p manager. This is an  asynchronous call and the calling activity is notified with a callback
                // on PeerListListener.onPeersAvailable()
                manager.requestPeers(channel, PeerListListenerImpl(
                    onPeerListChanged = { wifiP2pDevices ->
                        _updatePeers(wifiP2pDevices) //Can causes problem,it will make the status InitiatedConnection to Discover
                        wifiP2pDevices.forEach {
                            log("P2pDevice: $it")
                        }
                    }
                ))

            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                /*
                    starting with Android 10 (API level 29), the WIFI_P2P_CONNECTION_CHANGED_ACTION broadcast is no longer sticky. This change means:
                      Non-Sticky Behavior: When your app registers to receive this broadcast, it will not receive the last broadcasted intent
                      immediately upon registration if it hasn't captured it live.
                       to get the current connection state or to check if a connection is established, you should use methods provided by the WifiP2pManager API like requestConnectionInfo() which needs
                       to be called explicitly to obtain the current connection information.
                 */

                // Connection state changed! We should probably do something about that.
                val networkInfo: NetworkInfo? =
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)!!

                if (networkInfo?.isConnected == true) {
                    // We are connected with the other device, request connection  info to find group owner IP
                    manager.requestConnectionInfo(channel, connectionListener)
                }


            }

            WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                /*
                 Broadcast intent action indicating that peer discovery has either started or stopped. One extra EXTRA_DISCOVERY_STATE
                indicates whether discovery has started or stopped.
                 Note that discovery will be stopped during a connection setup. If the application tries to re-initiate discovery during this time,
                  it can fail.
                 */
                val code = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1)
                _isDiscovering.update {
                    when (code) {
                        WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED -> true
                        WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED -> false
                        else -> false
                    }
                }
                log("IsDiscovering: $code")
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                //Update this device info such as
                val thisDevice =
                    intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)!!
            }
        }
    }


    //TODO :Helper Methods ---- Helper Methods ---- Helper Methods---- Helper Methods ---- Helper Methods
    //TODO :Helper Methods ---- Helper Methods ---- Helper Methods---- Helper Methods ---- Helper Methods


    private fun _updatePeers(p2pDevices: List<WifiP2pDevice>) {
        _peers.update {
            p2pDevices.map {
                it._toPeer()
            }
        }
    }


    private fun WifiP2pDevice._toPeer() = Peer(
        deviceName = deviceName,
        deviceAddress = deviceAddress,
        connectionStatus = status._toConnectionStatus(),
        isGroupOwner = isGroupOwner
    )

    /**
     * From the docs: https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pDevice
     * Status Codes: 0: Connected, 1: Invited, 2: Failed, 3: Available, 4: Unavailable
     */
    private fun Int._toConnectionStatus(): PeerStatus {
        return when (this) {
            0 -> PeerStatus.Connected
            1 -> PeerStatus.Invited
            2 -> PeerStatus.Failed
            3 -> PeerStatus.Available
            4 -> PeerStatus.Unavailable
            else -> PeerStatus.Unavailable
        }
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@WifiDirectBroadcastReceiver::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}


