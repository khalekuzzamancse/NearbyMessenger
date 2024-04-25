package socket.peer


/**
 * * Do not create the instance is the UI thread
 * -  Handle :
 * -  Send message
 * - Listen for Receiver message
 * @param groupOwnerIP is NULL if this is just a client
 *
 */

class Communicator(
    private val groupOwnerIP: String,
    deviceName: String,
    isGroupOwner: Boolean,
    onNewMessageReceived: (ServerMessage)->Unit,
) {
    private val joinAsServer = isGroupOwner
    private val joinedAsOnlyClient =!joinAsServer
    private lateinit var joinAsClient: JoinAsClient
    private var server = if (joinAsServer) JoinAsServer(groupOwnerIP, deviceName,onNewMessageReceived) else null

    init {
        //if it not maintain the server,then it will create an client instance only to communicate with server
        if (joinedAsOnlyClient) {
            joinAsClient = JoinAsClient(groupOwnerIP, deviceName,onNewMessageReceived)
        } else {
            //join as server
        }
    }


    //Client need to create in background thread that is why it is suspend
    suspend fun sendToServer(msg: ServerMessage): Result<Unit> {
        return if (joinedAsOnlyClient) {
            joinAsClient.sendToServer(msg)
        } else {
            //Join as server
            server?.sendToServer(msg) ?: Result.failure(Throwable("JoinAsServer is NULL"))
        }
    }

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@Communicator::class.simpleName}Log"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }


}