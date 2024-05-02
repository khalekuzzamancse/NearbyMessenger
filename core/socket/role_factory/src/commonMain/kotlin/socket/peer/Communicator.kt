package socket.peer


/**
 * * Do not create the instance is the UI thread
 * -  Handle :
 * -  Send message
 * - Listen for Receiver message
 * @param groupOwnerIP is NULL if this device is is the group owner,means the server will run on this device,if this parameter is null
 *
 *
 */

class Communicator(
    private val groupOwnerIP: String?,
    deviceName: String,
    onNewMessageReceived: (ServerMessage)->Unit,
) {
    private val  isGroupOwner=groupOwnerIP==null
    private val joinAsServer = isGroupOwner
    private val joinedAsOnlyClient =!joinAsServer
    private lateinit var joinAsClient: JoinAsClient
    private var server = if (joinAsServer) JoinAsServer( deviceName,onNewMessageReceived) else null

    init {
        //if it not maintain the server,then it will create an client instance only to communicate with server
        if (joinedAsOnlyClient&&groupOwnerIP!=null) {
            joinAsClient = JoinAsClient(groupOwnerIP, deviceName,onNewMessageReceived)
        } else {
            //join as server:Already Done...
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