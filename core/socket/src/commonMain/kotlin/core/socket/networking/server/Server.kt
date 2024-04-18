package core.socket.networking.server

import java.net.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList

interface Server {
    val connectedClients: Flow<List<Socket>>
    fun getClients():List<Socket>
    val port: Int
    val address: String?
    suspend fun runForever()
    fun isServerCreated()=address!=null
}
