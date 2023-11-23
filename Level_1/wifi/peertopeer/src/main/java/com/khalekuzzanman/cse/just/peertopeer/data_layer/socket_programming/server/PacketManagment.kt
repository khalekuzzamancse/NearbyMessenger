package com.khalekuzzanman.cse.just.peertopeer.data_layer.socket_programming.server

import android.content.ContentResolver
import android.util.Log
import com.khalekuzzanman.cse.just.peertopeer.data_layer.io.DataPacketReader
import com.khalekuzzanman.cse.just.peertopeer.data_layer.io.FileExtensions
import com.khalekuzzanman.cse.just.peertopeer.data_layer.io.PacketReceiver
import com.khalekuzzanman.cse.just.peertopeer.data_layer.io.PacketToFileWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream


/*
Following Open close principle for this class
Following Dependency injection so that for different implementations of
Reader and writer we do  not have to modify the source code of it.
reducing the coupling
 */

class PacketManager(
    private val resolver: ContentResolver,
    private val reader: PacketReceiver
) {
    companion object {
        private const val TAG = "PacketManagerLog:: "
    }

    private var writer: PacketToFileWriter? = null

    private fun listenerPacketCompleted() {
        reader.onCompleted = {
            log("onCompleted():all packets received")
            if (writer != null) {
                writer?.writeFinished()
            } else {
                log("onCompleted():writer=NULL")
            }
        }
    }

    private fun listenPacket() {
        reader.onPacketReceived = { packet ->
            if (writer != null) {
                writer?.write(packet)
                log("onPacketReceived():${packet.size}")
            } else {
                log("onPacketReceived():writer==NULL")
            }

        }
    }

    private fun listenMimeType() {
        reader.onMimeTypeRead = {
            log("mimeType:$it")
            val mimeType = FileExtensions.getMimeType(it)
            if (mimeType != null) {
                val ext = FileExtensions.getFileExtension(mimeType)
                if (ext != null) {
                    log("onMimeTypeRead(): Ext=${ext.ext}")
                    writer = PacketToFileWriter(
                        resolver = resolver,
                        fileName = ext.ext + generateTimestamp(),
                        extension = ext
                    )
                } else {
                    log("onMimeTypeRead(): Ext=NULL")
                }
            }
        }
    }

    suspend fun listen() {
        listenMimeType()
        listenPacket()
        listenerPacketCompleted()
        reader.listen()
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    private fun generateTimestamp() = System.currentTimeMillis().toString()


}

