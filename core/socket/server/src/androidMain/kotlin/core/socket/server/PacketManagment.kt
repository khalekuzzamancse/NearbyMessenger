package core.socket.server

import android.util.Log
import kzcse.wifidirect.data_layer.io.FileExtensions
import kzcse.wifidirect.data_layer.io.PacketReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileWriter


/*
Following Open close principle for this class
Following Dependency injection so that for different implementations of
Reader and writer we do  not have to modify the source code of it.
reducing the coupling

 */


class PacketManager(
    private val packetWriter: FileWriter,
    private val packetReader: PacketReceiver
) {
    private var packetReceived=0L
    private var packetWritten=0L
    companion object {
        private const val TAG = "PacketManagerLog:: "
    }
    private val _receivingData= MutableStateFlow(false)
    val receivingData =_receivingData.asStateFlow()

    private fun listenerPacketCompleted() {
        packetReader.onCompleted = {
            log("onCompleted():all packets received")
            log("onCompleted():read=$packetReceived ,write=$packetWritten")
            this.packetWriter.stopWriting()
            _receivingData.value=false
        }
    }


    private fun listenPacket() {
        packetReader.onPacketReceived = { packet ->
            packetReceived+=packet.size
            packetWritten+=packet.size
            this.packetWriter.write(packet)
            log("onPacketReceived():${packet.size}")
            _receivingData.value=true
        }
    }

    private fun listenMimeType() {
        packetReader.onMimeTypeRead = {
            _receivingData.value=true
            //
            packetReceived+=1
            log("mimeType:$it")
            val mimeType = FileExtensions.getMimeType(it)
            if (mimeType != null) {
                val ext = FileExtensions.getFileExtension(mimeType)
                if (ext != null) {
                    log("onMimeTypeRead(): Ext=${ext.ext}")
                    packetWriter.apply {
                        setFileName(generateTimestamp())
                        setExtension(ext)
                        makeReadyForWrite()
                    }
                    //   fileName = ext.ext + generateTimestamp(),
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
        packetReader.listen()
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    private fun generateTimestamp() = System.currentTimeMillis().toString()


}

