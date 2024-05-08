package blueetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.util.Log

class BluetoothController(
    bluetoothManager: BluetoothManager
) {
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    val doesBluetoothSupport = bluetoothAdapter != null

    fun isBluetoothEnabled() = bluetoothAdapter?.isEnabled ?: false

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<NearByBlueetoothDevice> {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        return pairedDevices?.map { device ->
            val deviceName: String = device.name
            val deviceHardwareAddress: String = device.address // MAC address
            NearByBlueetoothDevice(deviceName, deviceHardwareAddress)
        } ?: emptyList()
    }

    /**
     * Caution: Performing device discovery consumes a lot of the Bluetooth adapter's resources.
     * After you have found a device to connect to, be certain that you stop discovery with cancelDiscovery()
     * before attempting a connection. Also, you shouldn't perform discovery while connected to a device
     * because the discovery process significantly reduces the bandwidth available for any existing connections.
     */
    fun startDiscovery(){

    }



    fun createDiscoverableIntent()=IntentFilters().createDiscoverableIntent()

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@BluetoothController::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }

}