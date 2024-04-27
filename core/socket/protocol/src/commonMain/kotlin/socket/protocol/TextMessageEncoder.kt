package socket.protocol

import socket.protocol.TextMessageProtocol.DEVICE_NAME_LENGTH
import socket.protocol.TextMessageProtocol.FILLED_CHAR
import socket.protocol.TextMessageProtocol.TIMESTAMP_LENGTH

class TextMessageEncoder(msg: TextMessage)  :Encoder(msg){
    override fun encodeName(name: String) =
        name.padEnd(DEVICE_NAME_LENGTH, FILLED_CHAR).take(DEVICE_NAME_LENGTH)


    override fun encodeTime(time: Long)=time.toString().padStart(TIMESTAMP_LENGTH, '0').take(TIMESTAMP_LENGTH)
     fun encodeText()=super.encode()

    override fun toString() = super.encode()

}