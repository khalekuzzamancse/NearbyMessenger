package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming

import android.net.wifi.p2p.WifiP2pInfo
import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client.Client
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.client.Peer
import com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import kotlin.random.Random

enum class ConnectionType {
    Client, Server, None
}

class CommunicationManager(
    info: WifiP2pInfo
) {
    private var peer: Peer? = null
    private val isThisDeviceIsServer = info.groupFormed && info.isGroupOwner

    companion object {
        private var _connectionType = MutableStateFlow(ConnectionType.None)
        val connectionType = _connectionType.asStateFlow()
        private const val ALIVE = true
        private const val TAG = "CommunicationManagerClass: "

    }

    private fun dummyMessage(): String {
        return "Hello Bro, I'm ${connectionType.value} !." + "This is msg:${Random.nextInt()}"
    }


    fun sendData(data: ByteArray = "".toByteArray()) {
              Log.d(TAG, "sendData():$data")
//        peer?.sendData(dummyMessage().toByteArray())
        peer?.sendData(data)
    }

//    fun listenReceived() {
////        Log.d(TAG, "listenReceived()")
//        val scope = CoroutineScope(Dispatchers.IO)
//        scope.launch {
//            peer?.readReceivedData()?.collect { data ->
//                Log.d(TAG, "receivedData() as ${connectionType.value.name}: $data")
//            }
//
//        }
//    }
fun listenReceived()= peer?.readReceivedData()



    init {
        if (isThisDeviceIsServer) {
            peer = Server()
            _connectionType.value = ConnectionType.Server

        } else if (info.groupFormed) {
            peer = Client(info.groupOwnerAddress)
            _connectionType.value = ConnectionType.Client
        } else {
            _connectionType.value = ConnectionType.None
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
                    // Log.d(TAG, "DataSend() Successfully")
                }
            } catch (e: Exception) {
                //  Log.d(TAG, "DataSend() Failed:${e.stackTraceToString()}")
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
            // Log.d(TAG, "ReceivedData(): $receivedData")
            if (count > 0)
                receivedData
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
