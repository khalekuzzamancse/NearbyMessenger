package core.socket.networking.server

import core.socket.networking.PeerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 */
class BasicServer2 : Server {
    //A server can have more than one connected client,
    private val  port: Int = generateFiveDigitNumber()
    private val _clients = MutableStateFlow<List<Socket>>(emptyList())
    override val clients = _clients.asStateFlow()
    //try to use port given by OS,the fixed port may not be empty in that case it will fail to connect
    private val server = ServerSocket(port)

    override val info: PeerInfo= PeerInfo(server.inetAddress.hostAddress,port)

    init {
        
        CoroutineScope(Dispatchers.Default).launch {
            runForever()
        }
    }

    override suspend fun runForever() {
        //TODO replace with
        // while(!server.isClosed)
        while (true) {
            withContext(Dispatchers.IO) {
                try {
                    val clientSocket = server.accept()
                    clientSocket?.let { cSocket ->
                        _clients.update { clients -> clients + cSocket }
                        logDeviceInfo(cSocket.inetAddress)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun logDeviceInfo(address: InetAddress) {
        println(
            "${this::class.simpleName},Connected to client: ( ip = ${address.hostAddress},name= ${address.hostName})"
        )
    }

    override fun isServerCreated(): Boolean = server.inetAddress != null
    override fun shutdown() {
        closeAllClients()
        server.close()
    }

    private fun closeAllClients() {
        clients.value.forEach {
            it.close()
        }
        _clients.update { emptyList() }
    }
}
