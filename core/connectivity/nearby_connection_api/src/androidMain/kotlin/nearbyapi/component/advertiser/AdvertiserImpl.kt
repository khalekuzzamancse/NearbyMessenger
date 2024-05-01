package nearbyapi.component.advertiser

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import nearbyapi.component.common.ConnectionAcceptor
import nearbyapi.component.common.ConnectionLifeCycleCallbackImpl
import nearbyapi.component.common.Message
import nearbyapi.component.common.data_communicator.DataCommunicatorImpl
import nearbyapi.component.common.endpoint.EndpointList
import kotlin.coroutines.resume

/**
 * - Prevent client module to create instance,to provide loose coupling via factory method for instance creation
 * @param name the [Advertiser]  name
 *
 */
class AdvertiserImpl internal constructor(
    private val context: Context,
    private val name: String,
) : Advertiser {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val dataCommunicator= DataCommunicatorImpl(connectionsClient)
    private val payloadCallback=dataCommunicator.payloadCallback

    override val receivedMessage=dataCommunicator.receivedMessage


    /**
     * - This value is required for creating a new Android service.
     * - It should be globally unique, which is why we use the app's namespace name.
     * - For both advertisers and discovered devices within this app, this value must be the same.
     */
    @Suppress("PrivatePropertyName")
    private val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"
    private val advertiserList = EndpointList()
    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    override val discoverers = advertiserList.endpoints




    /** - The return value indicates that the device advertise process has completed.
     * - Based on this information,UI or relevant vent information can be updated
     **/
    override suspend fun startAdvertising(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val option = createOptions()
            createConnectionClient()
                .startAdvertising(name, SERVICE_ID, connectionLifecycleCallback, option)
                .addOnSuccessListener {
                    log("startAdvertising:success")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    log("startAdvertising:failed with :$e")
                    continuation.resume(Result.failure(e))
                }
        }
    }


    /**
     *  - Indicate  the life cycle of a connection such as new connection has been initiated,
     *  a existing connection request(initiation) has been accepted,rejected or failed with  Exception
     *  - Executed in response to  [ConnectionsClient.requestConnection]
     *  */
    private val connectionLifecycleCallback = ConnectionLifeCycleCallbackImpl(
        advertiserList = advertiserList,
        _onConnectionInitiated = { endpointId, info ->
            //confirm for accept
            CoroutineScope(Dispatchers.Default).launch {
                acceptWithConfirmation(endpointId, info)
            }
        }
    )


    private suspend fun acceptWithConfirmation(endpointId: String, info: ConnectionInfo) {
        val acceptor = ConnectionAcceptor(context = context, connectionsClient = createConnectionClient(), advertiserList = advertiserList, payloadCallback = payloadCallback)
        acceptor.acceptWithConfirmation(endpointId, info)

    }


    /**- Create a new instance  of  [AdvertisingOptions]  using [Strategy.P2P_STAR] topology/strategy */
    private fun createOptions() = AdvertisingOptions
        .Builder()
        .setStrategy(Strategy.P2P_STAR)
        .build()



    /**
     * - Create a new instance  of  [ConnectionsClient]
     * - [ConnectionsClient] can be used to start advertising,initiate ,accept or reject a connection
     */
    private fun createConnectionClient(): ConnectionsClient {
//        return  Nearby.getConnectionsClient(context)
        return connectionsClient
    }



    override suspend fun sendMessage(msg: Message)=dataCommunicator.sendMessage(msg)


    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods

    override fun getDiscoverer() = discoverers.value

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@AdvertiserImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}