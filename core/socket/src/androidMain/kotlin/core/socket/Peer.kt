package core.socket

import kotlinx.coroutines.flow.StateFlow

interface Peer {
    suspend fun sendData(data: ByteArray)
    suspend fun stopSend()
    fun readReceivedData(): StateFlow<String?>
}