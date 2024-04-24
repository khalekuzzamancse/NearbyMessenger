package wifidirect.connection

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup

//if it is the group owner then will get the client list
//otherwise the list is empty though is it is connected
//if the device is not group owner then its connected with only
//a single device that is the group owner
//   group != null ,if there are connected devices/network
internal data class ScannedDevice(
    val scannedDevices: List<WifiP2pDevice> = emptyList(),
    val connectedDevices: List<WifiP2pDevice> = emptyList()
) {
    fun updateConnectDevices(group: WifiP2pGroup?): ScannedDevice {
        // group != null ,if there are connected devices/network
        val connectedDevices: List<WifiP2pDevice> = if (group != null) {
            //if it is the group owner then will get the client list
            //otherwise the list is empty though is it is connected
            //if the device is not group owner then its connected with only
            //a single device that is the group owner
            val thisDeviceIsGroupOwner = group.isGroupOwner
            if (thisDeviceIsGroupOwner) {
                group.clientList.toList()
            } else {
                this.connectedDevices + group.owner
            }
        } else {
            emptyList()
        }
        return this.copy(connectedDevices = connectedDevices)
    }

    fun updateScannedDevices(peers: WifiP2pDeviceList?): ScannedDevice {
        return if (peers != null) {
            this.copy(scannedDevices = peers.deviceList.toList())
        } else this
    }

    fun getDevice() = scannedDevices.map { device ->
        Device(
            name = device.deviceName,
            isConnected = connectedDevices.contains(device),
            device = device
        )
    }


    override fun toString(): String {
        return "ScannedDevice(\n" +
                "scannedDevices=${scannedDevices.map { it.deviceName.toString() }} \n" +
                ", connectedDevices=${connectedDevices.map { it.deviceName.toString() }}\n" +
                ")"
    }


}