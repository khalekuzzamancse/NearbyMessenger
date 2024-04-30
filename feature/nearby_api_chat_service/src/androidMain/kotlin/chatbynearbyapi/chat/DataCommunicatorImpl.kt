package chatbynearbyapi.chat

import chatbynearbyapi.devices.DeviceListViewModel
import chatui.viewmodel.DataCommunicator
import chatui.viewmodel.ReceivedMessage
import chatui.viewmodel.SendAbleMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nearbyapi.component.common.Message

class DataCommunicatorImpl(
    private val thisDeviceName:String,
    private val deviceListViewModel: DeviceListViewModel,
) : DataCommunicator {

    override val newMessage: Flow<ReceivedMessage?> =deviceListViewModel.receivedMessage.map { msg ->
        if (msg != null) ReceivedMessage(
            senderName = msg.senderName,
            message = msg.body,
            timestamp = msg.timestamp
        ) else null
    }

    override suspend fun sendMessage(msg: SendAbleMessage): Result<Unit> {
        return deviceListViewModel.sendMessage(msg.toMessage())
    }
    private fun SendAbleMessage.toMessage()=Message(
        senderName = thisDeviceName,
        sendId = null,//send all connected user
        timestamp=System.currentTimeMillis(),
        body = message
    )


    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DataCommunicatorImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
}