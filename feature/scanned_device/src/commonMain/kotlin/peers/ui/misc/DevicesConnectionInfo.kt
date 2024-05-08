package peers.ui.misc

/**
 * * to decouple from transient  or nested module or grand parent module
 * @param groupOwnerIP null if the device itself the group owner,server or the hotspot owner
 */
data class DevicesConnectionInfo(
    val groupOwnerIP: String?,
    val isConnected: Boolean,
){
    val isGroupOwner: Boolean
        get() = groupOwnerIP == null

}