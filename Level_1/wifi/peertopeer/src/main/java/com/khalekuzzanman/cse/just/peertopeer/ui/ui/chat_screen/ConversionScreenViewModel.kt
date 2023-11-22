package com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen

import android.content.ContentResolver
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandlerInstance
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ConversionScreenViewModel(
    val resolver: ContentResolver
) {
    companion object {
        private const val TAG = "ConversionScreenViewModelClass: "

    }


    private val _messages = MutableStateFlow<List<ConversationScreenMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    val messageInputFieldState = MessageInputFieldState()

    private val wifiManager = WifiAndBroadcastHandlerInstance.wifiAndBroadcastHandler

    private var socketManager: SocketManager? = null


    fun onConnectionRequest() {
        val info = wifiManager.connectionInfo.value
        socketManager = SocketManager(
            connectionInfo = info,
            resolver = resolver
        )
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            socketManager?.listenReceived()?.collect { data ->
                if (data != null) {
                    Log.d(TAG, "Recived: $data")
                    _messages.value = messages.value + ConversationScreenMessage(
                        message = data,
                        isSender = false,
                        timestamp = getCurrentTimeAsString()
                    )
                }
            }
        }

        Log.d(TAG, "$info")

    }

    fun sendBytes(byteArray: ByteArray) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            socketManager?.sendData(byteArray)
        }
    }
    fun stopSend(){
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            socketManager?.stopSend()
        }
    }


    fun onSendRequest() {
        val scope = CoroutineScope(Dispatchers.IO)


        Log.d(TAG, "onSendRequest(): CommMangr=$socketManager")
        val message = messageInputFieldState.message.value
        scope.launch {
            socketManager?.sendData(message.toByteArray())
        }

        _messages.value = messages.value + ConversationScreenMessage(
            message = message,
            isSender = true,
            timestamp = getCurrentTimeAsString()
        )
        messageInputFieldState.clear()
    }

    private fun getCurrentTimeAsString(): String {
        val formatter = SimpleDateFormat("hh:mm:ss a")
        val currentTime = formatter.format(Date())
        return currentTime
    }


}