package platform_contract

import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wifidirect.WifiDirectFactory
import wifidirect.connection.model.Device
import wifidirect.misc.ConnectionController
import wifidirect.model.ConnectionInfoModel
import wifidirect.model.ScannedDeviceModel
import wifidirect.model.ThisDeviceInfoModel

@androidx.annotation.RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WifiDirectControllerAndroid : ConnectionController {

    private val wifiManager = WifiDirectFactory.broadcastNConnectionHandler
    private val _wifiEnabled = MutableStateFlow(false)


    override val isNetworkOn: Flow<Boolean> =
        combine(wifiManager.isWifiEnabled, _wifiEnabled) { a, b ->
            a || b
        }

    override val nearbyDevices = wifiManager.nearByDevices.map { devices ->
            devices.map { it.toScannedDevice() }
        }

    override fun getNearByDevices()=wifiManager.nearByDevices.value.map { it.toScannedDevice() }

    override val connectionInfoModel: Flow<ConnectionInfoModel> = wifiManager.wifiDirectConnectionInfo.map { info ->
        ConnectionInfoModel(
            groupOwnerIP = info.groupOwnerIP,
            isGroupOwner = info.isGroupOwner,
            isConnected = info.isConnected,
            groupOwnerName = info.groupOwnerName
        )
    }

    override fun getThisDeviceInfo(): ThisDeviceInfoModel? {
      val info=wifiManager.getThisDeviceInfo()
      return  if (info!=null) ThisDeviceInfoModel(info.name,info.address) else null
    }



    override fun onStatusChangeRequest() {
        _wifiEnabled.update { it }
    }

    override fun scanDevices() {
        CoroutineScope(Dispatchers.Default).launch {
            wifiManager.scanDevice()
        }
    }


    override fun connectTo(address: String) = wifiManager.connectTo(address)
    override fun disconnectAll() = wifiManager.disconnectAll()


    private fun Device.toScannedDevice() =
        ScannedDeviceModel(
            name = this.name,
            address = this.device.deviceAddress,
            isConnected = this.isConnected
        )
}