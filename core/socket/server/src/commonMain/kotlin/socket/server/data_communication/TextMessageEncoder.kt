package socket.server.data_communication

import client.data_communication.TextMessage

class TextMessageEncoder(
    textMessage: TextMessage
) : TextMessageProtocol() {
    private val ip = textMessage.senderIp
    private val port = if (textMessage.senderPort == null) "" else textMessage.senderPort.toString()
    private val timestamp = textMessage.timestamp.toString()
    private val message = textMessage.message


    private fun encodeIpAddress() =
        ip?.padEnd(IP_LENGTH, FILLED_CHAR_FOR_IP)?.take(IP_LENGTH) ?: "".padEnd(IP_LENGTH, FILLED_CHAR_FOR_IP)
            .take(IP_LENGTH)

    private fun encodePort() = port.padStart(PORT_LENGTH, '0').take(PORT_LENGTH)
    private fun encodeTime() = timestamp.padStart(TIMESTAMP_LENGTH, '0').take(TIMESTAMP_LENGTH)

    fun encode(): String = "${encodeIpAddress()}${encodePort()}${encodeTime()}$message"
    override fun toString() = encode()

}