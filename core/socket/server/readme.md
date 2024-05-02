# Working Mechanism
We will follow the Mediator design pattern to efficiently handle multiple clients. The server will act as the mediator.
Each Connected Client will send message to the Server and then the Server will deliver the message to appropriate clients or broadcast it.
This Server module can be used as separate app or process
# Key Classes 
## Server
- The server runs in a background thread, waiting for clients to connect.
- Once a client connects, the server manages it through the `ParticipantHandler` class.
- We use Thread to handle `ParticipantHandler` as a separate process. This ensures that when the server sends a message to a client,
the corresponding client can retrieve it immediately. Using a thread instead of a coroutine is crucial because coroutines are lightweight and may not be as powerful as threads.
- Note that only the device or app acting as the server will create a thread for each new participant.

- ServerSocket.inetAddress is the IP address of that  device and will be used to by the Client to connect with the Server
- `serverSocket.inetAddress.hostAddress` is not related to the connection ip or the device ip  this can be empty or `null` 
even though the server is running successfully

## ParticipantHandler
- Each client will be identified via their IP address for different devices, and IP address + port within the same device.
- After sending data, each client will close their socket and input/output stream. This ensures that data can be retrieved easily
without mixing with previous data.
- When a client socket is closed, the device needs to send another connect request to the server. The server can then store 
the client’s active connection to reply back or forward messages. This is necessary because the server will fail to send data to a 
client whose socket or stream has been closed. 
