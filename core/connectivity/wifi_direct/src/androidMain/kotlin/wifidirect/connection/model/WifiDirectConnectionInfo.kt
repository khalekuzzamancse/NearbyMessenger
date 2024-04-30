package wifidirect.connection.model

data class WifiDirectConnectionInfo(
    val groupOwnerIP: String?=null,
    val isGroupOwner: Boolean=false,
    val isConnected: Boolean=false,
    val groupOwnerName:String?=null
)