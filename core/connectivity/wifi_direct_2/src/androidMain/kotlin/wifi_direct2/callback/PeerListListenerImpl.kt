package wifi_direct2.callback

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager

/**
 *  code that fetches and processes the list of peers.the WifiP2pManager.PeerListListener interface,
 *  which provides information about the peers that Wi-Fi Direct has detected. This information also allows your app to determine
 *  when peers join or leave the network.
 *  - Uses
 *  - Broadcast receiver's onReceive() method to call requestPeers() when an intent with the action WIFI_P2P_PEERS_CHANGED_ACTION
 *  is received.
 *  - You need to pass this listener into the receiver somehow.One way is to send it as an argument to the broadcast receiver's constructor.
 */
class PeerListListenerImpl(
    private val onPeerListChanged: (List<WifiP2pDevice>) -> Unit,
) : WifiP2pManager.PeerListListener {
    /**
     * - Peer list will be updated automatically when a peer  leaves the network.
     * - When a peer join then the list may not be updated automatically until rescan again.
     */
    override fun onPeersAvailable(peerList: WifiP2pDeviceList?) {
        val peers = peerList?.deviceList?.toList() ?: emptyList()
        onPeerListChanged(peers)
        return
    }
}