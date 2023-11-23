package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server

import android.content.ContentResolver
import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.io.DataPacketReader
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.DataCommunicator
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client.Peer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

class Server(
    private val resolver: ContentResolver
) : Peer {
    //select port 0 as a result OS will give a available port
    //which is a better solution,to overcome same port used by multiple processes

    companion object {
        private const val TAG = "ServerLog: "
        const val SERVER_PORT = 45555
    }


    private val server = ServerSocket(SERVER_PORT)
    private var connectedClientSocket: Socket? = null
    private var dataCommunicator: DataCommunicator? = null


    private val _lastMessage = MutableStateFlow<String?>(null)

    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    while (true) {
                        connectedClientSocket = server.accept()
                        connectedClientSocket?.let { socket ->
                            dataCommunicator = DataCommunicator(socket)
                            Log.d(TAG, "Connected to client")
                            //listening data continuously
                            listenPackets()
                        }
                        delay(1000)
                        Log.d(TAG, "Server Running")
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    override suspend fun sendData(data: ByteArray) {
        dataCommunicator?.sendData(data)
    }

    override suspend fun stopSend() {
        TODO("Not yet implemented")
    }

    private fun listenPackets() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            connectedClientSocket?.let { socket ->
                val packetManager = PacketManager(
                    resolver = resolver,
                    reader =DataPacketReader(inputStream = socket.getInputStream())
                )
                packetManager.listen()
                Log.d(TAG, "ListenPacket()")

//                val reader = DataPacketReader(
//                    inputStream = socket.getInputStream(),
//                    onMimeTypeRead = {
//                        Log.d(TAG, "mimeType:$it")
//                    },
//                    onPacketReceived = {
//                        Log.d(TAG, "packet received:${it.size}")
//                    },
//                    onCompleted = {
//                        Log.d(TAG, "all packets received")
//                    }
//                )
                // reader.listen()
            }
        }
    }

    override fun readReceivedData(): StateFlow<String?> {
        // Log.d(TAG, "receivedData()")
        return _lastMessage.asStateFlow()
    }


}



