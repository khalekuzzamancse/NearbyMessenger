package core.socket.networking.server

import core.socket.networking.TextMessage
import core.socket.networking.clinet.BasicClient
import core.socket.networking.clinet.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

/**
 * Server Data Reading Mechanism
 *
 * * The server utilizes individual client sockets to read data. Each client socket is created when a client connects to the server via the [ServerSocket].
 * * The [ServerSocket] listens on a specified port for incoming connections and, upon establishing a connection, delegates the communication to a new client socket.
 *
 * Process:
 * 1. [ServerSocket] listens for incoming client connections.
 * 2. Upon a client's successful connection, the [ServerSocket] accepts this connection and creates a dedicated client socket.
 * 3. The server reads incoming data from each client through these dedicated sockets.
 * 4. Data reading is typically handled in a loop, continuously checking for new messages until the socket is closed or the server terminates the connection.
 * 5. Each message is processed as per server logic, and appropriate responses are sent back through the same client socket.
 *
 * Note: The [ServerSocket] itself does not handle the direct data transmission or reception but is essential for establishing connections and creating client sockets.
 */

@Suppress("FunctionName")
class ServerAppMediator(
    private val server: Server = BasicServer2()
) {
    companion object {
        const val INT_SIZE = 4  // Size of an integer in bytes (used for lengths)
        const val LONG_SIZE = 8 // Size of a long in bytes (used for timestamp)
    }

    private val _clients = MutableStateFlow(emptyList<Client>())

    // MutableStateFlow to store and expose messages as they are received
    private val _messages = MutableStateFlow<List<TextMessage>>(emptyList())
    val messages = _messages.asStateFlow()


    private suspend fun createNConnectAsClient(): Client? {
        val serverAddress = server.info.address
        return if (serverAddress != null) {
            val client = BasicClient(server.info.address!!, server.info.port)
            val result = client.connect(timeoutSeconds = 5)
            return if (result.isSuccess) {
                _clients.update { it + client }
                client
            } else null
        } else null

    }

    /**
     * Client will send message to the server,so the client have to server socket
     * * to send the message we need the server socket
     * the message sent to the server,then server will send or broadcast...it
     *
     */
    suspend fun sendMessage(message: TextMessage): Result<Unit> {
        val client = createNConnectAsClient()
        return if (client != null)
            sendMessage(client, message)
        else
            Result.failure(Throwable("Client have NULL Socket reference of server"))

    }

    private suspend fun sendMessage(client: Client, message: TextMessage): Result<Unit> {
        val socket = client.getConnectedServerSocket()
        return if (socket != null) {
            sendToClient(socket, message)
            Result.success(Unit)
        } else
            Result.failure(Throwable("Client have NULL Socket reference of server"))
    }

    /**
     * * Closing the steam may causes to receiver to not received retrieve the data successfully
     */

//    private suspend fun sendToClient(clientSocket: Socket, message: TextMessage) {
//        withContext(Dispatchers.IO) {
//            val outputStream = clientSocket.getOutputStream()
//            outputStream.write(message.message.toByteArray())
//            outputStream.flush()
//            outputStream.close()
//            //if socket is not closed,then reading causes waiting infinite amount of time
//        }
//
//    }
    /**
     * * Does not pass [Int] as data because [Int] used as separator
     */
    private suspend fun sendToClient(clientSocket: Socket, message: TextMessage) {
        withContext(Dispatchers.IO) {
            val outputStream = clientSocket.getOutputStream()
            val dataOutput = DataOutputStream(outputStream)
            dataOutput.writeUTF(message.senderAddress)
            dataOutput.writeSeparator()
            dataOutput.writeUTF(message.receiverAddress)
            dataOutput.writeSeparator()
            dataOutput.writeLong(message.time)
            dataOutput.writeSeparator()
            dataOutput.writeUTF(message.message)
            dataOutput.flush()
            delay(2_000)
            outputStream.close() //if socket is not closed,then reading causes waiting infinite amount of time
        }
    }

    /**
     *     separate between next peaces of data ,use separate data type from the previses and next
     */

    private fun DataOutputStream.writeSeparator() = this.writeInt(0)
    private fun DataInputStream.retrieveSeparator() = this.readInt()


    /**
     * * Immaterially return by launching a coroutine,so the result may not be appears immediately
     * so Wait for some times for data
     * * Making it as [suspend] function may causes the client to block infinite amount of time since it is reading,
     * if the OutputStream is not closes
     */
//    suspend fun readReceivedData(): TextMessage? {
//        val socket = server.clients.value.first()
//        return withContext(Dispatchers.IO) {
//            val input = DataInputStream(socket.getInputStream())
//            try {
//                    val senderAddress = input.readUTF()
//                    val receiverAddress = input.readUTF()
//                    val time = input.readLong()
//                    val messageContent = input.readUTF()
//                    val message=TextMessage(senderAddress, receiverAddress, messageContent, time)
//                    println("${this@ServerAppMediator::class.simpleName}:: ,readReceivedData():$senderAddress")
//                    return@withContext null
//            } catch (e: Exception) {
//                println("${this@ServerAppMediator::class.simpleName}:: ,readReceivedData(): Failed for causes: $e")
//                null
//            }
//        }
//    }

    suspend fun readReceivedData(): TextMessage? {
        var message: TextMessage? = null
        withContext(Dispatchers.IO) {
            try {
                val socket = server.clients.value.first()
                DataInputStream(socket.getInputStream()).apply {
                    val senderAddress = readUTF();retrieveSeparator()
                    val receiverAddress = readUTF();retrieveSeparator()
                    val time = readLong();retrieveSeparator()
                    val body = readUTF()
                    message = TextMessage(
                        senderAddress = senderAddress,
                        receiverAddress = receiverAddress,
                        message = body,
                        time = time
                    )
                }
                println("${this@ServerAppMediator::class.simpleName}::readReceivedData():$message")
//                val receivedBytes = ByteArray(1024)
//                val count = input.read(receivedBytes)
//                val receivedData = String(receivedBytes, 0, count, Charsets.UTF_8)
//                message=receivedData
//                println("${this@ServerAppMediator::class.simpleName}:: ,readReceivedData():$receivedData")

            } catch (e: Exception) {
                println("${this@ServerAppMediator::class.simpleName}:: ,readReceivedData():Failed :: ${e.message}")
            }
        }
        return message

    }


}