package wifi_hotspot_chat_service.navigation.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import peers.ui.misc.DevicesConnectionInfo
import wifi_hotspot_chat_service.chat.DataCommunicatorImpl
import wifi_hotspot_chat_service.devices.DeviceListViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class MainViewModel(
    private val thisDeviceUserName: String
) : ViewModel() {
    val deviceListViewModel = DeviceListViewModel()
    private val dataCommunicator = DataCommunicatorImpl(thisDeviceUserName)

    private val _showJoinAsDialog = MutableStateFlow(true)
    val showJoinAsDialog = _showJoinAsDialog.asStateFlow()
    fun onNavigateRequest(){
        _showJoinAsDialog.value = false
    }

    fun createChatViewModel()=ChatViewModel(
        dataCommunicator = dataCommunicator,
        thisDeviceName =thisDeviceUserName
    )
    val newMessage = dataCommunicator.newMessage

    fun onGroupFormed(info: DevicesConnectionInfo){
        println("MainViewModel:$info")
        dataCommunicator.onGroupFormed(info)
    }

}
