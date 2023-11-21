package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client

import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.DataCommunicator
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

interface Peer {
    suspend fun sendData(data: ByteArray)
    suspend fun stopSend()
    fun readReceivedData(): StateFlow<String?>
}

/*
Important:
In order to connect server must run first,otherwise can be connected,
but if we want that server may start later then we should try connecting after the connection failed
for certain amount of or till get connected.

this technique will work insha-allah.
after however while trying each time create a new socket as well as a new instance of DataCommunicator
for each new socket and also check if we need to create new instance for CommunicationManager class;
because this class hold the reference of client and server so this may need to updated.
 */
class Client(
    private val hostAddress: InetAddress,
) : Peer {
    private var socket = Socket()
    private var dataCommunicator = DataCommunicator(socket)
    private val _lastMessage = MutableStateFlow<String?>(null)

    companion object {
        private const val TAG = "ClientClass"
    }

    init {
        val fiveMin = 300
        tryConnectUntil(fiveMin)
    }


    private fun tryConnectUntil(second: Int) {
        val scope = CoroutineScope(Dispatchers.Default)
        var elapsedSecond = 0
        scope.launch {
            while (elapsedSecond < second) {
                try {
                    val socket = Socket()
                    withContext(Dispatchers.IO) {
                        socket.connect(
                            InetSocketAddress(
                                hostAddress.hostAddress,
                                Server.SERVER_PORT
                            )
                        )
                    }
                    if (socket.isConnected) {
                        Log.d(TAG, "Connection established")
                        dataCommunicator = DataCommunicator(socket)
                        listenContinuously()
                        break
                    }
                } catch (e: Exception) {
                    // Log.d(TAG, e.stackTraceToString())
                    Log.d(TAG, "Connection failed:Retrying")
                } finally {
                    elapsedSecond++
                    delay(1000)
                }
            }

        }

    }


    override suspend fun sendData(data: ByteArray) {
        Log.d(TAG, "sendData()")
        dataCommunicator.sendData(data)
    }

    override suspend fun stopSend() {
        TODO("Not yet implemented")
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