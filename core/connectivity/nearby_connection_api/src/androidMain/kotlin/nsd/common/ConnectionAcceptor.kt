package nsd.common

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.PayloadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import nsd.common.conformer.AuthToken
import nsd.common.conformer.ConnectionConformer
import nsd.common.endpoint.EndPointStatus
import nsd.common.endpoint.EndpointList
import kotlin.coroutines.resume

internal class ConnectionAcceptor(
    private val context: Context,
    private val connectionsClient: ConnectionsClient,
    private val advertiserList: EndpointList,
    private val payloadCallback: PayloadCallback,
) {
    suspend fun acceptWithConfirmation(endpointId: String, info: ConnectionInfo) {
        //Start updating to denoted try to connecting
        advertiserList.updateStatus(endpointId, EndPointStatus.Connecting)
        val token = AuthToken(endpointId, info.endpointName, info.authenticationDigits)
        val confirmationResult = ConnectionConformer().showDialog(context,token)
        log("Confirmation result:$confirmationResult")
        val isConfirmed = confirmationResult.isSuccess
        if (isConfirmed) {
            val acceptationResult = acceptConnection(endpointId, payloadCallback)
            log("acceptResult:$acceptationResult")
            /* Just accept confirmation is accepted,the accept was success or not will de notified via
            ConnectionLifeCycleListener.onConnectionResult() ,So dot not updated the  EndPointStatus.Connected here */
        } else {
            //Connected was not successful
            advertiserList.updateStatus(endpointId, EndPointStatus.Discovered)
        }

    }

    /**
     * - Accept and initiate connection
     * - You have to make sure to accept both adviser and discover side
     * @param payloadCallback for data communication
     */
    private suspend fun acceptConnection(endpointId: String, payloadCallback: PayloadCallback): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            connectionsClient
                .acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
        }

    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionAcceptor::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}