package nsd.common

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient

abstract class NearByPeer(
    private val context: Context,
) {
    @Suppress("PropertyName")
    val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"

    fun createConnectionClient(): ConnectionsClient = Nearby.getConnectionsClient(context)

    abstract fun onConnectedToEndPoint(endpointId: String)
    abstract fun onTextReceived(text: String)

    //- Will notify about incoming connection request,connection success,fail or ...
    protected val connectionLifecycleCallback = ConnectionLifeCycleCallbackImpl(
        context = context,
        onAcceptConfirm = { endpointId ->
//            createConnectionClient()
//                .acceptConnection(endpointId, payloadCallback)
        },
        onConnected = ::onConnectedToEndPoint,
        onDisconnectedFrom = {

        }
    )
    private val payloadCallback = PayloadCallbackImpl(
        onTextReceived = ::onTextReceived
    )

    @Suppress("Unused")
    fun log(message: String, methodName: String? = null) {
        val tag = "${this::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }

}