package kzcse.wifidirect.data_layer.io

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

interface PacketReceiver {
    var onMimeTypeRead: suspend (Byte) -> Unit
    var onFileSizeArrived: suspend (Byte) -> Unit
    var onPacketReceived: suspend (ByteArray) -> Unit
    var onCompleted: suspend () -> Unit
    suspend fun listen()
}


class DataPacketReader(
     val inputStream: InputStream,
): PacketReceiver {
    companion object {
        private const val TAG = "DataPacketReaderLog:"
        private const val MAX_BYTES_TO_READ = 1024 * 64//1 KB
        private const val CLOSE_SIGNAL = -1
    }

    override var onMimeTypeRead: suspend (Byte) -> Unit = { }
    override var onFileSizeArrived: suspend (Byte) -> Unit = {}
    override var onPacketReceived: suspend (ByteArray) -> Unit = {}
    override var onCompleted: suspend () -> Unit = {}

    ///

    private var dataBytesRead = 0L
    private var extensionBytesRead = 0L
    override suspend fun listen() {
        readExtensionBytes()
        readPackets()
    }

    private suspend fun readPackets() {
        var readingNotFinished = true
        val buffer = ByteArray(MAX_BYTES_TO_READ)
        try {
            withContext(Dispatchers.IO) {
                while (readingNotFinished) {
                    val numberOfByteWasRead = inputStream.read(buffer)
                    if (numberOfByteWasRead != CLOSE_SIGNAL) {
                        if (numberOfByteWasRead>0){
                            //avoiding index  be -ve
                            val packet = buffer.copyOf(numberOfByteWasRead)
                            //returning copy to avoid shared mutability
                            onPacketReceived(packet)
                            dataBytesRead += numberOfByteWasRead

                            Log.d(TAG, "Read:$numberOfByteWasRead")
                        }

                    }
                    if (numberOfByteWasRead == CLOSE_SIGNAL) {
                        readingNotFinished = false
                        complete()
                    }
                }
                inputStream.close()
            }


        } catch (e: IOException) {
            log("ReadPacket():Cause Exception")
            Log.d(TAG, "readPackets() Causes Exception")

        }

    }
    private suspend fun complete(){
        onCompleted()
        log("onCompleted(): data bytes read:$dataBytesRead\n" +
                "extensionBytes read:$extensionBytesRead\n" +
                "total bytes read:${dataBytesRead+extensionBytesRead}\n")
    }

    private suspend fun readExtensionBytes() {
        var extensionByte: Int
        try {
            withContext(Dispatchers.IO) {
                extensionByte = inputStream.read()
                extensionBytesRead += 1
            }
            val extensionReceived = extensionByte != CLOSE_SIGNAL && extensionByte > 0
            if (extensionReceived) {
                val ext = FileExtensions.getMimeType(extensionByte.toByte())
                log("ReadExtension():$ext")
                onMimeTypeRead(extensionByte.toByte())
            }

        } catch (_: IOException) {
            log("ReadExtension():Cause Exception")
        }
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }


}