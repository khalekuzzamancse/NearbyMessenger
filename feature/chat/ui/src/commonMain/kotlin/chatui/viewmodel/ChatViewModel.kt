package chatui.viewmodel

import chat.di.DependencyFactory
import chat.domain.model.TextMessageModel
import chat.domain.model.TextMessageModelRole
import chatui.message.ChatMessage
import chatui.message.MessageFieldController
import core.database.api.TextMessageAPIs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @param deviceId is other device that is connected with the device and whose conversation list we are going to open
 */
class ChatViewModel(
    private val dataCommunicator: DataCommunicator,
    private val deviceId: String
) {

    private val repository = DependencyFactory.getChatRepository()
    internal val controller = MessageFieldController()

    internal val conversations: Flow<List<ChatMessage>> =
        repository.observerConversation(deviceId).map { messages ->
            messages.map {
                ChatMessage(
                    message = it.message,
                    timestamp = convertTimeToString(it.timeStamp),
                    isSender = it.deviceRole == TextMessageModelRole.Sender
                )
            }.distinct()//TODO :DB has bug,insert multiple time,so fix it later ,then remove the distance()
        }

    init {
        CoroutineScope(Dispatchers.Default).launch{
            repository.observerConversation(deviceId).collect { messages ->
                log(messages.toString())
            }
        }

    }
    init {

        observerIncomingMessage()
    }

    private fun observerIncomingMessage() {
        CoroutineScope(Dispatchers.Default).launch {
            dataCommunicator.newMessage.collect { msg ->
                if (msg != null) {
                     log("NewMessage:$msg")
                 val res= repository.addToDatabase(msg.toModel())
                    log("$res")
                }
            }
        }
    }

    suspend fun sendMessage() {
        val message = SendAbleMessage(
            message = controller.message.value,
            receiverName =deviceId,
            receiverAddresses = deviceId,
            timestamp = System.currentTimeMillis()
        )
        val result = dataCommunicator.sendMessage(message)
        if (result.isSuccess) {
            repository.addToDatabase(message.toModel())
            controller.clearInputField()
        }
    }

    private fun ReceivedMessage.toModel() = TextMessageModel(
        participantsAddress = this.senderAddress,
        message = this.message,
        timeStamp = this.timestamp,
        deviceRole = TextMessageModelRole.Receiver
    )

    private fun SendAbleMessage.toModel() = TextMessageModel(
        participantsAddress = this.receiverAddresses,
        message = this.message,
        timeStamp = this.timestamp,
        deviceRole = TextMessageModelRole.Sender
    )

    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ChatViewModel::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

    private fun convertTimeToString(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(timeInMillis))
    }

}