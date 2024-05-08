package blueetooth

import android.bluetooth.BluetoothManager

object BluetoothFactory {
    lateinit var bluetoothController: BluetoothController
    fun createBluetoothController(manager: BluetoothManager) {
        bluetoothController=BluetoothController(manager)
    }
}