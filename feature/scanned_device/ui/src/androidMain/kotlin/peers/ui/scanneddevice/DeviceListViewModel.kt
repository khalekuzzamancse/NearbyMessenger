package peers.ui.scanneddevice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import peers.di.PeersDependencyFactoryAndroid

/**
 * * Using the Delegation Design pattern
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DeviceListViewModel : ViewModel() {
    private val controller=PeersDependencyFactoryAndroid.createConnectionController()
    val message=controller.message
    val connectionInfo=controller.connectionInfoModel
    val isNetworkOn = controller.isNetworkOn

    fun getThisDeviceInfo():ThisDeviceInfo?{
       val info= controller.getThisDeviceInfo()
        return if (info==null) null
        else
            ThisDeviceInfo(info.name,info.address)
    }




    fun onNetworkStatusChangeRequest()=controller.onStatusChangeRequest()
    val isDeviceScanning =controller.isDeviceScanning
    val nearbyDevices=controller.nearbyDevices
    fun scanDevices() = controller.scanDevices()
    fun connectTo(address:String) = controller.connectTo(address)
    fun disconnectAll() = controller.disconnectAll()


}