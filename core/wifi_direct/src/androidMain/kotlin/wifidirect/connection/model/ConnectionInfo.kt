package wifidirect.connection.model

import java.net.InetAddress
data class ConnectionInfo(
    val type: ConnectionType = ConnectionType.NotConnected,
    val groupOwnerAddress: InetAddress? = null,
    val isConnected: Boolean = false
)