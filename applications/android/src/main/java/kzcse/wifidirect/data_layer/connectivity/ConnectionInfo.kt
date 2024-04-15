package kzcse.wifidirect.data_layer.connectivity

import android.net.wifi.p2p.WifiP2pInfo

import java.net.InetAddress
enum class ConnectionType {
    Client, Server, NotConnected
}

data class ConnectionInfo(
    val type: ConnectionType = ConnectionType.NotConnected,
    val groupOwnerAddress: InetAddress? = null,
    val isConnected: Boolean = false
) {
    companion object {
        private const val TAG = "ConnectionInfoLog: "

    }
    /*
  Tracking the connected devices' status enables other parts of the app to make decisions or take actions accordingly.
  Storing information about the connection type, whether the device functions as a server or client.

   */

    fun updateInfo(info: WifiP2pInfo): ConnectionInfo {
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

    override fun toString(): String {
        return "ConnectionInfo(\n" +
                "type=$type,\n" +
                "groupOwnerAddress=$groupOwnerAddress,\n" +
                "isConnected=$isConnected" +
                ")"
    }


}