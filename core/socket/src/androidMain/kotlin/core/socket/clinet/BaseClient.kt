package core.socket.clinet;

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
class BaseClient(
    private val serverAddress: String,
    private val serverPort: Int,
) {
    private val _serverSocket = MutableStateFlow<Socket?>(null)
    val connectedServer = _serverSocket.asStateFlow()

    companion object {
        private const val TAG = "ClientClass"
    }

    init {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            connect()
        }

    }

     fun isNotConnected(): Boolean {
        _serverSocket.value ?: return true
        return false
    }

     fun isConnected() = !isNotConnected()
     suspend fun connect() {
        if (isConnected()) return
        while (true) {
            try {
                val socket = withContext(Dispatchers.IO) {
                    val serverSocket = Socket()
                    serverSocket.connect(InetSocketAddress(serverAddress, serverPort))
                    serverSocket
                }
                _serverSocket.update { socket }
                if (socket.isConnected) {
                    Log.d(TAG, "Connected Successfully")
                    return
                } else {
                    withContext(Dispatchers.IO) {
                        socket.close()
                    }
                    return
                }
            } catch (ex: IOException) {
                // Delay before the next retry
                Log.d(TAG, "Connection Failed:Retrying")
                delay(1000)
            }
        }

    }

     suspend fun closeConnection() {
        _serverSocket.value?.let { socket ->
            withContext(Dispatchers.IO) {
                socket.getOutputStream().close()
            }
        }
        _serverSocket.update { null }
    }


}