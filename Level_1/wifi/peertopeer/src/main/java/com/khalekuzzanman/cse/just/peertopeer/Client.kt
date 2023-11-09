package com.khalekuzzanman.cse.just.peertopeer

import DataCommunicator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

interface Peer {
    fun sendData(data: ByteArray)
    fun readReceivedData(): StateFlow<String?>
}

class Client(
    hostAddress: InetAddress
) : Peer {
    private val socket = Socket()
    private val dataCommunicator = DataCommunicator(socket)
    private val _lastMessage = MutableStateFlow<String?>(null)

    companion object {
        private const val TAG = "ClientClass"
    }

    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {

            withContext(Dispatchers.IO) {
                try {
                    while (true) {
                        socket.connect(InetSocketAddress(hostAddress.hostAddress, 8888), 2000)
                        //listing continuously
                        if (socket.isConnected){
                            listenContinuously()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun sendData(data: ByteArray) {
        Log.d(TAG, "sendData()")
        dataCommunicator.sendData(data)
    }

    private fun listenContinuously() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            while (true) {
                _lastMessage.value = dataCommunicator.readReceivedData()
            }

        }
    }

    override fun readReceivedData(): StateFlow<String?> {
        return _lastMessage.asStateFlow()
    }

}