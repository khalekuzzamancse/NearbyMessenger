package blueetooth

/**
 * Used as separator to decouple the device information from underlying framework
 * @param address is the MAC address of the device,will required for connect
 */
data class NearByBlueetoothDevice(
    val name: String,
    val address: String
)
