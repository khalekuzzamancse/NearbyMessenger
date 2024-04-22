package core.socket.networking.server

import core.socket.networking.PeerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

/**
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 */
class BasicServer(
    port: Int = generateFiveDigitNumber(),
) : Server {
    override val participants: StateFlow<List<PeerInfo>> = TODO()
    //A server can have more than one connected client,
    private val _connectedClients = MutableStateFlow<List<Socket>>(emptyList())
    override val clients = _connectedClients.asStateFlow()

    override fun getClientsSocket() = _connectedClients.value.toList()
    //try to use port given by OS,the fixed port may not be empty in that case it will fail to connect

    private val server = ServerSocket(port)
    override val info: PeerInfo = PeerInfo(server.inetAddress.hostAddress,port)
    init {
        CoroutineScope(Dispatchers.Default).launch {
            runForever()
        }
    }

    override suspend fun runForever() {
        while (true) {
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

    override  fun isServerCreated() :Boolean{
       return server.inetAddress!=null
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }
}

fun generateFiveDigitNumber(): Int {
    return Random.nextInt(10000, 65535)
}



