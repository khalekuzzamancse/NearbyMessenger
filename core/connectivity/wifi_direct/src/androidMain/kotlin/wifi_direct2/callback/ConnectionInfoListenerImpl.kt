package wifi_direct2.callback

import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import wifi_direct2.model.ConnectionInfo

/**
- Note that the connect() method only notifies you when the initiation succeeds or fails via  WifiP2pManager.ActionListener.
To listen for changes in connection state such as successfully connected or not, implement the WifiP2pManager.ConnectionInfoListener interface.
Its onConnectionInfoAvailable() callback notifies you when the state of the connection changes.
- In cases where multiple devices are going to be connected to a single device (like a game with three or more players, or a chat app),
one device is designated the "group owner". You can designate a particular device as the network's
group owner by following the steps in the Create a Group section. */
class ConnectionInfoListenerImpl(
    private val onConnectionInfoAvailable: (ConnectionInfo) -> Unit
) : WifiP2pManager.ConnectionInfoListener {

    /**
     * Now go back to the onReceive() method of the broadcast receiver, and modify the section that listens for
     * a WIFI_P2P_CONNECTION_CHANGED_ACTION intent. When this intent is received, call requestConnectionInfo().
     * This is an asynchronous call, so results are received by the connection info listener you provide as a parameter.
     */
    override fun onConnectionInfoAvailable(p2pInfo: WifiP2pInfo?) {
        log("onConnectionInfoAvailable() called with: p2pInfo = [$p2pInfo]")
        p2pInfo?.let { info ->
            val groupOwnerAddress = info.groupOwnerAddress?.hostAddress
            // After the group negotiation, we can determine the group owner  that will act as server
            if (info.groupFormed && info.isGroupOwner) {
                //since this the group owner,so local host will work
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a group owner thread and accepting
                // incoming connections.
                if (groupOwnerAddress != null) {
                    onConnectionInfoAvailable(
                        ConnectionInfo(
                            groupOwnerIp = groupOwnerAddress,
                            isThisDeviceGroupOwner = true
                        )
                    )
                }
            } else if (info.groupFormed) {
                //only the group formed,it is not the group owner
                // The other device acts as the peer (client). In this case,
                // you'll want to create a peer thread that connects
                // to the group owner.
                if (groupOwnerAddress != null) {
                    onConnectionInfoAvailable(
                        ConnectionInfo(
                            groupOwnerIp = groupOwnerAddress,
                            isThisDeviceGroupOwner = false
                        )
                    )
                }
            }


        }


    }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionInfoListenerImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}