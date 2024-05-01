connect(),createGroup(),startDiscovery() ,,these are async call becuase it consist of
network call,so the method to their action successfully or not,to know that we have to 
give a ActionListener call back,so that via this callback ,we will  notified that
the method call(connect,createGroup,,...) was success or not.
These api are written in java,when there are no concept of suspend function,that is why
these api uses the callback,however we can wrap them into a suspend function and can make
the code more  readable.


all device  need to start discover in order to be found by other device.
If the device is support wifi direct then do not need to special method to enable wifi direct,
if wifi is turned on and you called the discover method then automatically wifi direct will be turned on.
after discovering,if we do not form a group explicitly then no group will be created automatically.
if no group is formed then after a while all the discover device will be automatically disappear ,because we 
are using broadcast...which associate a lifecycle of active.
when a group will formed...?

If a connction initation is sent via the connect() method,then the group will be formed,if they connect succesfully,
and if success ,OS will broadcast the WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION intent,and then we have to 
call requestPeers() to get the updated info about the peer such as group is formed or not.. 
the result of  requestPeers() will be avaiable to the   PeerListListener.onPeerList will be executed to update the group info
```kotlin
  WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION  {
      
                manager.requestPeers(channel, PeerListListenerImpl(
                    onPeerListChanged = { peers ->
                        
                    }
                ))

            }
```
Note that util the group is formed,we can not get the group info ,in the group info the ip of group owner 
is available,using the ip the other device can communicate to group owner
