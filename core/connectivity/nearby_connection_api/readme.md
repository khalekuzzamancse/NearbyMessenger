## Resources
- [Android Official Video](https://youtu.be/oi_ARV_I8Dc)
- [ Google Official with implementation](https://developers.google.com/nearby/connections/android/get-started)

NSD is not like a client -server protocol,NSD is based on two protocols
- [DNS-SD](https://youtu.be/v8poeqoeRgE) and 
- [multicast DNS](https://youtu.be/CPOpPTpMSiE)

## This is NearByConnection API demo
Nearby Connections is a peer-to-peer networking API that allows apps to easily discover, connect to,  and exchange data with nearby devices in 
real-time, regardless of network connectivity .that means you can transform data via bluetooth,wifi  or other connectivity. Under the hood, the API 
uses a combination of Bluetooth,
BLE, and Wi-Fi technologies


- Advertisers advertise themselves so that the other devices(Discoverers) discover nearby Advertisers and connect with them.
- When Discoverer initiate a connection request,then if the initiate  is success,both the Adviser and Discoverer will get a confirmation to accept
the connect or not
- If both are accept the request then they are connected and then can exchange data
- Advertisers will become visible to other discovers and discoverers will be notified when a new advertiser is found or has been lost
- In order to connect the Discovered need to send/initiate a connection request to the Advertiser
- 


## Project setup
- add Dependency to your module gradle `  implementation("com.google.android.gms:play-services-nearby:19.2.0")`
- Add the following permission
``` xml
<!-- Required for Nearby Connections -->
<uses-permission android:maxSdkVersion="31" android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:maxSdkVersion="31" android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:maxSdkVersion="30" android:name="android.permission.BLUETOOTH" />
<uses-permission android:maxSdkVersion="30" android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:maxSdkVersion="28" android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:minSdkVersion="29" android:maxSdkVersion="31" android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:minSdkVersion="31" android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:minSdkVersion="31" android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:minSdkVersion="31" android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:minSdkVersion="32" android:name="android.permission.NEARBY_WIFI_DEVICES" />
<!-- Optional: only required for FILE payloads -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

``
Since ACCESS_FINE_LOCATION, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT, BLUETOOTH_SCAN and READ_EXTERNAL_STORAGE are considered to be dangerous system permissions, in addition to adding them to your manifest, you must request these permissions at runtime, as described in Requesting Permissions.

If the user does not grant all required permissions, the Nearby Connections API will refuse to allow your app to start advertising or discovering.

## Advertiser

- to create Advertiser need a name of it,that will be seen by Discovered.The name that you provide may be modified by OS ,in order to resolve naming conflicts if multiple nearby devices uses same  name
- Advertiser is going to create a Android service so that it can
run in background so it need a service id,the service id should be globally unique,so use app package name or app namespace name.


## AdvertisingOptions

in order to create advertising we need to create an instance of AdvertisingOptions class.using this class we can set the following options

- Strategy : The network topology ,available  are Strategy.P2P_STAR,Strategy.P2P_CLUSTER, P2P_POINT_TO_POINT

- Can not create the instance directly through the constructor ,instead use the AdvertisingOptions.Builder for creating the instance

## ConnectionLifeCycleCallback
This is the life cycle of a connection such as connection request(initiate),connection request result such as accepted,rejected,..
are notified via this callback.

lets look the technical definition of this callback.


```kotlin
public abstract class ConnectionLifecycleCallback {

    public void onBandwidthChanged(String id,BandwidthInfo var2) {
    }

    public abstract void onConnectionInitiated(String id, ConnectionInfo var2);

    public abstract void onConnectionResult(String id, ConnectionResolution var2);

    public abstract void onDisconnected(String id);
}
```


## `Nearby.getConnectionsClient(..).startAdvertising(....) `
- Used to start advertising
- It takes Advertiser name,service id,an instance of ConnectionLifeCycleCallback and advertiserOptions instance.

 `onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) `
- When a new request will come both Adverser and Discovers,this method will be called
- Here using this connectionInfo we can connect them,it is better to show a confirmation before connect them 
so that the both device know that they are going to connect
- 

