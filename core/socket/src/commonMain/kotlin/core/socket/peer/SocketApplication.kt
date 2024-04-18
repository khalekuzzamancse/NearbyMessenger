package core.socket.peer

import core.socket.datacommunication.TextDataCommunicator
import core.socket.datacommunication.TextDataCommunicatorImpl
import core.socket.networking.clinet.BasicClient
import core.socket.networking.server.BasicServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ConnectionStatus {
    RunningAsServer, ConnectedAsClient, None
}

/**
 * * For Client, the port and address are need not to use that is why is nullable
 * * Prevent to create the instance to the client module,so that can maintain single source of instance creation
 * and reduce the complexity of client
 */
class SocketApplication(
    private val deviceRole: DeviceRole,
) {
    private var communicator: TextDataCommunicator? = null
    var address: String? = null
        private set
    var port: Int? = null
        private set
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.None)
    val connectionStatus = _connectionStatus.asStateFlow()
    private val _newMessages=MutableStateFlow<List<String>?>(null)
    val newMessages=_newMessages.asStateFlow()


    init {
        CoroutineScope(Dispatchers.IO).launch {
            when (deviceRole) {
                DeviceRole.Server -> runAsServer()
                is DeviceRole.Client -> runAsClient(deviceRole)
            }
        }
    }
    init {
        CoroutineScope(Dispatchers.IO).launch {
            observerNewMessage()
        }
    }
    private suspend fun observerNewMessage(){
        while (true){
          val result=  retrieveReceivedData()
            if (result.isSuccess){
                val messages=result.getOrNull()
                if (!messages.isNullOrEmpty())
                    _newMessages.update { messages }
            }
            delay(1_000)//check for new message in every 1 sec interval
        }
    }


    private suspend fun runAsServer() {
        val server = BasicServer()
        //server is running for ever
        _connectionStatus.update { ConnectionStatus.RunningAsServer }
        address = server.address
        port = server.port
        server.connectedClients.collect { clients ->
            if (clients.isNotEmpty()) {
                communicator = TextDataCommunicatorImpl(server.getClientsSocket().first())
            }
        }

    }

    private suspend fun runAsClient(deviceRole: DeviceRole.Client) {
        val client = BasicClient(
            serverAddress = deviceRole.serverAddress,
            serverPort = deviceRole.serverPort
        )
        if (client.connect(5_000).isSuccess) {
            //server is running for ever
            _connectionStatus.update { ConnectionStatus.ConnectedAsClient }
            communicator = TextDataCommunicatorImpl(client.getConnectedServerSocket()!!)
        }
    }

    suspend fun sendMessage(message: String): Result<Unit> {
        return communicator?.sendMessage(message)
            ?: Result.failure(Throwable("DataCommunicator is NULL"))
    }

 private   suspend fun retrieveReceivedData(): Result<List<String>> {
        communicator?.let { communicator ->
            return Result.success(communicator.retrieveReceivedData())
        }
        return Result.failure(Throwable("Communicator is null"))

    }
}