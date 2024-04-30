package nsd.common.data_communicator

import com.google.android.gms.nearby.connection.PayloadCallback
import kotlinx.coroutines.flow.StateFlow
import nsd.common.Message

internal interface DataCommunicator {

    val payloadCallback:PayloadCallback
    val receivedMessage:StateFlow<Message?>
    suspend fun sendMessage(msg: Message): Result<Unit>

}