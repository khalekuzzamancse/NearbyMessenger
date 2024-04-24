package kzcse.wifidirect.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import peers.ui.scanneddevice.DevicesConnectionInfo
import socket.peer.Communicator
import socket.peer.ServerMessage

class DataCommunicator {
    //
    private val _newMessage=MutableStateFlow<ServerMessage?>(null)
    val newMessage=_newMessage.asStateFlow()
    private val onNewMessageReceived: (ServerMessage)->Unit={msg->
        log("NewMessage:${msg.message}")
        _newMessage.update { msg }
    }
    private var communicator: Communicator? =  null
    fun onGroupFormed(info: DevicesConnectionInfo){
        if (info.isConnected) {
            CoroutineScope(Dispatchers.Default).launch {
                val ownerName = info.groupOwnerName?:"GroupUserWithNULLName"
                communicator = Communicator(
                    groupOwnerIP = info.groupOwnerIP,
                    isGroupOwner = info.isGroupOwner,
                    deviceName = if (info.isGroupOwner) ownerName else "client1",
                    //TODO : for more than two device,must use a separate name for each client,
                    onNewMessageReceived = onNewMessageReceived
                )
            }

        }
    }

    suspend fun sendMessageToServer(msg: ServerMessage):Result<Unit>{
       return withContext(Dispatchers.Default) {
           return@withContext communicator?.sendToServer(msg)?:Result.failure(Throwable("DataCommunicator is NULL"))
        }
    }
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DataCommunicator::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
}