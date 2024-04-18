package core.socket.networking.server

import java.net.Socket
import kotlinx.coroutines.flow.Flow


interface Server {
    val connectedClients: Flow<List<Socket>>
    fun getClientsSocket():List<Socket>
    val port: Int
    val address: String?
    suspend fun runForever()
    fun isServerCreated()=address!=null
}