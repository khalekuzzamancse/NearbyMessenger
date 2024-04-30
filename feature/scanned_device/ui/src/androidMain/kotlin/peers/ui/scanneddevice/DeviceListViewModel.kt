package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import peers.di.PeersDependencyFactoryAndroid

/**
 * * Using the Delegation Design pattern
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DeviceListViewModel : ViewModel() {
    private val controller = PeersDependencyFactoryAndroid.createConnectionController()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val message = _errorMessage.asStateFlow()
    private val _isScanning = MutableStateFlow(false)
    val isDeviceScanning = _isScanning

    val connectionInfo = controller.connectionInfoModel
    val isNetworkOn = controller.isNetworkOn


    fun onNetworkStatusChangeRequest() = controller.onStatusChangeRequest()

    val nearbyDevices = controller.nearbyDevices

    fun scanDevices() {
        CoroutineScope(Dispatchers.Default).launch {
            _isScanning.update { true }
            repeat(8) {
                controller.scanDevices()
                delay(250)
            }
            //TODO: There is Bug ,that is why Scanning multiple times,to avoid multiple
            _isScanning.update { false }
            updateErrorMessageAboutScannedDevice()

        }

    }

    fun connectTo(address: String) = controller.connectTo(address)
    fun disconnectAll() = controller.disconnectAll()

    //TODO:Utils----------------------    TODO:Utils----------------------------TODO:Utils
    //TODO:Utils----------------------    TODO:Utils----------------------------TODO:Utils

    private fun updateErrorMessageAboutScannedDevice() {
        CoroutineScope(Dispatchers.Default).launch {
            val scannedDevice = controller.getNearByDevices()
            if (scannedDevice.isEmpty())
                _errorMessage.update { "No Device found,Rescan both your and participants device.Make device are sure connected via same wifi" }
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