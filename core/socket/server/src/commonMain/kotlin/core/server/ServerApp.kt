package core.server

import client.data_communication.TextMessageDecoder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.random.Random

fun main() {
    val app = ServerApp()
    app.start()

}

/**
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 */
class ServerApp {
    val port: Int = generateRandomPort()
    private val serverSocket = ServerSocket(port)
    val ip: String = serverSocket.inetAddress.hostAddress
    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning = _isServerRunning.asStateFlow()

    companion object {
        var participants = mutableSetOf<Participant>()
            private set
    }

    init {
        log("init", "serverPort:$port")
    }

    fun start() {
        try {
            while (!serverSocket.isClosed) {
                _isServerRunning.update { true }
                val connectedClientSocket = serverSocket.accept()
                //Hande each client to a separate thread
                //Should we use separate thread or coroutine to handle multiple client?
                //
                log(
                    methodName = "startServer",
                    message = "new client is connected:${connectedClientSocket.connectedClientInfo()}"
                )
                val participant= Participant(connectedClientSocket)
                participants.add(participant)
                val thread = Thread(participant).start()
                //Blocking call like Scan function in C,use separate thread to prevent the caller to wait infinite amount of time
            }
        } catch (e: Exception) {
            _isServerRunning.update { false }
            log(
                methodName = "startServer",
                message = "Failed to start server"
            )
        }

    }

    private fun closeServer() {
        try {
            serverSocket.close()
        } catch (e: Exception) {
            log(
                methodName = "closeServer",
                message = "Exception to close server:${e.message}"
            )
        } finally {
            _isServerRunning.update { false }
        }
    }

    private fun Socket.connectedClientInfo() = "(ip:${inetAddress.hostAddress},ip:$port)"

    private fun log(methodName: String, message: String) =
        println("${this@ServerApp::class.simpleName} Logging , $methodName()'s :: $message")

    private fun generateRandomPort() = Random.nextInt(10000, 65535)


}

/**
 * Using separate thread instead of separate coroutine so that make sure always running in a background
 * * Using separate thread so that as as new message is received we can retrieve before the client closes
 * the stream
 * * using threads will works nearly as it is a separate process,but the coroutine may not close to a process
 * that is why using separate thread
 * *TODO(Find out should we use coroutine or threads)
 */
/*
It will handle each client separate,
if new client join then via this class ,we can communicate to this client such as send and receive message
actually this a sever class to handle the client
 */
class Participant(private val socket: Socket) : Runnable {
    private val participants = ServerApp.participants
    private val username = "${socket.inetAddress.hostAddress}-${socket.port}"
    private val outputStream = DataOutputStream(socket.getOutputStream())
    private val inputStream = DataInputStream(socket.getInputStream())

    init {

        log("init", "$username has enter the chat...")
    }

    private fun log(methodName: String, message: String) =
        println("${this@Participant::class.simpleName} Logging , $methodName()'s :: $message")


    val clientInfo = "(${socket.inetAddress.hostAddress},${socket.port})"
    private fun refreshParticipants() {
        //TODO(Filter whose OutputStream is closed or socket is closed)
    }

    /**
     * This method will be run in a separate thread,so here listen for new message here
     *
     */
    override fun run() {
        while (socket.isConnected) {
            try {
                val message = inputStream.readUTF()
                log(
                    methodName = "run",
                    message = "message to $username msg:${TextMessageDecoder(message).decode()}"
                )
                broadcastMessage(message)
            } catch (e: Exception) {
                closeResources()
                break
            }
        }
    }

    private fun closeResources() {
        try {
            inputStream.close()
            outputStream.close()
            socket.close()
        } catch (_: Exception) {
        }

    }


    private fun broadcastMessage(message: String) {
        //Except the client itself,other will receive this message
        participants.filter { it.username!=username}.forEach { participant ->
            try {
                val os = DataOutputStream(participant.outputStream)
                os.writeUTF(message)
                os.flush()
            } catch (e: Exception) {
                log(
                    "broadcastMessage",
                    "failed to broadcast;${e.message}"
                )
                closeResources()
            }
        }
    }

}

