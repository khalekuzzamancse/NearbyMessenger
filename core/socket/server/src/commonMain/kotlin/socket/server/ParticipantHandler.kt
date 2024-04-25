package socket.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


/*
- It will handle each client separate,
- if new client join then via this class ,we can communicate to this client such as send and receive message
actually this a sever class to handle the client
 */
internal class ParticipantHandler(private val socket: Socket) : Runnable {

    private val participants = Server.participantHandlers

    /** Right now,we have  to detect each device uniquely,a device can join via multiple port over different time.
     * the device ip is the unique.so use it as username
     **/
    private val username = socket.inetAddress.hostAddress
    //ip of the device via the connection.this ip might be different  for same device based on connection type

    //    private val username = "${socket.inetAddress.hostAddress}-${socket.port}"
    private val outputStream = DataOutputStream(socket.getOutputStream())
    private val inputStream = DataInputStream(socket.getInputStream())

//    init {
//        log("$username has enter the chat")
//    }
@Suppress("unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ParticipantHandler::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
    @Suppress("unused")
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
            //    log("message from $username msg:${TextMessageDecoder(message).decode()}")
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
        participants.filter { isNotSender(it.username) }.forEach { participant ->
            try {
                val os = DataOutputStream(participant.outputStream)
                os.writeUTF(message)
                os.flush()
            } catch (e: Exception) {
              //  log("failed to broadcast;${e.message}")
                closeResources()
            }
        }
    }
    private fun isNotSender(username:String)=username!=this.username

}

