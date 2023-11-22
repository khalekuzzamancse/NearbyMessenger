package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client

import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


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
class Client2(
    private val hostAddress: InetAddress,
) : Peer {
    private var serverSocket: Socket? = null
    private val _lastMessage = MutableStateFlow<String?>(null)

    companion object {
        private const val TAG = "ClientClass"
    }

    init {
        //   tryConnectUntil(fiveMin)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            connect()
        }

    }


    private fun isNotConnected(): Boolean {
        serverSocket ?: return true
        return false
    }

    private fun isConnected() = !isNotConnected()

    private suspend fun closeConnection() {
        serverSocket?.let { socket ->
            withContext(Dispatchers.IO) {
                socket.getOutputStream().close()
            }
        }
        serverSocket = null
        Log.d(TAG, "Disconnected")

    }


    override suspend fun sendData(data: ByteArray) {
        Log.d(TAG, "sendData()")
        if (isNotConnected())
            connect()
        send(data)
//        closeConnection()
//        connect()
    }

    private suspend fun send(data: ByteArray) {
        serverSocket?.let { socket ->
            try {
                withContext(Dispatchers.IO) {
                    val out = socket.getOutputStream()
                    out.write(data)
                    Log.d(TAG, "DataSend(): Successfully")
                }
            } catch (e: Exception) {
                Log.d(TAG, "DataSend() Failed:${e.stackTraceToString()}")
            }
        }
    }


    override suspend fun stopSend() {
        closeConnection()
       // connect()//reconnect for if server wants reply back
    }

    private fun listenContinuously() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            while (true) {
                // _lastMessage.value = dataCommunicator.readReceivedData()
            }

        }
    }

    suspend fun connect() {
        if (isConnected())
            return
        while (true) {
            try {
                val socket = withContext(Dispatchers.IO) {
                    val newSocket = Socket()
                    newSocket.connect(
                        InetSocketAddress(
                            hostAddress.hostAddress,
                            Server.SERVER_PORT
                        )
                    )
                    newSocket

                }
                serverSocket = socket
                if (socket.isConnected) {
                    Log.d(TAG, "Connected Successfully")
                    return
                } else {
                    withContext(Dispatchers.IO) {
                        socket.close()
                    }
                    return
                }
            } catch (ex: IOException) {
                // Delay before the next retry
                Log.d(TAG, "Connection Failed:Retrying")
                delay(1000)
            }
        }

    }


    override fun readReceivedData(): StateFlow<String?> {
        return _lastMessage.asStateFlow()
    }

}