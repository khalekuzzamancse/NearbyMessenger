package socket.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

/**
 * * It object creation and the start message should be created in Background thread so use the observable proper's
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 *
 */
class Server {
//    private val _port: Int = generateRandomPort()
    /**
     * Right now running on fixed port,so that client can hardcoded this value,
     * but we should not use fixed port.Refactor it later
     */
    private val _port: Int = 54321

    //it is possible that server is not stared for some reason or start after a delay,that is why
    //expose the port as as observable
    private val _portObservable = MutableStateFlow<Int?>(null)

    /**
     * if [port] is null that means server is failed to start or stop for some reason or closed
     */
    val port = _portObservable.asStateFlow()
    private val serverSocket = ServerSocket(_port)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            port.collect {
                log(
                    methodName = "startServer",
                    message = "server observable port:$it"
                )
            }

        }

    }

    //This [serverSocket.inetAddress.hostAddress] is not related to the connection ip or the device ip
    //this can be empty or null even though the server is started
    //that is why,we should not expose it,
    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning = _isServerRunning.asStateFlow()

    companion object {
        var connectedParticipants = mutableSetOf<ConnectedParticipant>()
            private set
    }

    init {
        log("init", "serverPort:$_port")
        log("init", "serverIp:${serverSocket.inetAddress}")
    }

    fun start() {
        try {
            while (!serverSocket.isClosed) {
                _isServerRunning.update { true }
                _portObservable.update { _port }
                log("server running at:${serverSocket.inetAddress.hostAddress}")
                val connectedClientSocket = serverSocket.accept()
                //Hande each client to a separate thread
                //Should we use separate thread or coroutine to handle multiple client?
                //
                log("new client is connected:${connectedClientSocket.connectedClientInfo()}")
                val connectedParticipant = ConnectedParticipant(connectedClientSocket)
                connectedParticipants.add(connectedParticipant)
                Thread(connectedParticipant).start()
                //Blocking call like Scan function in C,use separate thread to prevent the caller to wait infinite amount of time
            }
        } catch (e: Exception) {
            _isServerRunning.update { false }
            _portObservable.update { null }

            log("Failed to start server because of exception:${e}")
        }

    }

    private fun closeServer() {
        try {
            serverSocket.close()
        } catch (e: Exception) {
            log("Exception to close server:${e.message}")
        } finally {
            _isServerRunning.update { false }
        }
    }

    private fun Socket.connectedClientInfo() = "(ip:${inetAddress.hostAddress},ip:$port)"

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@Server::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }


    private fun generateRandomPort() = Random.nextInt(10000, 65535)


}
