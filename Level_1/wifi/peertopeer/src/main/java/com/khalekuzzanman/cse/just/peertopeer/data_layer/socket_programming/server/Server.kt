package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server

import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.FileExtensions
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
import java.io.IOException
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

class Server : Peer {
    //select port 0 as a result OS will give a available port
    //which is a better solution,to overcome same port used by multiple processes

    companion object {
        private const val TAG = "ServerClass: "
         const val SERVER_PORT=45555
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
                                listenContinuously()
                        }
                        delay( 1000)
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

    private fun listenContinuously() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
                connectedClientSocket?.let {
                    val received = mutableListOf<Byte>()
                    val closeSignal = -1
                    var readingNotFinished = true
                    val maxByteToRead = 1024* 1//1 KB
                    val buffer = ByteArray(maxByteToRead)
                    try {
                        val inputStream: InputStream = connectedClientSocket!!.getInputStream()
                        //read the 1st bytes to determine the file extension
                        val extensionBytes = inputStream.read()
                        if(extensionBytes!=closeSignal&&extensionBytes>0){
                            val ext=FileExtensions.getMimeType(extensionBytes.toByte())
                            Log.d(TAG ,"ReadExtension:$ext")
                        }
                        //
                        //
                        while (readingNotFinished) {
                            val numberOfByteWasRead = inputStream.read(buffer)
                            if (numberOfByteWasRead != closeSignal) {
                                val packet = buffer.copyOf(numberOfByteWasRead)
                              //  onPacketReceived(packet)
                                Log.d(TAG ,"Read:$numberOfByteWasRead")
                                packet.forEach { received.add(it) }

                            }
                            if (numberOfByteWasRead == closeSignal) {
                                readingNotFinished = false
                               // onFinished()
                                Log.d(TAG ,"ReadingFinished")
                            }
                        }
                        inputStream.close()
                    } catch (e: IOException) {
                        Log.d(TAG ,"readPackets() Causes Exception")


                }
               // _lastMessage.value = dataCommunicator?.readReceivedData()
            }

        }
    }


    override fun readReceivedData(): StateFlow<String?> {
        // Log.d(TAG, "receivedData()")
        return _lastMessage.asStateFlow()
    }


}