package peers.domain.misc

data class ConnectionInfo(
    val groupOwnerIP: String?,
    val isGroupOwner: Boolean,
    val isConnected: Boolean,
    val groupOwnerName:String?
)