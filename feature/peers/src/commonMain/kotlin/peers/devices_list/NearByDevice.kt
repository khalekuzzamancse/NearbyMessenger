package peers.devices_list

data class NearByDevice(
    val name: String,
    val isConnected: Boolean = false,
    val ip: String
)
