package peers.domain.misc

import kotlinx.coroutines.flow.Flow
import peers.domain.model.ScannedDeviceModel

interface ConnectionController {
    val message:Flow<String?>
    val isNetworkOn: Flow<Boolean>
    fun onStatusChangeRequest()
    val isDeviceScanning: Flow<Boolean>
    val nearbyDevices: Flow<List<ScannedDeviceModel>>
    fun scanDevices()
    fun connectTo(address: String)
    fun disconnectAll()
}