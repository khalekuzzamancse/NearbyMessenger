package core.wifi_hotspot.demo_unused

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

internal class ServerClient {
    private val port = 12345
    private lateinit var serverSocket: ServerSocket


    fun startServer() {
        Thread {
            try {
                serverSocket  = ServerSocket(port)

                while (true) {
                    log("Server:Running at ${serverSocket.localSocketAddress}")
                    val connectedClientSocket = serverSocket.accept()
                    val inputStream = DataInputStream(connectedClientSocket.getInputStream())
                    log("Server:new client connected;${inputStream.readUTF()}")
                }
            } catch (e: Exception) {
                log("Failed to start server because of exception:${e}")
            }
        }.start()
    }

    /**
     * @param serverIp is the is Gateway from the connected hotspot details.Go to to details on the connected hotspot from the device setting
     * and you find the Gateway info,as well as IP Info,do not use the IP Info,Use the Gateway Info as IP address to join as client.
     * - Read the readme.md to find the ip of connected hotspot using code,instead of getting manually from setting
     */

    fun startClient(serverIp:String,port:Int) {
        Thread {
            try {
                val socket = Socket(serverIp, port)
                val outputStream = DataOutputStream(socket.getOutputStream())
                DataInputStream(socket.getInputStream())
                outputStream.writeUTF("Hello from client")
                outputStream.flush()
                log("Client:Connected with server and sent message")
            } catch (e: Exception) {
                log("Exception:${e.stackTraceToString()}")
            }
        }.start()
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ServerClient::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        println(tag + msg)
    }

}
