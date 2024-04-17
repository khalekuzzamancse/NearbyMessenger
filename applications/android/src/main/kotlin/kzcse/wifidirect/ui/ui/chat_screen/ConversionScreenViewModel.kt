package kzcse.wifidirect.ui.ui.chat_screen

import android.content.ContentResolver
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import chatui.ChatMessage
import chatui.MessageFieldController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kzcse.wifidirect.data_layer.socket_programming.SocketManager
import wifidirect.Factory
import java.text.SimpleDateFormat
import java.util.Date

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ConversionScreenViewModel(
    val resolver: ContentResolver
) {
    companion object {
        private const val TAG = "ConversionScreenViewModelClass: "

    }


    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    val messageInputFieldState = MessageFieldController()

    private val wifiManager = Factory.broadcastNConnectionHandler

    private var socketManager: SocketManager? = null




   suspend fun onConnectionRequest() {
        val info = wifiManager.connectionInfo.value
        socketManager = SocketManager(
            connectionInfo = info,
            resolver = resolver
        )
            socketManager?.listenReceived()?.collect { data ->
                if (data != null) {
                    Log.d(TAG, "Received: $data")
                    _messages.value = messages.value + ChatMessage(
                        message = data,
                        isSender = false,
                        timestamp = getCurrentTimeAsString()
                    )
                }
            }
        Log.d(TAG, "$info")

    }

    suspend fun sendBytes(byteArray: ByteArray) {
        socketManager?.sendData(byteArray)

    }

    suspend fun stopSend() {
        socketManager?.stopSend()

    }


    fun onSendRequest() {
        val scope = CoroutineScope(Dispatchers.IO)
        Log.d(TAG, "onSendRequest(): CommManor=$socketManager")
        val message = messageInputFieldState.message.value
        scope.launch {
            socketManager?.sendData(message.toByteArray())
        }

        _messages.value = messages.value + ChatMessage(
            message = message,
            isSender = true,
            timestamp = getCurrentTimeAsString()
        )
        messageInputFieldState.clear()
    }

    private fun getCurrentTimeAsString(): String {
        val formatter = SimpleDateFormat("hh:mm:ss a")
        return formatter.format(Date())
    }


}