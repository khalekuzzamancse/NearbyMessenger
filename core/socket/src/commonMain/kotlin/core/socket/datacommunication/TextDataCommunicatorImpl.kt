package core.socket.datacommunication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Reads and sends data over a TCP/IP socket connection.
 * This communicator implements a length-prefixed messaging protocol to handle
 * transmission and reception of text messages up to 4KB in size.
 *
 * Protocol Specification:
 * - Each message is prefixed with its length as a 4-byte integer (big-endian format),
 *   allowing the receiver to know exactly how many bytes to read for each message.
 *
 * Sending Messages:
 * - Messages are first converted to UTF-8 encoded bytes.
 * - A 4-byte header, representing the length of the byte array, is prepended to the message.
 * - The combined byte array (length header + message) is then sent over the socket.
 * - This method ensures that each message is sent as a discrete unit, regardless of TCPDF's
 *   stream-oriented nature, preventing message concatenation at the receiving end.
 *
 * Receiving Messages:
 * - The receiver reads the first 4 bytes to determine the message length.
 * - It then reads the subsequent bytes (as indicated by the length header) to retrieve the entire message.
 * - This process repeats for each message, ensuring that messages are received as distinct entities
 *   and processed accordingly.
 *
 * Approximations (based on UTF-8 encoding):
 * - Characters: Up to approximately 4096 characters if only ASCII characters are used.
 * - Words: Assuming an average word length of 5 characters plus a space, approximately 680 words.
 * - Sentences: Assuming an average sentence length of 15-20 words, approximately 34-45 sentences.
 *
 * Caution:
 * - If multiple messages are sent in quick succession, ensure that each message is retrieved promptly
 *   to prevent buffer overflow or message merging, as TCP does not inherently preserve message boundaries.
 */

class TextDataCommunicatorImpl(
    private val socket: Socket
) : TextDataCommunicator {
    companion object {
        private const val BUFFER_SIZE = 4 * 1024
        private const val BYTES_FOR_MESSAGE_LEN = 4
    }


    override suspend fun sendMessage(message: String): Result<Unit> {
        val data = prepareMessageData(message)
        return if (data.size > BUFFER_SIZE) {
            Result.failure(IllegalArgumentException("Message exceeds the maximum size of 4096 bytes"))
        } else {
            sendData(data)
        }
    }

    private fun prepareMessageData(message: String): ByteArray {
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        val buffer = ByteBuffer.allocate(BYTES_FOR_MESSAGE_LEN + messageBytes.size)
        buffer.putInt(messageBytes.size)
        buffer.put(messageBytes)
        return buffer.array()
    }

    private suspend fun sendData(data: ByteArray): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            val out = DataOutputStream(socket.getOutputStream())
            out.write(data)
            out.flush()
            //println("${this::class.simpleName}Log ,DataSend(): Successfully")
            Result.success(Unit)
        }
    } catch (e: Exception) {
        println("${this::class.simpleName}Log ,DataSend() Failed: ${e.stackTraceToString()}")
        Result.failure(e)
    }

    override suspend fun retrieveReceivedData(): List<String> = withContext(Dispatchers.IO) {
        val messages = mutableListOf<String>()
        val input = DataInputStream(socket.getInputStream())
        while (input.available() > 0) {
            val message = readMessage(input)
            message?.let { messages.add(it) }
        }
        messages
    }

    // Helper method to read the length and content of the next message
    private fun readMessage(input: DataInputStream): String? {
        val length = readMessageLength(input)
        return if (length in 1..BUFFER_SIZE) {
            readMessageContent(input, length)
        } else {
            null
        }
    }


    private fun readMessageLength(input: DataInputStream): Int {
        val lengthBytes = ByteArray(BYTES_FOR_MESSAGE_LEN)
        input.readFully(lengthBytes)
        //assuming big-endian order
        return (lengthBytes[0].toInt() and 0xFF shl 24) or
                (lengthBytes[1].toInt() and 0xFF shl 16) or
                (lengthBytes[2].toInt() and 0xFF shl 8)  or
                (lengthBytes[3].toInt() and 0xFF)
    }

    private fun readMessageContent(input: DataInputStream, length: Int): String {
        val buffer = ByteArray(length)
        input.readFully(buffer, 0, length) // Ensure all bytes are read
        return String(buffer, StandardCharsets.UTF_8)
    }

}
