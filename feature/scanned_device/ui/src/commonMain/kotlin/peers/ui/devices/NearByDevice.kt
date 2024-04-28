package peers.ui.devices

data class NearByDevice(
    val name: String,
    val isConnected: Boolean = false,
    val deviceAddress: String
)
