package kzcse.wifidirect.data_layer.io

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kzcse.wifidirect.PermissionIfNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream


/*
File will be write in the download folder
 */


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TextFileWriteDemo() {
    PermissionIfNeeded()
    val resolver = LocalContext.current.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)

    val writeTextFileDemo: () -> Unit = {
        val writer: FileWriter = PacketWriter(resolver)
        writer.setFileName(System.currentTimeMillis().toString())
        writer.setExtension(FileExtensions.TXT)
        writer.makeReadyForWrite()
        scope.launch {
            for (i in 1..10) {
                val packet = "Hello world!: $i\n".toByteArray()
                writer.write(packet)
            }
            writer.stopWriting()
        }

    }
    Button(onClick = writeTextFileDemo) {
        Text(text = "WriteText")
    }


}


//
//Used suspend functions make sure that it is thread safe.
//make sure that two threads does not write the multiple bytes in the same file at a time
//otherwise file make be corrupted

interface FileWriter {
    fun setFileName(fileName: String)
    fun setExtension(extension: FileExtension)
    fun makeReadyForWrite()
    suspend fun write(packet: ByteArray)
    suspend fun stopWriting()

}

class PacketWriter(private val resolver: ContentResolver) : FileWriter {
    private var bytesWritten = 0L
    private var fileName: String? = null
    private var extension: FileExtension? = null
    private var outputStream: OutputStream? = null
    private val lock=Unit

    companion object {
        private const val TAG = "PacketWriterLog: "
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    override fun setFileName(fileName: String) {
        this.fileName = fileName
        log("setFileName():$fileName")
    }

    override fun setExtension(extension: FileExtension) {
        this.extension = extension
        log("setExtension():${extension.ext}")
    }

    override fun makeReadyForWrite() {
        val values = createContentValue()
        if (values != null) {
            createOutputStream(values)
            log("makeReadyForWrite():Success")
        } else {
            log("makeReadyForWrite():Failed")
        }
    }

    private fun createOutputStream(values: ContentValues) {
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
        outputStream = uri?.let { it1 -> resolver.openOutputStream(it1) }
    }

    private fun createContentValue(): ContentValues? {
        val fileName = fileName
        val extension = extension
        if (fileName != null && extension != null) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, extension.mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            return values
        } else {
            log("createContentValue():Failed;FileName||Extension==NULL")
        }
        return null
    }

    override suspend fun write(packet: ByteArray) {
//            val outputStream = this.outputStream
//            if (outputStream != null) {
//                writePacket(packet, outputStream)
//            } else {
//                log("write():outputStream is NULL")
//            }
            val outputStream = this.outputStream
            if (outputStream != null) {
                writePacket(packet, outputStream)
            } else {
                log("write():outputStream is NULL")
            }

    }


    private fun writePacket(packet: ByteArray, outputStream: OutputStream) {
        //   log("writePacket():Called")
        try {
            outputStream.write(packet)
            bytesWritten += packet.size
            log("write():Success ;${packet.size} sized packed written")
        } catch (e: Exception) {
            log("write():Failed ${packet.size} sized packed")
            e.printStackTrace()
        }

    }


    override suspend fun stopWriting() {
        closeOutputStream() //after close output stream then make it null,order is important
        log("total bytes written $bytesWritten")
        resetFileInfo()
        log("stopWriting():writingCompleted")
    }

    private fun resetFileInfo() {
        bytesWritten = 0
        fileName = null
        extension = null
        outputStream = null
    }

    private suspend fun closeOutputStream() {
        try {
            withContext(Dispatchers.IO) {
                outputStream?.close()
                log("OutputStream Close:Successfully")
            }
        } catch (e: Exception) {
            log("OutputStream Close:Failed")
            e.printStackTrace()
        }
    }

}


