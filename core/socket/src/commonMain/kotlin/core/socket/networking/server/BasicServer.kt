package core.socket.networking.server

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

/**
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 */
class BasicServer(
    override val port:Int = 45553
) : Server {
    //A server can have more than one connected client,
    private val _connectedClients = MutableStateFlow<List<Socket>>(emptyList())
    override val connectedClients: Flow<List<Socket>> = _connectedClients.asStateFlow()
    override fun getClients()=_connectedClients.value.toList()
    //try to use port given by OS,the fixed port may not be empty in that case it will fail to connect

    private val server = ServerSocket(port)
    override val address: String? = server.inetAddress.hostAddress

    init {
        CoroutineScope(Dispatchers.Default).launch {
            runForever()
        }
    }

    override suspend fun runForever() {
        while (true){
            withContext(Dispatchers.IO) {
                try {
                    val clientSocket = server.accept()
                    clientSocket?.let { socket ->
                        _connectedClients.update { clients -> clients + socket }
                        println("${this::class.simpleName}Log ,Connected to client")
                    }
                    delay(1000)
                    println("${this::class.simpleName}Log : Server Running")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}



