package socket.server

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

/**
 * * It object creation and the start message should be created in Background thread so use the observable proper's
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 * * Uses :
 * ```
 *     val app = Server()
 *     app.start() //must run in background thread to avoid NetworkExceptionInMainThread, in case of Android
 *
 * ```
 */

class Server {
    /**
     * Right now running on fixed port,so that client can hardcoded this value,but we should not use fixed port.Refactor it later
     */
    private val _port: Int = 54321

    /**
     * - it is possible that server is not stared for some reason or start after a delay,that is why,expose the port as as observable
     * - if [port] is null that means server is failed to start or stop for some reason or closed
     */
    private val _portObservable = MutableStateFlow<Int?>(null)

    @Suppress("unused", "MemberVisibilityCanBePrivate")
    val port = _portObservable.asStateFlow()
    private val serverSocket = ServerSocket(_port)

    //This [serverSocket.inetAddress.hostAddress] is not related to the connection ip or the device ip
    //this can be empty or null even though the server is started
    //that is why,we should not expose it,
    private val _isServerRunning = MutableStateFlow(false)

    @Suppress("unused", "MemberVisibilityCanBePrivate")
    val isServerRunning = _isServerRunning.asStateFlow()

    companion object {
       internal var participantHandlers = mutableSetOf<ParticipantHandler>()
            private set
    }


    fun start() {
        try {
            while (!serverSocket.isClosed) {
                updateStates(isRunning = true, port = _port)
                logRunningServerInfo()
                acceptNHandleConnectedClient()
            }
        } catch (e: Exception) {
            updateStates(isRunning = false, port = null)
            log("Failed to start server because of exception:${e}")
        }

    }

    private fun updateStates(isRunning: Boolean, port: Int?) {
        _isServerRunning.update { isRunning }
        _portObservable.update { port }
    }

    /**
     *  - Blocking call like Scan function in C,use separate thread to prevent the caller to wait infinite amount of time
     *  - each client to a separate thread
     *  - Should we use separate thread or coroutine to handle multiple client?
     */
    private fun acceptNHandleConnectedClient() {
        val connectedClientSocket = serverSocket.accept()
        log("new client is connected:${connectedClientSocket.connectedClientInfo()}")
        val participantHandler = ParticipantHandler(connectedClientSocket)
        participantHandlers.add(participantHandler)
        Thread(participantHandler).start() //handle to a new thread
    }

    @Suppress("unused")
    private fun closeServer() {
        try {
            serverSocket.close()
        } catch (e: Exception) {
            log("Exception to close server:${e.message}")
        } finally {
            _isServerRunning.update { false }
        }
    }

    //TODO:Utilities ----------
    //TODO:Utilities ----------
    //TODO:Utilities ----------

    private fun Socket.connectedClientInfo() = "(ip+port:${this.remoteSocketAddress})"
    @Suppress("unused")
    private fun logRunningServerInfo() {
        val info = "host address:${serverSocket.inetAddress.hostAddress},port:${_portObservable.value},local socket address:${serverSocket.localSocketAddress}"
        log("server running:$info")
    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@Server::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

    @Suppress("unused")
    private fun generateRandomPort() = Random.nextInt(10000, 65535)


}
