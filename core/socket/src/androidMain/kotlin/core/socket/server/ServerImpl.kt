package core.socket.server

import android.util.Log
import core.socket.DataCommunicator
import core.socket.Peer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

/**
 * * It create a server
 * * Update Server PORT
 * * Notify if data received
 */
class ServerImpl(
    private val onConnected: (client: Socket) -> Unit = {},
) : Peer {
    companion object {
        private const val TAG = "ServerLog: "
        const val SERVER_PORT = 45555
    }

    private val server = ServerSocket(SERVER_PORT)
    private var connectedClientSocket: Socket? = null
    private var dataCommunicator: DataCommunicator? = null
    private val _lastMessage = MutableStateFlow<String?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                runServer()
            }
        }
    }

    private suspend fun runServer() = withContext(Dispatchers.IO) {
        try {
            connectedClientSocket = server.accept()
            connectedClientSocket?.let { socket ->
                onConnected(socket)
//                dataCommunicator = DataCommunicator(socket)
                Log.d(TAG, "Connected to client")
                listenPackets()
            }
            delay(1000)
            Log.d(TAG, "Server Running")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun sendData(data: ByteArray) {
        dataCommunicator?.sendData(data)
    }

    override suspend fun stopSend() {
        TODO("Not yet implemented")
    }

    private suspend fun listenPackets() {
        connectedClientSocket?.let { socket ->
            val inSteam = socket.getInputStream()
        }
    }

    override fun readReceivedData(): StateFlow<String?> {
        // Log.d(TAG, "receivedData()")
        return _lastMessage.asStateFlow()
    }


}

