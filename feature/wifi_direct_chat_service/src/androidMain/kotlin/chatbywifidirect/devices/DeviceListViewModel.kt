package chatbywifidirect.devices

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.ScannnedDevice
import wifi_direct2.model.PeerStatus

/**
 * * Using the Delegation Design pattern
 */

internal class DeviceListViewModel(

) : ViewModel() {
    private val broadcastReceiver = wifi_direct2.WifiDirectFactory.broadcastReceiver
    private val _errorMessage = MutableStateFlow<String?>(null)
    val message = _errorMessage.asStateFlow()
    val isDeviceScanning = broadcastReceiver.isDiscovering
    val connectionInfo = broadcastReceiver.connectionInfo
    val nearbyDevices = broadcastReceiver.peers.map { peers ->
        peers.map {
            ScannnedDevice(
                name = it.deviceName,
                id = it.deviceAddress,
                connectionStatus = when (it.connectionStatus) {
                    PeerStatus.Connected -> ConnectionStatus.Connected
                    PeerStatus.Invited -> ConnectionStatus.Connecting
                    else -> ConnectionStatus.NotConnected
                }
            )

        }

    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            broadcastReceiver.peers.collect {
                log("perres:$it.toString()")
            }
        }
    }

    fun scanDevices() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = broadcastReceiver.startDiscovery()
            if (result.isFailure) {
                updateErrorMessage("Failed to start scan")
            }
        }

    }

    fun connectTo(address: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val res = broadcastReceiver.initiateConnection(address)
            if (res.isFailure) {
                updateErrorMessage("Connection Initiation failed")
            }
        }
    }

    fun disconnectAll() {
        CoroutineScope(Dispatchers.Default).launch {
            val res = broadcastReceiver.disconnectRequest()
            if (res.isFailure) {
                updateErrorMessage("Failed to disconnect")
            }
        }
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