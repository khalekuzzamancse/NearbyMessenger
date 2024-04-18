package core.socket

import android.content.ContentResolver
import android.util.Log
import core.socket.networking.clinet.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wifidirect.connection.ConnectionInfo
import wifidirect.connection.ConnectionType
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket


class SocketManager(
    connectionInfo: ConnectionInfo,
    resolver: ContentResolver
) {
    private var peer: Peer? = null


    companion object {
        private const val TAG = "SocketManagerLog: "
    }

    suspend fun sendData(data: ByteArray = "".toByteArray()) {
        Log.d(TAG, "sendData():$data")
        if (peer != null && peer is Client) {
            val client = peer as Client
            client.sendData(data)


        }
    }
    suspend fun stopSend(){
        if (peer != null && peer is Client) {
            val client = peer as Client
            client.stopSend()
        }
    }



    fun listenReceived() = peer?.readReceivedData()

    init {
        if (connectionInfo.type == ConnectionType.Server) {
            peer = ServerImpl()

            Log.d(TAG, "ConnectionType:Server")
        } else if (connectionInfo.isConnected) {
            peer = connectionInfo.groupOwnerAddress?.let { Client(it) }
            Log.d(TAG, "ConnectionType:Client")
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


            if (count > 0) {
                Log.d(TAG, "ReceivedData(): ${receivedData}")
                receivedData
            } else null
        } catch (e: Exception) {
            Log.d(TAG, "ReceivedData():${e.printStackTrace()}")
            null
        }
    }
}
