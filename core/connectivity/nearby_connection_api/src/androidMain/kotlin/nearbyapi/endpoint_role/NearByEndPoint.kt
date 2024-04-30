package nearbyapi.endpoint_role

import kotlinx.coroutines.flow.StateFlow
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointInfo

interface NearByEndPoint {
    val receivedMessage:StateFlow<Message?>
    suspend fun scan():Result<Unit>
    val endPoints: StateFlow<Set<EndPointInfo>>
    suspend fun initiateConnection(endpointId:String):Result<Unit>
    suspend fun sendMessage(msg: Message): Result<Unit>
}