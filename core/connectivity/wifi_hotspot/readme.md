
# Implementation Guidelines and Challenges

## For the non hotspot owner
For the security reason   it is not straight forward to connect the wifi network either supplied by Router or Hotspot.
But using the android OS system call such as using Intent Action,we can navigate the user to the Wifi setting
so that  they can turn on the Wifi and connect that.
I think this is okay,because connecting to a network need password and select it,so even if after some work and writing
 a UI that show the list of scanned wifi device and allow user to choose a network to connect,we can direcly
navigate him to the System UI as result we need to write extra code and UI,also we do not need to manage extra 
permission and the system is UI better than your UI.
Since Server app(hotspot owner) need not to know the  IP of the client,it just requested  a connection,so wherever you 
join via the System UI or App you both are equal.

To launch a intent for wifi you can either go to Setting[https://developer.android.com/reference/android/provider/Settings]   and find the action 
that matched our requirement.
Also WifiManager[https://developer.android.com/reference/android/net/wifi/WifiManager]  to find out what are the constant defined
for the action to launch the user setting.

Navigate to setting has defined in Setting as well as WifiManager,here I am going to use the Setting constant.
```kotlin
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivity(intent)
```

### Finding the gateway of the connect hotspot
Note that the hotspot ip that is available in the hotspot configuration will not be able to transfer data,we need the 
gateway,the gateway will be act as ip in the `Java Socket`.
Use the following method on `ConnectivityManager` instance ,try it after connecting with the hotspot,otherwise it will be null.

```kotlin
  fun getHotspotDefaultGateway(): String? {
        try {
            val network = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(network)
            linkProperties?.routes?.forEach { routeInfo: RouteInfo ->
                if (routeInfo.isDefaultRoute) 
                    return routeInfo.gateway?.hostAddress
            }
        } catch (_: Exception) {
        }
        return null
    }
```




## For Hotspot owner
For the security reason we are unable to turn ON/OFF the hotspot programmatically using our code.
So ask the user to manually turn on the hotspot.
Note that right now I did not find any System call such as Intent action to navigate the Hotspot setting,that is why
the only option is to ask the user itself to do it manually.

But for improving the user experience we can do the following:
concept 1: If the user is connected to a network means a wifi then it is not able to start the tethering/portable hotspot.
using the connect 1,if the user is already connected to network but want to join as server(hotspot owner),then 
we will prevent him to turn of the wifi otherwise it will not allowed to continue.

If the user is not connect to a wifi or hotspot then the ``getHotspotDefaultGateway`` will return null,so using 
it we will check if it connected to a network or not,though you can check via the wifi is enabled or not..











# Resources and keyword for further reading

### local only  hotspot
 it is easy to start a local only  hotspot ,but it is not a tethered hotspot,so i did not find any easy way to 
connect the other devices to that hotspot.You can explore the concept later.


https://source.android.com/docs/core/connect/wifi-softap
tethered hotspot vs local only  hotspot
The AOSP Settings app provides a baseline for configuring a tethering hotspot with SSID and security credentials.

Because of the android os restriction the hotspot owner has to enable the hotpot manually,
then the client scan the wifi as they do,and then connect.

Resources:
https://developer.android.com/develop/connectivity/wifi/wifi-scan#kotlin
In android 10 or higher we can  not start scan,instead we can tell the user to scan via the setting,
and we can get the scan result and use it,



