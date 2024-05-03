package wifi_hotspot_chat_service.devices

import androidx.lifecycle.ViewModel
import core.wifi_hotspot.WiFiHotspotFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.ScannnedDevice

/**
 * * Using the Delegation Design pattern
 */

internal class DeviceListViewModel(

) : ViewModel() {
    private val broadcastReceiver = WiFiHotspotFactory.receiver
    private val _errorMessage = MutableStateFlow<String?>(null)
    val message = _errorMessage.asStateFlow()
    private val _isDeviceScanning = MutableStateFlow(false)
    val isDeviceScanning = _isDeviceScanning.asStateFlow()
    val nearbyDevices = MutableStateFlow(getDevice())



    private fun getDevice(): ScannnedDevice? {
        val gateWay = broadcastReceiver.getHotspotDefaultGateway()
        return if (gateWay != null) {
            ScannnedDevice(
                name = "",//
                id = gateWay,
                connectionStatus = ConnectionStatus.Connected
            )
        } else null

    }
    init {


    }


    fun scanDevices() {

        CoroutineScope(Dispatchers.Default).launch {
            updateErrorMessage("Scan unavailable because of Security reason,use System Setting app instead")
            _isDeviceScanning.update { true }
            delay(1000)
            _isDeviceScanning.update { false }
        }

    }

    fun connectTo(address: String) {
//    CoroutineScope(Dispatchers.Default).launch {
//        val res = broadcastReceiver.initiateConnection(address)
//        if (res.isFailure) {
//            updateErrorMessage("Connection Initiation failed")
//        }
//    }
    }

    fun disconnectAll() {
//    CoroutineScope(Dispatchers.Default).launch {
//        val res = broadcastReceiver.disconnectRequest()
//        if (res.isFailure) {
//            updateErrorMessage("Failed to disconnect")
//        }
//    }
    }

//TODO:Utils----------------------    TODO:Utils----------------------------TODO:Utils
//TODO:Utils----------------------    TODO:Utils----------------------------TODO:Utils

    private fun updateErrorMessage(message: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _errorMessage.update { message }
            delay(3_000)
            _errorMessage.update { null }
        }

    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DeviceListViewModel::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

}