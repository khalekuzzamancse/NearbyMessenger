package wifi_direct2.model

/**
 * - This represent the Info of the group,if the group is formed
 * - Make this instance NULL if the group is not formed
 * - If the group if formed and this is not the group owner,that means this device is connected to the group,
 * because without connecting the Connection info or the group info is not available
 */
data class ConnectionInfo(
    val isThisDeviceGroupOwner:Boolean,
    val groupOwnerIp:String,
)