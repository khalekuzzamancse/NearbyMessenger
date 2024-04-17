package peers.data

import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import peers.domain.misc.ConnectionController
import peers.domain.model.ScannedDeviceModel
import wifidirect.Factory
import wifidirect.connection.Device

@androidx.annotation.RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiDirectController() : ConnectionController {
    private val wifiManager = Factory.broadcastNConnectionHandler
    private val _wifiEnabled = MutableStateFlow(false)

    //
    private val _message = MutableStateFlow<String?>(null)
    override val message: Flow<String?> = _message.asStateFlow()
    override val isNetworkOn: Flow<Boolean> =
        combine(wifiManager.isWifiEnabled, _wifiEnabled) { a, b ->
            a || b
        }
    private val _isDeviceScanning = MutableStateFlow(true)
    override val isDeviceScanning: Flow<Boolean> = _isDeviceScanning.asStateFlow()
    override val nearbyDevices =
        wifiManager.nearByDevices.map { devices -> devices.map { it.toScannedDevice() } }

    override fun onStatusChangeRequest() {
        _wifiEnabled.update { it }
    }

    override fun scanDevices() {
        CoroutineScope(Dispatchers.Default).launch {
            _isDeviceScanning.update { true }
            delay(2_000)
            wifiManager.scanDevice()
            _isDeviceScanning.update { false }


        }
    }

    override fun connectTo(address: String) = wifiManager.connectTo(address)
    override fun disconnectAll() = wifiManager.disconnectAll()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            observeScanning()
        }
    }

    private suspend fun observeScanning() {
        nearbyDevices.collect {
            val deviceNotFound = it.isEmpty()
            if (deviceNotFound) {
                updateMessage("No Device Found,ReScan again..")
            }
        }
    }

    private suspend fun updateMessage(msg: String) {
        _message.update { msg }
        delay(1_000)
        //   _message.update { null }
    }

    private fun Device.toScannedDevice() =
        ScannedDeviceModel(
            name = this.name,
            address = this.device.deviceAddress,
            isConnected = this.isConnected
        )
}