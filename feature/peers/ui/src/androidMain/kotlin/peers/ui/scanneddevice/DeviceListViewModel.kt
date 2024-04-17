package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import peers.di.PeersDependencyFactoryAndroid
/**
 * * Using the Delegation Design pattern
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DeviceListViewModel : ViewModel() {
    private val controller=PeersDependencyFactoryAndroid.createConnectionController()
    val message=controller.message
    val isNetworkOn = controller.isNetworkOn
    fun onNetworkStatusChangeRequest()=controller.onStatusChangeRequest()
    val isDeviceScanning =controller.isDeviceScanning
    val nearbyDevices=controller.nearbyDevices
    fun scanDevices() = controller.scanDevices()
    fun connectTo(address:String) = controller.connectTo(address)
    fun disconnectAll() = controller.disconnectAll()
}