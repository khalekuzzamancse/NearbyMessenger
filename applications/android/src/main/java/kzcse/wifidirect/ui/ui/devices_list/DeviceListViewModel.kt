package kzcse.wifidirect.ui.ui.devices_list

import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wifidirect.Factory
import wifidirect.connection.Device

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class DeviceListViewModel : ViewModel() {
    private val wifiManager = Factory.broadcastNConnectionHandler
    private val _wifiEnabled = MutableStateFlow(false)
    val wifiEnabled = _wifiEnabled.asStateFlow()
    fun onWifiStatusChangeRequest() {
        _wifiEnabled.update { !it }
    }

    private val _isDeviceScanning = MutableStateFlow(true)
    val isDeviceScanning = _isDeviceScanning.asStateFlow()

    val nearbyDevices: StateFlow<List<Device>> = wifiManager.nearByDevices

    init {
        viewModelScope.launch {
            nearbyDevices.collect {
                val deviceFound = it.isEmpty()
                _isDeviceScanning.value = deviceFound
            }
        }
    }

    init {
        viewModelScope.launch {
            while (true) {
                val hasNotFound = nearbyDevices.value.isEmpty()
                if (hasNotFound)
                    scanDevices()
                else
                    break
                delay(2_000)
            }
        }
    }

    fun scanDevices() = wifiManager.scanDevice()
    fun connectTo(device: WifiP2pDevice) = wifiManager.connectTo(device)
    fun connectTo(address:String) = wifiManager.connectTo(address)
    fun disconnectAll(device: WifiP2pDevice) = wifiManager.disconnectAll()
    fun disconnectAll() = wifiManager.disconnectAll()

}