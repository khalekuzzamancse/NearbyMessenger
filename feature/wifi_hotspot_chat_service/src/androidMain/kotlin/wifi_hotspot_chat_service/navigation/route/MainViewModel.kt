package wifi_hotspot_chat_service.navigation.route

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import chatui.viewmodel.SendAbleMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    fun onNavigateRequest() {
        _showJoinAsDialog.value = false
    }

    fun createChatViewModel() = ChatViewModel(
        dataCommunicator = dataCommunicator,
        thisDeviceName = thisDeviceUserName
    )

    val newMessage = dataCommunicator.newMessage

    fun onConnected(info: DevicesConnectionInfo) {
        dataCommunicator.onConnected(info)
        /* TODO:Refactor Required
        In case of Hotspot,the hotspot owner(server),will not able to sent message until the client
        the client is connected to the server,because the server does not know the socket of the client,
        as a result,if user try to send the message from the server device,the message will not received to the
        client device.
        to solve the bug,right now after joining,if this device is a client then send a dummy message to the
        server,so the user can better experience
         */
        val isThisDeviceHotspotConsumer = info.groupOwnerIP != null
        if (isThisDeviceHotspotConsumer) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                dataCommunicator.sendMessage(
                    SendAbleMessage(
                        message = "$thisDeviceUserName has enter the chat !",
                        receiverName = null, timestamp = System.currentTimeMillis(),
                    )
                )
            }

        }

    }

}
