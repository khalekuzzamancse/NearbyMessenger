package wifi_direct2.model

/**
 * Used to decouple from underlying framework representation of P2P device
 */
data class Peer(
    val deviceName: String,
    val connectionStatus: PeerStatus,
    val deviceAddress:String,
    val isGroupOwner: Boolean
)