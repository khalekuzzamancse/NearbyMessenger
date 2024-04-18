package core.socket.server

import java.net.Socket
import kotlinx.coroutines.flow.Flow

interface Server {
    val connectedClients: Flow<List<Socket>>
    val port: Int
    val address: String?
    suspend fun runServer()
}
