# Working Mechanism
This module can be used as separate app or process
# Key Classes
## Client
- Each client will be identified via their IP address for different devices, and IP address + port within the same device,in the server side.
- After sending data, each client may close their socket and input/output stream. This ensures that data can be retrieved easily
  without mixing with previous data.
- When a client socket is closed, the device needs to send another connect request to the server. The server can then store
  the client’s active connection to reply back or forward messages. This is necessary because the server will fail to send data to a
  client whose socket or stream has been closed. 
- the client `username` is not accessible or known to the server,that is why server maintain it own representation to identify each client(ip or ip+port)

