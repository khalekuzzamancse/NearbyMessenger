package peers.domain.misc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import peers.domain.model.ScannedDeviceModel

interface ConnectionController {
    val message:Flow<String?>
    val isNetworkOn: Flow<Boolean>
    val connectionInfo:Flow<ConnectionInfo>
    fun onStatusChangeRequest()
    val isDeviceScanning: Flow<Boolean>
    val nearbyDevices: Flow<List<ScannedDeviceModel>>
    fun scanDevices()
    fun connectTo(address: String)
    fun disconnectAll()
}