package core.socket.networking.server

import core.socket.networking.TextMessage
import core.socket.networking.clinet.BasicClient
import core.socket.networking.clinet.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    // MutableStateFlow to store and expose messages as they are received
    private val clientsOfServer = server.clients//manage single source of truth
    private val _connectedClientsToServer = MutableStateFlow(emptySet<Socket>())

    //ip address,server itself is participants
    //  val participants = _participants.asStateFlow()
    val participants = server.participants
    private val _messages = MutableStateFlow<List<TextMessage>>(emptyList())

    val messages = _messages.asStateFlow()


    /**
     * * Should not return the client,instead return info
     */

    suspend fun createNConnectAsClient(): Client? {
        val serverAddress = server.info.address
        return if (serverAddress != null) {
            val client = BasicClient(server.info.address!!, server.info.port)
            val result = client.connect(timeoutSeconds = 5)
            return if (result.isSuccess) {
                val socket = client.getConnectedServerSocket()
                if (socket != null) {
                    _connectedClientsToServer.update { it + socket }
                }
                client
            } else null
        } else null

    }

    init {
      listenForServerIncomingMessage()
    }
    private fun listenForServerIncomingMessage(){
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                server.clients.value.forEach { socket ->
                    //socket form the sever,not from the client list
                    //  val socket = server.clients.value.first()
                    val message = readReceivedData(socket)
                    if (message != null) {
                        _messages.update { msgs -> msgs + message }
                            println("${this@ServerAppMediator::class.simpleName}::Observing():$message")
                    }

                }
                delay(1_000)
            }

        }
    }

    /**
     * Client will send message to the server,so the client have to server socket
     * * to send the message we need the server socket
     * the message sent to the server,then server will send or broadcast...it
     *
     */
    suspend fun sendMessageToServer(message: TextMessage): Result<Unit> {
        val client = createNConnectAsClient()
        return if (client != null)
            sendMessageToServer(client.getConnectedServerSocket(), message)
        else
            Result.failure(Throwable("Client have NULL Socket reference of server"))
    }

    suspend fun sendMessageToServer2(message: TextMessage): Result<Unit> {
        val participants =
            server.clients.value.find { it.inetAddress.hostAddress == message.receiverAddress && it.port == message.receiverPort }
        return if (participants != null) {
            //return sendMessageToServer(socket, message) //TODO(Find out why not work)
            sendMessageToServer(message)
        } else{
            Result.failure(
                makeReceiverNotFoundException(message.receiverAddress, message.receiverPort)
            )
        }

    }

    private fun makeReceiverNotFoundException(
        receiverAddress: String, receiverPort: Int,
    ): Throwable {
        return Throwable(
            "Receiver not found\n" +
                    "Request for : ($receiverAddress,$receiverPort)\n" +
                    ":Available:${participants.value}"
        )
    }

    private suspend fun sendMessageToServer(
        clientSocket: Socket?,
        message: TextMessage
    ): Result<Unit> {
        return if (clientSocket != null) {
            sendToClient(clientSocket, message)
            Result.success(Unit)
        } else
            Result.failure(Throwable("Client have NULL Socket reference of server"))
    }

    /**
     * * Closing the steam may causes to receiver to not received retrieve the data successfully

     * * Does not pass [Int] as data because [Int] used as separator
     */
    private suspend fun sendToClient(clientSocket: Socket, message: TextMessage) {
        withContext(Dispatchers.IO) {
            val outputStream = clientSocket.getOutputStream()
            val dataOutput = DataOutputStream(outputStream)
            dataOutput.writeUTF(message.senderAddress)
            dataOutput.writeSeparator()
            dataOutput.writeInt(message.senderPort)
            dataOutput.writeSeparator()
            dataOutput.writeUTF(message.receiverAddress)
            dataOutput.writeSeparator()
            dataOutput.writeInt(message.receiverPort)
            dataOutput.writeSeparator()
            dataOutput.writeLong(message.time)
            dataOutput.writeSeparator()
            dataOutput.writeUTF(message.body)
            dataOutput.flush()
            delay(2_000)
            outputStream.close() //if socket is not closed,then reading causes waiting infinite amount of time
        }
    }

    /**
     *     separate between next peaces of data ,use separate data type from the previses and next
     */

    private fun DataOutputStream.writeSeparator() = this.writeBoolean(true)
    private fun DataInputStream.retrieveSeparator() = this.readBoolean()


    /**
     * * Immaterially return by launching a coroutine,so the result may not be appears immediately
     * so Wait for some times for data
     * * Making it as [suspend] function may causes the client to block infinite amount of time since it is reading,
     * if the OutputStream is not closes
     */


    private suspend fun readReceivedData(socket: Socket): TextMessage? {
        var message: TextMessage? = null
        withContext(Dispatchers.IO) {
            try {
                DataInputStream(socket.getInputStream()).apply {
                    val senderAddress = readUTF();retrieveSeparator()
                    val senderPort = readInt();retrieveSeparator()
                    val receiverAddress = readUTF();retrieveSeparator()
                    val receiverPort = readInt();retrieveSeparator()
                    val time = readLong();retrieveSeparator()
                    val body = readUTF()
                    message = TextMessage(
                        senderAddress = senderAddress,
                        senderPort = senderPort,
                        receiverAddress = receiverAddress,
                        receiverPort = receiverPort,
                        body = body,
                        time = time,
                    )
                }
                // println("${this@ServerAppMediator::class.simpleName}::readReceivedData():$message")

            } catch (e: Exception) {
                //   println("${this@ServerAppMediator::class.simpleName}:: ,readReceivedData():Failed :: ${e.message}")
            }
        }
        return message

    }


}