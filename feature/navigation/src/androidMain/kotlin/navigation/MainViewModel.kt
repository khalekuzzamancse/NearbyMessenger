package navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.DevicesConnectionInfo

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainViewModel(
    private val thisDeviceUserName: String
) : ViewModel() {
    val deviceListViewModel = DeviceListViewModel()
    private val dataCommunicator = DataCommunicatorImpl(thisDeviceUserName)

    fun createChatViewModel()=ChatViewModel(
        dataCommunicator = dataCommunicator,
        thisDeviceUserName =thisDeviceUserName
    )
    val newMessage = dataCommunicator.newMessage

    fun onGroupFormed(info: DevicesConnectionInfo) = dataCommunicator.onGroupFormed(info)

}
