package core.socket.networking.server

import core.socket.networking.Participants
import core.socket.networking.PeerInfo
import java.net.Socket
import kotlinx.coroutines.flow.StateFlow


interface Server:Participants {
    val clients: StateFlow<List<Socket>>
    fun getClientsSocket(): List<Socket> = clients.value
    val participants:StateFlow<List<PeerInfo>>
    val info:PeerInfo
    suspend fun runForever()
    fun isServerCreated() = info.address != null
    fun shutdown()
}