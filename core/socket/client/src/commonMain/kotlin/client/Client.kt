package client

import client.data_communication.TextMessage
import client.data_communication.TextMessageDecoder
import client.data_communication.TextMessageEncoder
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Must run the server and the client as separate process.
 * not within the same process.otherwise they will not able to work
 * properly
 */
class Client(
    private val userName: String,
    serverPort: Int,
) {
    private val socket = Socket("localhost", serverPort)
    private val outputStream = DataOutputStream(socket.getOutputStream())
    private val inputStream = DataInputStream(socket.getInputStream())


    fun sendMessage(message: TextMessage) {
        try {
            outputStream.writeUTF(TextMessageEncoder(message).encode())
            outputStream.flush()
        } catch (e: Exception) {
            closeResources()
        }
    }

    init {
        log(
            "init",
            "client is created:$userName"
        )
    }

    fun listenForMessage() {
        Thread {
            while (socket.isConnected) {
                try {
                    val message = inputStream.readUTF()
                    log(methodName = "listenForMessage", message = "${TextMessageDecoder(message).decode()}")
                } catch (e: Exception) {
                    log(methodName = "listenForMessage", message = "Exception:${e.message}:")
                    closeResources()
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

    private fun log(methodName: String, message: String) =
        println("${this@Client::class.simpleName} Logging , $methodName()'s :: $message")
}