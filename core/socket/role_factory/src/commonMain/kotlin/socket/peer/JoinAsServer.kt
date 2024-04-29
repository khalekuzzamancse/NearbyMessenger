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
    deviceName: String,
    onNewMessageReceived: (ServerMessage)->Unit,
) {

    private var server: Server = Server()
    private val joinAsClient = JoinAsClient(
        /*
        We are following the mediator design pattern,where the server is the mediator,
        Since this device is server,at a time it a participants so we should allow this device to listen and send message,
        so that that is why we maintain a separate client for this device along with the server.
        Since this the server and so the client within this  device can use with the sever using ip="localhost"
         */

        groupOwnerIP = "localhost",
        deviceName = deviceName,
        onNewMessageReceived = onNewMessageReceived
    )

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