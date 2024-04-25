package chatui.viewmodel

import chatui.message.ChatMessage
import chatui.message.MessageFieldController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel(
    private val dataCommunicator: DataCommunicator
) {
    private val _conversations = MutableStateFlow(emptyList<ChatMessage>())
    internal val conversations = _conversations.asStateFlow()
    internal val controller = MessageFieldController()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            dataCommunicator.newMessage.collect { msg ->
                if (msg != null) {
                    // log("NewMessage:$msg")
                    addMessage(
                        ChatMessage(
                            message = msg.message,
                            timestamp = convertTimeToString(msg.timestamp),
                            isSender = false
                        )
                    )
                }
            }
        }
    }

    suspend fun sendMessage() {
        val msg = controller.message.value
        val result = dataCommunicator.sendMessage(
            SendAbleMessage(message = msg)
        )
        if (result.isSuccess) {
            addMessage(
                ChatMessage(
                    message = msg,
                    timestamp = convertTimeToString(System.currentTimeMillis()),
                    isSender = true
                )
            )
            controller.clearInputField()
        }
    }

    private fun addMessage(chatMessage: ChatMessage) {
        _conversations.update {
            it + chatMessage
        }
    }

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