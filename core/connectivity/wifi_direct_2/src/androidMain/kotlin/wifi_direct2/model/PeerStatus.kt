package wifi_direct2.model

/**
 * - [ConnectionInitiated] represent that device is connecting,it may or may succeed to connect
 */
enum class PeerStatus{
    Discovered,ConnectionInitiated,Connected,Disconnected
}