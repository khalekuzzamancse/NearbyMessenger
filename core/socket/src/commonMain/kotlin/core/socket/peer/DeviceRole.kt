package core.socket.peer
sealed interface DeviceRole {
    data object Server : DeviceRole
    data class Client(val serverAddress: String, val serverPort: Int) : DeviceRole
}