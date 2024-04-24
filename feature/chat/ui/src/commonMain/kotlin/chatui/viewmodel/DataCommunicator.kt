package chatui.viewmodel

import chatui.viewmodel.ReceivedMessage
import chatui.viewmodel.SendAbleMessage
import kotlinx.coroutines.flow.Flow

interface DataCommunicator {
    val newMessage: Flow<ReceivedMessage?>
    suspend fun sendMessage(msg: SendAbleMessage): Result<Unit>
}