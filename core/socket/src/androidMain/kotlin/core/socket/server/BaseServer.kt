package core.socket.server

import core.socket.networking.server.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket


class BaseServer : Server {
    //A server can have more than one connected client,
    private val _connectedClients = MutableStateFlow<List<Socket>>(emptyList())
    override val connectedClients: Flow<List<Socket>> = _connectedClients.asStateFlow()

    //try to use port given by OS,the fixed port may not be empty in that case it will fail to connect
    override val port = 45555
    private val server = ServerSocket(port)
    override val address: String? = server.inetAddress.hostAddress

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                runForever()
            }
        }
    }

    suspend fun runForever() {
        withContext(Dispatchers.IO) {
            try {
                val clientSocket = server.accept()
                clientSocket?.let { socket ->
                    _connectedClients.update { clients -> clients + socket }
                  //  Log.d("${this::class.simpleName}Log", "Connected to client")
                }
                delay(1000)
                //Log.d("${this::class.simpleName}Log", "Server Running")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}



