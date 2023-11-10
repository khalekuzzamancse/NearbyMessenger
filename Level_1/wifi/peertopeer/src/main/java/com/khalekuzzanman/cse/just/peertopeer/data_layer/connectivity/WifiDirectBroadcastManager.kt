package com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
/*
Through out the whole application we need to the manage it,
but all application may not observe it such as all the screens may not want to listen the boroadcast,
so do not make it directly singleton but you can less reference of it
 */

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiDirectBroadcastManager(
    private val context: Context,
    onStateChangeAction: () -> Unit={},
    onPeersChangeAction: () -> Unit={},
    onConnectionChangeAction: () -> Unit={},
    onThisDeviceChangeAction: () -> Unit={}
) {
    private val tag = "PeerToPeerApp: -> "
    private var actionReceiver: BroadcastReceiver? = null
        private set

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }
    private val broadcastActions = listOf(
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION,
            handler = onStateChangeAction
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION,
            handler = onPeersChangeAction
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION,
            handler = onConnectionChangeAction
        ),
        BroadcastReceiverAction(
            action = WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION,
            handler = onThisDeviceChangeAction
        )
    )

    init {
        actionReceiver = WifiDirectBroadcastReceiver(broadcastActions)
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
