package peers.domain.model

data class ConnectionInfoModel(
    val groupOwnerIP: String?,
    val isGroupOwner: Boolean,
    val isConnected: Boolean,
    val groupOwnerName:String?
)