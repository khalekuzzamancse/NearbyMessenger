package wifidirect.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi


/** Through out the whole application we need to the manage it, but all screen may not observe it such as all the screens may
 *  not want to listen the broadcast.
 * @param onStateChangeAction Executed when Wi-Fi P2P is enabled or disabled
 * @param onPeersChangeAction Indicates that the available peer list(Nearby device list) has changed.Executed as a feedback of discoverPeers().call requestPeers() to get an updated list of peers
 * @param onConnectionChangeAction Executed  when the state of the device's Wi-Fi Direct connection changes such as Wi-Fi ON/OFF
 * @param onThisDeviceChangeAction Executed when a device's details have changed, such as the device's name
 */
@SuppressLint("MissingPermission")
internal class WifiDirectBroadcastReceiver(
    private val context: Context,
    private val onStateChangeAction: () -> Unit = {},
    private val onPeersChangeAction: () -> Unit = {},
    private val onConnectionChangeAction: () -> Unit = {},
    private val onThisDeviceChangeAction: () -> Unit = {},
    private val onWifiStateChangedAction: (Int) -> Unit = {},
) {

    private var actionReceiver: BroadcastReceiver =
        GenericBroadcastReceiver(createBroadcastActions())

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)//to check wifi is enabled or not

    }

    private fun createBroadcastActions() = listOf(
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,
            handler = { onStateChangeAction() }
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION,
            handler = { onPeersChangeAction() }
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION,
            handler = { onConnectionChangeAction() }
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION,
            handler = { onThisDeviceChangeAction() }
        ),
        BroadcastReceiverAction(
            action = WifiManager.WIFI_STATE_CHANGED_ACTION,
            handler = {
                val wifiState =
                    it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                onWifiStateChangedAction(wifiState)
            }
        )

    )


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun register() {
        context.registerReceiver(
            actionReceiver,
            intentFilter,
            ComponentActivity.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregister() {
        context.unregisterReceiver(actionReceiver)
    }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@WifiDirectBroadcastReceiver::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}
