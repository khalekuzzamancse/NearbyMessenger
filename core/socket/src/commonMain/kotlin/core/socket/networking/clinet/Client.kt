package core.socket.networking.clinet

import core.socket.networking.Participants
import kotlinx.coroutines.flow.Flow
import java.net.Socket

interface Client: Participants {
    val connectedServer: Flow<Socket?>
    fun getConnectedServerSocket():Socket?
    fun isNotConnected(): Boolean
    fun isConnected(): Boolean
    suspend fun connect(timeoutSeconds: Int):Result<Unit>
    suspend fun closeConnection()
}
