package nearbyapi.component.common.data_communicator

import com.google.android.gms.nearby.connection.PayloadCallback
import kotlinx.coroutines.flow.StateFlow
import nearbyapi.component.common.Message

internal interface DataCommunicator {

    val payloadCallback:PayloadCallback
    val receivedMessage:StateFlow<Message?>
    suspend fun sendMessage(msg: Message): Result<Unit>

}