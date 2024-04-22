package core.socket.networking.clinet;

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Important:
 * * In order to connect server must run first,otherwise can be connected,
but if we want that server may start later then we should try connecting after the connection failed
for certain amount of or till get connected.

this technique will work insha-allah.
 * * after however while trying each time create a new socket as well as a new instance of [DataCommunicator]
 * * for each new socket and also check if we need to create new instance for [CommunicationManager] class;
because this class hold the reference of client and server so this may need to updated.
 * * Client will connected at most one server at a time
 */
class BasicClient(
    private val serverAddress: String, private val serverPort: Int,
) : Client {
    private val _serverSocket = MutableStateFlow<Socket?>(null)
    override val connectedServer = _serverSocket.asStateFlow()

    override fun getConnectedServerSocket() = _serverSocket.value
    override fun isNotConnected(): Boolean {
        _serverSocket.value ?: return true
        return false
    }

    override fun isConnected() = !isNotConnected()
    override suspend fun connect(timeoutSeconds: Int): Result<Unit> {
        if (isConnected()) return Result.success(Unit)
        return attemptConnectionWithTimeout(timeoutSeconds)
    }

    private suspend fun attemptConnectionWithTimeout(timeoutSeconds: Int): Result<Unit> =
        withTimeoutOrNull(timeoutSeconds * 1000L) {
            try {
                val socket = createSocket()
                if (socket.isConnected) {
                    handleSuccessfulConnection(socket)
                } else {
                    handleFailedConnection(socket)
                }
            } catch (ex: IOException) {
                println("${this::class.simpleName}Log ,Connection Failed: ${ex.message}")
                Result.failure(ex)
            }
        }
            ?: Result.failure(IOException("Connection attempt timed out, make sure that the server is running or not busy"))

    private suspend fun createSocket(): Socket = withContext(Dispatchers.IO) {
        Socket().apply {
            connect(InetSocketAddress(serverAddress, serverPort))
        }
    }

    private fun handleSuccessfulConnection(socket: Socket): Result<Unit> {
        _serverSocket.update { socket }
        println("${this::class.simpleName}Log ,Connected Successfully")
        return Result.success(Unit)
    }

    private suspend fun handleFailedConnection(socket: Socket): Result<Unit> =
        withContext(Dispatchers.IO) {
            socket.close()
            Result.failure(IOException("Connection closed immediately after opening."))
        }

    override suspend fun closeConnection() {
        _serverSocket.value?.let { socket ->
            withContext(Dispatchers.IO) {
                socket.getOutputStream().close()
            }
        }
        _serverSocket.update { null }
    }
}