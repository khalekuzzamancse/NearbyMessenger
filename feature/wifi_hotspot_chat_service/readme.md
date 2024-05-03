

TODO:
Find the SSID (network name)in which the device is connected for non hotspot owner
Find the Connected devies SSID for the hotspot owner
How to make sure A device is hotspot owner?
  - f a device is hotspot owner then it is not connected with any other network
  - 


local only network are the network  without the NetworkCapabilities#NET_CAPABILITY_INTERNET










Since the android os give restriction,that is why we can not direcly 
scan the wifi ,and turn on the hotspot,also we can not connect to a wifi ..

but if we are connected to a wifi where the wifi owner can be a router or can be hotspot,
in that case as a client device,we can get the info of connected wifi,
in case of hotspot,at first we will connect to the hotspot via setting app(provied by os),
then in the retive the connected netwrok info ,if the connected network info is null,
that means this not connected to a wifi,note the hotspot owner at a time either can 
supply hotspot or connect to a wifi,
that means if Device X is the hotspot owner,then it network info is null,so we will
make the device as server,
all connected device to this hotspot will get the connected network info and retive 
the default gateway(not the ip),then use the gateway to connect with the sever,
treat this gateway as the device ip...



TODO:
Is is possible to fetch the list on the hotspot owner device which devices 
are connected with it..?
TODO:
How to get the name of the network hotspot/router, in which we are connected to

## Hotspot owner fetched the connected device list


## Get the scanned device for non hotspot owner
get the old scanned device,
navigate to the scanned device sceen of setting.
the top most in the list of the network is the connected,
that means check the  connected network info along with the topmost,
or is the connected info is found over the scanned device list...
SSID=Name of the wifi
BSSID=kind of network id that is unique within the network

Wifi manager:
EXTRA_WIFI_INFO
The lookup key for a WifiInfo object giving the information about the access point to which we are connected.

## Connectivity manager
https://developer.android.com/reference/android/net/ConnectivityManager
££ Misc
https://developer.android.com/reference/android/net/wifi/WifiNetworkSpecifier.Builder#build()
to trigger connection to a network that matches the set params. The system will find the set of networks matching the request and present the user with a system dialog which will allow the user to select a specific Wi-Fi network to connect to or to deny the request. To protect user privacy, some limitations to the ability of matching patterns apply. In particular, when the system brings up a network to satisfy a NetworkRequest from some app, the system reserves the right to decline matching the SSID pattern to the real SSID of the network for other apps than the app that requested the network, and not send those callbacks even if the SSID matches the requested pattern.
For example: To connect to an open network with a SSID prefix of "test" and a BSSID OUI of "10:03:23":

final NetworkSpecifier specifier =
new Builder()
.setSsidPattern(new PatternMatcher("test", PatternMatcher.PATTERN_PREFIX))
.setBssidPattern(MacAddress.fromString("10:03:23:00:00:00"),
MacAddress.fromString("ff:ff:ff:00:00:00"))
.build()
final NetworkRequest request =
new NetworkRequest.Builder()
.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
.setNetworkSpecifier(specifier)
.build();
final ConnectivityManager connectivityManager =
context.getSystemService(Context.CONNECTIVITY_SERVICE);
final NetworkCallback networkCallback = new NetworkCallback() {
...
{@literal @}Override
void onAvailable(...) {}
// etc.
};
connectivityManager.requestNetwork(request, networkCallback);

