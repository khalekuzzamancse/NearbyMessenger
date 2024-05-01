package peers.ui.devices

/**
 * @param id can be IP address in case of wifi and wifi direct,EndPoint address
 * in case of NearByDevice API,gateway ip in case of wifi hotspot
 */
data class NearByDevice(
    val name: String,
    val connectionStatus:ConnectionStatus,
    val id: String
)
