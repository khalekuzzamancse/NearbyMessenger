package peers.domain.misc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import peers.domain.model.ConnectionInfoModel
import peers.domain.model.ScannedDeviceModel
import peers.domain.model.ThisDeviceInfoModel

interface ConnectionController {
    val message:Flow<String?>
    val isNetworkOn: Flow<Boolean>
    val connectionInfoModel:Flow<ConnectionInfoModel>
    fun getThisDeviceInfo():ThisDeviceInfoModel?
    fun onStatusChangeRequest()
    val isDeviceScanning: Flow<Boolean>
    val nearbyDevices: Flow<List<ScannedDeviceModel>>
    fun scanDevices()
    fun connectTo(address: String)
    fun disconnectAll()
}