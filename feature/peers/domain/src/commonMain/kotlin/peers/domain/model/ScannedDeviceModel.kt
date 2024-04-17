package peers.domain.model

/**
 * * Used to decouple the data and ui layer module
 * * data and ui module should not direcly used it to own purpose,to reduce coupling
 * * It can represent any device such as Wifi or Bluetooth device
 */
data class ScannedDeviceModel(
    val name: String,
    val isConnected: Boolean = false,
    val address: String
)
