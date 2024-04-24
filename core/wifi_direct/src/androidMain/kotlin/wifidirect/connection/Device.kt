package wifidirect.connection

import android.net.wifi.p2p.WifiP2pDevice

/**
 * Used to decouple from outer module
 */
data class Device(
    val name: String,
    val isConnected: Boolean = false,
    val device: WifiP2pDevice
)
