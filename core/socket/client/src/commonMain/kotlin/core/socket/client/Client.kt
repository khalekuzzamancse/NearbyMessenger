package core.socket.client

import core.socket.client.data_communication.TextMessage
import core.socket.client.data_communication.TextMessageDecoder
import core.socket.client.data_communication.TextMessageEncoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Must run the server and the client as separate process(at least separate thread,instead of  separate coroutine)
 * not within the same process.otherwise they will not able to work
 * properly
 */
class Client(
    private val userName: String,
    serverIp: String,
    serverPort: Int,
) {
    private val socket = Socket(serverIp, serverPort)
    private val outputStream = DataOutputStream(socket.getOutputStream())
    private val inputStream = DataInputStream(socket.getInputStream())


    fun sendMessage(message: TextMessage): Result<Unit> {
        return try {
            outputStream.writeUTF(TextMessageEncoder(message).encode())
            outputStream.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            log("MessageSendFailed:$e")
            closeResources()
            Result.failure(e)
        }
    }

    init {
        log("created")
    }
    fun listenForMessage(callback:(TextMessage)->Unit) {
        Thread {
            while (socket.isConnected) {
                try {
                    val message = inputStream.readUTF()
                    val decodedMessage=TextMessageDecoder(message).decode()
                    log("New MessageReceived :$decodedMessage")
                   callback(decodedMessage)
                } catch (e: Exception) {
                    log(methodName = "listenForMessage", message = "Exception:${e.message}:")
                    closeResources()
                    break//exit this because the client may disconnect or gone
                }
            }
        }.start()
    }

    private fun closeResources() {
        try {
            inputStream.close()
            outputStream.close()
            socket.close()
        } catch (_: Exception) {
        }

    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@Client::class.simpleName}Log:$userName"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

}