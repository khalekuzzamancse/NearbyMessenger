package core.socket.networking

data class PeerInfo(
    val address: String?,
    val port: Int,
    val name:String="Unnamed",
)