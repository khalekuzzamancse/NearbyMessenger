package socket.peer

import core.socket.client.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import socket.protocol.data_communication.TextMessage

internal class JoinAsClient(
    groupOwnerIP: String,
    deviceName: String,
    private val onNewMessageReceived: (ServerMessage) -> Unit,
) {
    /**
     * Right now running on fixed port,so that client can hardcoded this value,
     * but we should not use fixed port.Refactor it later
     */
    private val serverPort = 54321
    private val serverIP = groupOwnerIP

    /**
     * -  username is not known to the server,it is the client side user name
     * - the server is unaware about it
     */
    private val username = deviceName
    private val latestClientInstance = MutableStateFlow<Client?>(null)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            latestClientInstance.update { createClient().getOrNull() }

        }
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            latestClientInstance.collect { client ->
                client?.listenForMessage {
                    onNewMessageReceived(
                        ServerMessage(
                            message = it.message,
                            timestamp = it.timestamp
                        )
                    )
                }
            }
        }
    }

    //Client need to create in background thread that is why it is suspend
    suspend fun sendToServer(msg: ServerMessage): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val result = createClient()//need to create instance in network thread
            latestClientInstance.update { result.getOrNull() }//update the old clients
            if (result.isSuccess) {
                return@withContext result.getOrThrow().sendMessage(
                    TextMessage(
                        senderName = msg.senderAddress,
                        senderPort = msg.senderAddress,
                        message = msg.message,
                    )
                )
            } else
                return@withContext Result.failure(
                    result.exceptionOrNull() ?: Throwable("Can not create client")
                )
        }
    }

    /**
     * Need to create the instance in background thread
     * - Otherwise failed to create the instances
     */

    private fun createClient(): Result<Client> {
        return try {
            val client = Client(
                userName = username,
                serverIp = serverIP,
                serverPort = serverPort
            )
            Result.success(client)
        } catch (e: Exception) {
            log("Failed to create Client instance exception:$e")
            Result.failure(e)
        }

    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@JoinAsClient::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
}