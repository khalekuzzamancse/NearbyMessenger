package chatui.viewmodel

import chat.di.DependencyFactory
import chat.domain.model.TextMessageModel
import chat.domain.model.TextMessageModelRole
import chatui.message.ChatMessage
import chatui.message.MessageFieldController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @param thisDeviceUserName is other device that is connected with the device and whose conversation list we are going to open
 */
class ChatViewModel(
    private val dataCommunicator: DataCommunicator,
    private val thisDeviceUserName: String
) {

    private val repository = DependencyFactory.getChatRepository()
    internal val controller = MessageFieldController()
//    private val _conversation = MutableStateFlow<List<ChatMessage>>(emptyList())
//    val conversations = _conversation.asStateFlow()


    internal val conversations: Flow<List<ChatMessage>> =
        repository.observerGroupConversation().map { messages ->
            messages.map {
                ChatMessage(
                    senderName=if (it.participantsName=="ME")null else it.participantsName ,
                    message = it.message,
                    timestamp=it.timeStamp.convertTimeToString()
                )
            }.distinct()//TODO :DB has bug,insert multiple time,so fix it later ,then remove the distance()
        }

    //    init {
//        CoroutineScope(Dispatchers.Default).launch{
//            repository.observerConversation(deviceId).collect { messages ->
//                log(messages.toString())
//            }
//        }
//
//    }
    init {
        observerIncomingMessage()
    }

    private fun observerIncomingMessage() {
        CoroutineScope(Dispatchers.Default).launch {
            dataCommunicator.newMessage.collect { msg ->
                if (msg != null) {
                    log("NewMessage:$msg")
//                    addToConversation(
//                        senderName = msg.senderName,
//                        message = msg.message,
//                        timeStamp = msg.timestamp,
//                    )
                 val res= repository.addToDatabase(msg.toModel())
                    log("$res")
                }
            }
        }
    }

    suspend fun sendMessage() {
        val message = createMessageFromInputBoxText()
        val result = dataCommunicator.sendMessage(message)
        if (result.isSuccess) {
//            addToConversation(
//                message = message.message,
//                timeStamp = message.timestamp,
//                senderName = null,//send from this device that is why null
//            )
            repository.addToDatabase(message.toModel())
            controller.clearInputField()
        }
    }

    private fun createMessageFromInputBoxText() = SendAbleMessage(
        message = controller.message.value,
        receiverName = null,//group message
        timestamp = System.currentTimeMillis()
    )

    @Suppress("Unused")
    private fun ReceivedMessage.toModel() = TextMessageModel(
        participantsName = this.senderName,
        message = this.message,
        timeStamp = this.timestamp,
        deviceRole = TextMessageModelRole.Receiver
    )

    @Suppress("Unused")
    private fun SendAbleMessage.toModel() = TextMessageModel(
        participantsName = this.receiverName?:"ME",
        message = this.message,
        timeStamp = this.timestamp,
        deviceRole = TextMessageModelRole.Sender
    )

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ChatViewModel::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

//    private fun addToConversation(
//        senderName: String?, message: String, timeStamp: Long,
//    ) {
//        _conversation.update {
//            it + ChatMessage(
//                senderName = senderName,
//                message = message,
//                timestamp = timeStamp.convertTimeToString(),
//            )
//        }
//    }

    private fun Long.convertTimeToString(): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(this))
    }

}