
- They must first form a channel of communication using a pairing process
- Another device finds the discoverable device using a service discovery process
- Needs to access the BluetoothAdapter and determine if Bluetooth is available on the device
- All of the Bluetooth APIs are available in the `android.bluetooth` package
## Permission
If your app targets Android 12 (API level 31) or higher, declare the following permissions in your
app's manifest file:

- `android.permission.BLUETOOTH_SCAN`
  - Required to be able to discover and pair nearby Bluetooth devices
  
- `android.permission.BLUETOOTH_ADVERTISE`
  - If your app makes the current device discoverable to other Bluetooth devices means Required to be able to 
  advertise to nearby Bluetooth devices
- `android.permission.BLUETOOTH_CONNECT`
  - Required to be able to connect to paired Bluetooth devices
- android.permission.ACCESS_FINE_LOCATION
  - If your app uses Bluetooth scan results to derive physical location
  - Allows an app to access precise location
  
    For your legacy Bluetooth-related permission declarations, set android:maxSdkVersion to 30. 
    This app compatibility step helps the system grant your app only the Bluetooth permissions
    that it needs when installed on devices that run Android 12 or higher.
- The `BLUETOOTH_ADVERTISE`, `BLUETOOTH_CONNECT`, and `BLUETOOTH_SCAN` permissions are runtime permissions
- 



# Resources
- https://developer.android.com/develop/connectivity/bluetooth