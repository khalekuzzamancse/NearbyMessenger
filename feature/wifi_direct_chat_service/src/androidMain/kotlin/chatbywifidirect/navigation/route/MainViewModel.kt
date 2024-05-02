package chatbywifidirect.navigation.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatbywifidirect.chat.DataCommunicatorImpl
import chatui.viewmodel.ChatViewModel
import peers.ui.misc.DevicesConnectionInfo

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class MainViewModel(
    private val thisDeviceUserName: String
) : ViewModel() {
    val deviceListViewModel = chatbywifidirect.devices.DeviceListViewModel()
    private val dataCommunicator = DataCommunicatorImpl(thisDeviceUserName)

    fun createChatViewModel()=ChatViewModel(
        dataCommunicator = dataCommunicator,
        thisDeviceName =thisDeviceUserName
    )
    val newMessage = dataCommunicator.newMessage

    fun onGroupFormed(info: DevicesConnectionInfo) = dataCommunicator.onGroupFormed(info)

}
