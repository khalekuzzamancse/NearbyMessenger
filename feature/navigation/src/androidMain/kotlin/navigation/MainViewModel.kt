package navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.DevicesConnectionInfo

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainViewModel(
) : ViewModel() {
    val deviceListViewModel = DeviceListViewModel()
    private val dataCommunicator = DataCommunicatorImpl(deviceListViewModel::getThisDeviceInfo)

    fun createChatViewModel(deviceID: String) = ChatViewModel(
        dataCommunicator = dataCommunicator,
        deviceId = deviceID
    )

    fun onGroupFormed(info: DevicesConnectionInfo) = dataCommunicator.onGroupFormed(info)

}
