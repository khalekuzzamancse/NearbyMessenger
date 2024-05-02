
serverIp is the is Gateway from the connected hotspot details.Go to to details on the connected 
hotspot from the device setting and you find the Gateway info,as well as IP Info,do not use the IP Info,
Use the Gateway Info as IP address to join as client.

The wifi hotspot follow the ACCESS POINT base protocol that is why you can not communicate via device ip instead we have to use the 
gateway to communicate

in the client device(that is not owner of the hotspot),you can get the connected hotspot gateway by using the following 
code in the application context or some other context,then use the ip to connect as client.
for the device that is the owner of the hotspot,this will return null
```kotlin
    try {
            val connectivityManager =getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(network)
            // Find the first available gateway
            val ip= linkProperties
            log("info:IP:$ip")
        }
        catch (e:Exception){
            log("info:Exe:$e")
        }


```