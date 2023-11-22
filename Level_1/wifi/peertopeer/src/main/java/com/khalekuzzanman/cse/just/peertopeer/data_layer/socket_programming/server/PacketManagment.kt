package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server

import android.content.ContentResolver
import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.FileExtensions
import com.khalekuzzanman.cse.just.peertopeer.data_layer.PacketToFileWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream


class PacketManager(
    private val resolver: ContentResolver,
    inputStream: InputStream,
) {
    companion object {
        private const val TAG = "PacketManagerLog:: "
    }

    private var writer: PacketToFileWriter? = null
    private val reader = DataPacketReader(
        inputStream = inputStream,
        onMimeTypeRead = {
            Log.d(TAG, "mimeType:$it")
            val mimeType = FileExtensions.getMimeType(it)
            if (mimeType != null) {
                val ext = FileExtensions.getFileExtension(mimeType)
                if (ext != null) {
                    PacketToFileWriter(
                        resolver = resolver,
                        fileName = ext.ext+generateTimestamp(),
                        extension = ext
                    )
                }
            }
        },
        onPacketReceived = {packet->
            writer?.write(packet)
            Log.d(TAG, "packet received:${packet.size}")
        },
        onCompleted = {
            writer?.writeFinished()
            Log.d(TAG, "all packets received")
        }
    )

    suspend fun listen() {
        reader.listen()
    }
    private fun generateTimestamp() = System.currentTimeMillis().toString()


}


class DataPacketReader(
    private val inputStream: InputStream,
    private val  onMimeTypeRead: suspend  (Byte) -> Unit = {},
    private val onPacketReceived:suspend (ByteArray) -> Unit = {},
    private val onCompleted:suspend () -> Unit = {}
) {
    companion object {
        private const val TAG = "BytesReaderLogger:"
        private const val MAX_BYTES_TO_READ = 1024 * 1//1 KB
        private const val CLOSE_SIGNAL = -1
    }

    suspend fun listen() {
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
                        val packet = buffer.copyOf(numberOfByteWasRead)
                        //returning copy to avoid shared mutability
                        onPacketReceived(packet)
                        Log.d(TAG, "Read:$numberOfByteWasRead")
                    }
                    if (numberOfByteWasRead == CLOSE_SIGNAL) {
                        readingNotFinished = false
                        onCompleted()
                        Log.d(TAG, "ReadingFinished")
                    }
                }
                inputStream.close()
            }


        } catch (e: IOException) {
            Log.d(TAG, "readPackets() Causes Exception")

        }

    }

    private suspend fun readExtensionBytes() {
        var extensionByte: Int
        try {
            withContext(Dispatchers.IO) {
                extensionByte = inputStream.read()
            }
            val extensionReceived = extensionByte != CLOSE_SIGNAL && extensionByte > 0
            if (extensionReceived) {
                val ext = FileExtensions.getMimeType(extensionByte.toByte())
                Log.d(TAG, "ReadExtension:$ext")
                onMimeTypeRead(extensionByte.toByte())
            }

        } catch (_: IOException) {

        }
    }


}