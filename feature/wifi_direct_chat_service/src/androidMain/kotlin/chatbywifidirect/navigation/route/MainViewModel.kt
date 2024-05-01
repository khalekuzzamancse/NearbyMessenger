package chatbywifidirect.navigation.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatbywifidirect.chat.DataCommunicatorImpl
import chatui.viewmodel.ChatViewModel
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.misc.DevicesConnectionInfo
import platform_contract.WifiDirectControllerAndroid

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class MainViewModel(
    private val thisDeviceUserName: String
) : ViewModel() {
    val deviceListViewModel = chatbywifidirect.devices.DeviceListViewModel(WifiDirectControllerAndroid())
    private val dataCommunicator = DataCommunicatorImpl(thisDeviceUserName)

    fun createChatViewModel()=ChatViewModel(
        dataCommunicator = dataCommunicator,
        thisDeviceName =thisDeviceUserName
    )
    val newMessage = dataCommunicator.newMessage

    fun onGroupFormed(info: DevicesConnectionInfo) = dataCommunicator.onGroupFormed(info)

}
