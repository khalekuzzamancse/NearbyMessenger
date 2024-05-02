# About
- A device can join either as a client or as a server.
- If a device wants to join as a client, this module will maintain the client instances to communicate with the server.
- If a device wants to join as a server, it will manage a single server instance as well as some client instances. This is because we are following the Mediator pattern, where the server acts as the mediator. 
- Thus, the server itself does not listen for or send messages as the clients do. However, the device running the server also needs to communicate with other devices. Therefore, we need to maintain separate clients for the device acting as the server.
- This module functions as a factory (Factory design pattern).
- It does not expose unnecessary information; it only exposes the send and receive methods. It determines internally whether the device 
will join as a server or a client and handles the send and receive methods accordingly.
This ensures that dependent modules do not need to manage these aspects.

## Client class
- socket.netAddress.hostName is  the name of the server(not the client device)
- socket.remoteSocketAddress is :ip:port of server (not the client device)