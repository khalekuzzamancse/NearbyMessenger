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
            log("mimeType:$it")
            val mimeType = FileExtensions.getMimeType(it)
            if (mimeType != null) {
                val ext = FileExtensions.getFileExtension(mimeType)
                if (ext != null) {
                    log("onMimeTypeRead(): Ext=${ext.ext}")
                    writer=PacketToFileWriter(
                        resolver = resolver,
                        fileName = ext.ext+generateTimestamp(),
                        extension = ext
                    )
                }
                else{
                    log("onMimeTypeRead(): Ext=NULL")
                }
            }
        },
        onPacketReceived = {packet->
            if (writer!=null){
                writer?.write(packet)
                log("onPacketReceived():${packet.size}")
            }
            else{
                log("onPacketReceived():writer==NULL")
            }

        },
        onCompleted = {
            log("onCompleted():all packets received")
            if (writer!=null){
                writer?.writeFinished()
            }
            else{
                log("onCompleted():writer=NULL")
            }
        }
    )

    suspend fun listen() {
        reader.listen()
    }
    private fun log(message: String){
        Log.d(TAG, message)
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
        private const val TAG = "DataPacketReaderLog:"
        private const val MAX_BYTES_TO_READ = 1024 * 16//1 KB
        private const val CLOSE_SIGNAL = -1
    }
    private var totalBytesRead=0

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
                        totalBytesRead += numberOfByteWasRead

                        Log.d(TAG, "Read:$numberOfByteWasRead")
                    }
                    if (numberOfByteWasRead == CLOSE_SIGNAL) {
                        readingNotFinished = false
                        onCompleted()
                       log("Total read:$totalBytesRead")
                        Log.d(TAG, "ReadingFinished")
                    }
                }
                inputStream.close()
            }


        } catch (e: IOException) {
            log("ReadPacket():Cause Exception")
            Log.d(TAG, "readPackets() Causes Exception")

        }

    }

    private suspend fun readExtensionBytes() {
        var extensionByte: Int
        try {
            withContext(Dispatchers.IO) {
                extensionByte = inputStream.read()
                totalBytesRead += 1
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
    private fun log(message: String){
        Log.d(TAG,message)
    }


}