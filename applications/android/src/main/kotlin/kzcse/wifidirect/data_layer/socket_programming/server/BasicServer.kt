package kzcse.wifidirect.data_layer.socket_programming.server

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

/**
 * @param onConnected ,it is okay to pass [Socket] to loose couple  ,
 * since it belong to Java that is not platform specific
 */

class BasicServer(
    private val onConnected: (client: Socket) -> Unit = {},
) {
    companion object {
        private const val TAG = "ServerLog: "
        const val SERVER_PORT = 45555
    }

    private val server = ServerSocket(SERVER_PORT)
    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                runServer()
            }
        }
    }

    private suspend fun runServer() = withContext(Dispatchers.IO) {
        try {
            val clientSocket = server.accept()
            clientSocket?.let { socket ->
                onConnected(socket)
                Log.d(TAG, "Connected to client")
            }
            delay(1000)
            Log.d(TAG, "Server Running")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



