package blueetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter

internal class IntentFilters {
    /**
     * - To receive information about each device discovered, your app must register a BroadcastReceiver for the `ACTION_FOUND` intent
     * - The system broadcasts this intent for each device. The intent contains the extra fields `EXTRA_DEVICE` and `EXTRA_CLASS`,
     * which in turn contain a BluetoothDevice and a BluetoothClass, respectively.
     */
    fun intentFilterForNewDeviceFound(): IntentFilter {
        return IntentFilter(BluetoothDevice.ACTION_FOUND)
    }

    /**
    The device silently remains in discoverable mode for the allotted time. To be notified when the discoverable
    mode has changed,register a BroadcastReceiver for the ACTION_SCAN_MODE_CHANGED intent.
    This intent contains the extra fields EXTRA_SCAN_MODE and EXTRA_PREVIOUS_SCAN_MODE, which provide the new and old scan mode, respectively.
    - Possible values for each extra are as follows:
    - SCAN_MODE_CONNECTABLE_DISCOVERABLE
        - - The device is in discoverable mode.
    - SCAN_MODE_CONNECTABLE
        - - The device isn't in discoverable mode but can still receive connections
   - SCAN_MODE_NONE
      -The device isn't in discoverable mode and cannot receive connections

    * If you are initiating the connection to a remote device, you don't need to enable device discoverability.
    Enabling discoverability is only necessary when you want your app to host a server socket that accepts incoming connections,
    as remote devices must be able to discover
    other devices before initiating connections to those other devices.
     */
    fun intentFilterForScanMode(): IntentFilter {
        return IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
    }



    /**
     * - To make the local device discoverable to other devices, call startActivityForResult(Intent, int) with
     * the ACTION_REQUEST_DISCOVERABLE intent. This issues a request to enable the system's discoverable
     * mode without having to navigate to the Settings app, which would stop your own app. By default,
     * the device becomes discoverable for two minutes.
     * You can define a different duration, up to one hour, by adding the EXTRA_DISCOVERABLE_DURATION extra.
     * - Caution: If you set the EXTRA_DISCOVERABLE_DURATION extra's value to 0, the device is
     * always discoverable.
     * This configuration is insecure and therefore highly discouraged.
     */
    fun createDiscoverableIntent()= Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)//discoverable for 5 min
    }
}