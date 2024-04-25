package socket.peer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import socket.server.Server


/**
 * * Do not create the instance is the UI thread
 *
 */

internal class JoinAsServer(
    groupOwnerIP: String,
    deviceName: String,
    onNewMessageReceived: (ServerMessage)->Unit,
) {

    private var server: Server = Server()
    private val joinAsClient = JoinAsClient(groupOwnerIP, deviceName,onNewMessageReceived)

    suspend fun sendToServer(msg: ServerMessage): Result<Unit> {
        return joinAsClient.sendToServer(msg)
    }

    init {
        startServer()
    }

    private fun startServer() {
        CoroutineScope(Dispatchers.Default).launch {
            server.start()
        }
    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@JoinAsServer::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }


}