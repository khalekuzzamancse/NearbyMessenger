package peers.ui.misc

/**
 * * to decouple from transient  or nested module or grand parent module
 */
data class DevicesConnectionInfo(
    val groupOwnerIP: String,
    val isGroupOwner: Boolean,
    val isConnected: Boolean,
)