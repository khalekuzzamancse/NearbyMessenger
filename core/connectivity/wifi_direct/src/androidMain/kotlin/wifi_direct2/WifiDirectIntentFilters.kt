package wifi_direct2

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
/** Through out the whole application we need to the manage it, but all screen may not observe it such as all the screens may
 *  not want to listen the broadcast.
 * @param onStateChangeAction Executed when Wi-Fi P2P is enabled or disabled
 * @param onPeersChangeAction Indicates that the available peer list(Nearby device list) has changed.Executed as a feedback of discoverPeers().call requestPeers() to get an updated list of peers
 * @param onConnectionChangeAction Executed  when the state of the device's Wi-Fi Direct connection changes such as Wi-Fi ON/OFF
 * @param onThisDeviceChangeAction Executed when a device's details have changed, such as the device's name
 */
@SuppressLint("MissingPermission")
object WifiDirectIntentFilters {
    val filters = IntentFilter().apply {
        // Indicates a change in the Wi-Fi Direct status such as ON/OFF,etc
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers such device list
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi Direct connectivity has changed such as ..
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed such has this device name,..
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        //to know the device is correctly discovering or not
        addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)

    }



}
