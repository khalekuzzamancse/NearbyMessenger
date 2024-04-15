package kzcse.wifidirect.data_layer.connectivity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
/*
Through out the whole application we need to the manage it,
but all application may not observe it such as all the screens may not want to listen the boroadcast,
so do not make it directly singleton but you can less reference of it

Intents along with description::
    WIFI_P2P_CONNECTION_CHANGED_ACTION
     Broadcast when the state of the device's Wi-Fi connection changes.
   WIFI_P2P_PEERS_CHANGED_ACTION
   	    Broadcast when you call discoverPeers(). You will usually call requestPeers() to get
   	    an updated list of peers if you handle this intent in your application.
    WIFI_P2P_STATE_CHANGED_ACTION
        Broadcast when Wi-Fi P2P is enabled or disabled on the device.
   WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
        Broadcast when a device's details have changed, such as the device's name.
 */

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiDirectBroadcastManager(
    private val context: Context,
    onStateChangeAction: () -> Unit={},
    onPeersChangeAction: () -> Unit={},
    onConnectionChangeAction: () -> Unit={},
    onThisDeviceChangeAction: () -> Unit={},
    onWifiStateChangedAction: (Int) -> Unit={},
) {
    private val tag = "PeerToPeerApp: -> "
    private var actionReceiver: BroadcastReceiver? = null
        private set

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)//to check wifi is enabled or not

    }
    private val broadcastActions = listOf(
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,
            handler = {onStateChangeAction()}
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
                val wifiState = it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                onWifiStateChangedAction(wifiState)
            }
        )


    )

    init {
        actionReceiver = GenericBroadcastReceiver(broadcastActions)
    }

    fun register() {
        actionReceiver?.also { receiver ->
            context.registerReceiver(
                receiver,
                intentFilter,
                ComponentActivity.RECEIVER_NOT_EXPORTED
            )
        }
    }

    fun unregister() {
        actionReceiver?.also { receiver ->
            context.unregisterReceiver(receiver)
        }
    }
}
