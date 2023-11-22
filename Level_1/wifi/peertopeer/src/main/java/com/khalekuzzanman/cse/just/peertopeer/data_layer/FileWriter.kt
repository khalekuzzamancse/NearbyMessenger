package com.khalekuzzanman.cse.just.peertopeer.data_layer

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
import androidx.compose.ui.tooling.preview.Preview
import com.khalekuzzanman.cse.just.peertopeer.PermissionIfNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
File will be write in the download folder
 */


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun TextFileWriteDemo() {
    PermissionIfNeeded()
    val resolver = LocalContext.current.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)

    val writeTextFileDemo: () -> Unit = {
        val textWriter = PacketToFileWriter(
            resolver = resolver,
            fileName = "newText3",
            extension = FileExtensions.TXT
        )
        val packet = "Hello world!".toByteArray()
        val packet2 = "\nnew world!".toByteArray()
        scope.launch {
            textWriter.write(packet)
            textWriter.write(packet2)
            textWriter.writeFinished()
        }

    }
    Button(onClick = writeTextFileDemo) {
        Text(text = "WriteText")
    }


}


class PacketToFileWriter(
    resolver: ContentResolver,
    fileName: String,
    extension: FileExtension
) {

    init {
        log("Writer created")
    }

    companion object{
        private const val TAG = "PacketToFileWriterLog: "
    }
    private val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, extension.mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    private val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
    private val outputStream = uri?.let { it1 -> resolver.openOutputStream(it1) }

    private var totalPacketsWritten = 0


    suspend fun write(packet: ByteArray) {
        try {
            withContext(Dispatchers.IO) {
                if (outputStream != null){
                    outputStream.write(packet)
                    totalPacketsWritten+=packet.size
                    log("write():${packet.size}size packed written:success")
                }
                else {
                    log("write():${packet.size}size packed written:failure,OutputStream=NULL")
                }

            }

        } catch (e: Exception) {
            log("write():${packet.size}size packed written:Causes Exception")
            e.printStackTrace()
        }
    }

    suspend fun writeFinished() {
        try {
            withContext(Dispatchers.IO) {
                outputStream?.close()
                log("OutputStream Close:Successfully")
            }
        } catch (e: Exception) {
            log("OutputStream Close:Failed")
            e.printStackTrace()
        }
        log("total bytes written $totalPacketsWritten")
    }
    private fun log(message: String){
        Log.d(TAG,message)
    }
}