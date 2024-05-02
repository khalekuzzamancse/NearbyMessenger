package wifi_direct2.model

/**
 *
 * - From the docs: https://developer.android.com/reference/android/net/wifi/p2p/WifiP2pDevice
 * - Status Codes: 0: Connected, 1: Invited, 2: Failed, 3: Available, 4: Unavailable
 * - [Invited] represent that device is connecting,it may or may succeed to connect
 */

enum class PeerStatus {
    Connected, Invited,Failed, Available,Unavailable
}