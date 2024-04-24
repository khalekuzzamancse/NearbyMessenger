package socket.server

import socket.server.data_communication.TextMessageDecoder
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

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
class ConnectedParticipant(private val socket: Socket) : Runnable {
    private val participants = Server.connectedParticipants

    /** Right now,we have  to detect each device uniquely,a device can join via multiple port over different time.
     * the device ip is the unique.so use it as username
     **/
    private val username = socket.inetAddress.hostAddress

    //    private val username = "${socket.inetAddress.hostAddress}-${socket.port}"
    private val outputStream = DataOutputStream(socket.getOutputStream())
    private val inputStream = DataInputStream(socket.getInputStream())

    init {
        log("$username has enter the chat")
    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectedParticipant::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

    private fun refreshParticipants() {
        //TODO(Filter whose OutputStream is closed or socket is closed)
    }

    /**
     * This method will be run in a separate thread,so here listen for new message here
     *
     */
    override fun run() {
        observeIncomingMessage()
    }

    private fun observeIncomingMessage() {
        while (socket.isConnected) {
            try {
                val message = inputStream.readUTF()
                log("message from $username msg:${TextMessageDecoder(message).decode()}")
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
        participants.filter { it.username != username }.forEach { participant ->
            try {
                val os = DataOutputStream(participant.outputStream)
                os.writeUTF(message)
                os.flush()
            } catch (e: Exception) {
                log("failed to broadcast;${e.message}")
                closeResources()
            }
        }
    }

}

