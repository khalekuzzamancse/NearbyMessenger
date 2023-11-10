package com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.khalekuzzanman.cse.just.peertopeer.WifiAndBroadcastHandlerInstance
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandler
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.CommunicationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ConversionScreenViewModel(
) {
    companion object {
        private const val TAG = "ConversionScreenViewModelClass: "

    }


    private val _messages = MutableStateFlow<List<ConversationScreenMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    val messageInputFieldState = MessageInputFieldState()
    val wifiManager = WifiAndBroadcastHandlerInstance.wifiAndBroadcastHandler

    var communicationManager: CommunicationManager? = null

    fun onConnectionRequest() {
        val info = wifiManager.connectionInfo.value
        if (info != null) {
            communicationManager = CommunicationManager(info)
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                communicationManager?.listenReceived()?.collect { data ->
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

    }

    fun onSendRequest() {
        val message = messageInputFieldState.message.value
        communicationManager?.sendData(message.toByteArray())
        _messages.value = messages.value + ConversationScreenMessage(
            message = message,
            isSender = true,
            timestamp = getCurrentTimeAsString()
        )
        messageInputFieldState.clear()
    }
    fun getCurrentTimeAsString(): String {
        val formatter = SimpleDateFormat("hh:mm:ss a")
        val currentTime = formatter.format(Date())
        return currentTime
    }


}