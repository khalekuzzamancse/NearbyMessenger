package navigation

import chatui.viewmodel.DataCommunicator
import chatui.viewmodel.ReceivedMessage
import chatui.viewmodel.SendAbleMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import peers.ui.misc.DevicesConnectionInfo
import socket.peer.Communicator
import socket.peer.ServerMessage

class DataCommunicatorImpl(
    private val thisDeviceName:String
) : DataCommunicator {
    //
    private val _newMessage = MutableStateFlow<ServerMessage?>(null)
    override val newMessage: Flow<ReceivedMessage?> = _newMessage.asStateFlow().map { msg ->
        if (msg != null) ReceivedMessage(
            senderName = msg.senderName,
            message = msg.message,
            timestamp = msg.timestamp
        ) else null
    }


    private val onNewMessageReceived: (ServerMessage) -> Unit = { msg ->
        log("NewMessage:${msg.message}")
        _newMessage.update { msg }
    }

    private var communicator: Communicator? = null
    fun onGroupFormed(info: DevicesConnectionInfo) {
        try {
            if (info.isConnected) {
                val ownerName = info.groupOwnerName ?: "GroupUserWithNULLName"
                communicator = Communicator(
                    groupOwnerIP = info.groupOwnerIP,
                    isGroupOwner = info.isGroupOwner,
                    deviceName = if (info.isGroupOwner) ownerName else "client1",
                    //TODO : for more than two device,must use a separate name for each client,
                    onNewMessageReceived = onNewMessageReceived
                )
            }
            //TODO:FIX why causes Exception on back button presses from conversation screen,for the client app or android 10
        } catch (_: Exception) {

        }

    }

    override suspend fun sendMessage(msg: SendAbleMessage): Result<Unit> {
        return withContext(Dispatchers.Default) {
            return@withContext communicator?.sendToServer(
                ServerMessage(
                    senderName = thisDeviceName,
                    receiverName =msg.receiverName,
                    message = msg.message,
                    timestamp = msg.timestamp,
                )
            )
                ?: Result.failure(Throwable("DataCommunicator is NULL"))
        }
    }


    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DataCommunicatorImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
}