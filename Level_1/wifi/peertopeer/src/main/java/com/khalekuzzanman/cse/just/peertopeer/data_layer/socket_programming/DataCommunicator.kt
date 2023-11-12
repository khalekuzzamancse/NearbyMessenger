package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming

import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.ConnectionInfo
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.ConnectionType
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client.Client
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client.Peer
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


class SocketManager(
    connectionInfo: ConnectionInfo
) {
    private var peer: Peer? = null
    companion object {
        private const val TAG = "SocketManagerLog: "
    }

    fun sendData(data: ByteArray = "".toByteArray()) {
        Log.d(TAG, "sendData():$data")
        peer?.sendData(data)
    }


    fun listenReceived() = peer?.readReceivedData()

    init {
        if (connectionInfo.type == ConnectionType.Server) {
            peer = Server()
        } else if (connectionInfo.isConnected) {
            peer = connectionInfo.groupOwnerAddress?.let { Client(it) }
        }

    }

}

class DataCommunicator(private val socket: Socket) {

    companion object {
        private const val TAG = "DataCommunicatorClass: "
    }

    //Must do in background thread otherwise causes exception
    fun sendData(data: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.IO) {
                    val out = DataOutputStream(socket.getOutputStream())
                    out.write(data)
                    Log.d(TAG, "DataSend(): Successfully")

                }
            } catch (e: Exception) {
                Log.d(TAG, "DataSend() Failed:${e.stackTraceToString()}")
            }
        }

    }
    //Must do in background thread otherwise causes exception

    fun readReceivedData(): String? {
        return try {
            val input = DataInputStream(socket.getInputStream())
            val receivedBytes = ByteArray(1024)
            val count = input.read(receivedBytes)
            val receivedData = String(receivedBytes, 0, count, Charsets.UTF_8)
            Log.d(TAG, "ReceivedData(): $receivedData")

            if (count > 0)
                receivedData
            else null
        } catch (e: Exception) {
            Log.d(TAG, "ReceivedData():${e.printStackTrace()}")
            null
        }
    }
}
