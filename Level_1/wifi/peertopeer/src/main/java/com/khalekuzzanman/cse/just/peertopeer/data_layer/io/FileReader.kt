package com.khalekuzzanman.cse.just.peertopeer.data_layer.io

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream


@Preview
@Composable
fun FetchFileBytesDemo() {
    FetchFileStream(
        onFileSelected = {
            val extension = FileExtensions.getFileExtension(it)
            Log.i("ContentPicked:Ext ", extension.toString())
        },
        onReading = {
            Log.i("ContentPicked:Bytes ", it.size.toString())
        },
        onReadingFinished = {
            Log.i("ContentPicked: ", "Completed")
        }

    )

}


@Composable
fun FetchFileStream(
    onFileSelected: (mimeType: String) -> Unit,
    onReading: suspend (bytes: ByteArray) -> Unit,
    onReadingFinished: suspend () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)

    FilePicker { contentUri ->
        val mimeType = contentResolver.getType(contentUri)
        if (mimeType != null) {
            onFileSelected(mimeType)
        }
        scope.launch {
            val inputStream = contentResolver.openInputStream(contentUri)
            if (inputStream != null) {
                val reader = FileReaderImp(inputStream)
                reader.onReading = onReading
                reader.onReadingFinished = onReadingFinished
                reader.read()

            }

        }

    }

}


/*
Permissions:
No permissions need as runtime or manifest
 */
@Composable
private fun FilePicker(
    onPicked: (Uri) -> Unit
) {

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { contentUri ->
            if (contentUri != null) {
                onPicked(contentUri)
            }
        }
    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                "application/pdf",
                "video/*",
                "image/*",
                "video/*",
                "application/msword",
                "application/ms-doc",
                "application/doc",
                "text/plain"
            )
        )
    }


}

interface FileReader {
    var onReading: suspend (bytes: ByteArray) -> Unit
    var onReadingFinished: suspend () -> Unit
    suspend fun read()
}

class FileReaderImp(
    private val inputStream: InputStream,
) : FileReader {
    companion object {
        private const val TAG = "FileReaderImpLog: "
        private const val MAX_BYTE_TO_READ = 1024 * 16 // 16 KB, use power of 2
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    private val buffer = ByteArray(MAX_BYTE_TO_READ)
    private var bytesRead = 0L

    override var onReading: suspend (bytes: ByteArray) -> Unit = {}
    override var onReadingFinished: suspend () -> Unit = {}
    override suspend fun read() {
        var hasNoPacket = false
        while (!hasNoPacket) {
            hasNoPacket = readPacketOrTerminate()
        }
        onReadingComplete()
    }

    private suspend fun onReadingComplete() {
        log("onReadingComplete():bytesReads $bytesRead")
        resetInfo()
        closeStream()
    }

    private fun resetInfo() {
        bytesRead = 0L

    }

    private suspend fun closeStream() {

        try {
            withContext(Dispatchers.IO) {
                inputStream.close()
                onReadingFinished()
                log("closeStream():stream closed successfully")
            }
        } catch (_: IOException) {
            log("closeStream():IOException caught")
        }
    }

    private suspend fun readPacketOrTerminate(): Boolean {
        var numberOfByteWasRead = 0
        try {
            numberOfByteWasRead = withContext(Dispatchers.IO) {
                inputStream.read(buffer)
            }
            if (numberOfByteWasRead > 0) {
                val actualReadBytes = buffer.copyOf(numberOfByteWasRead)
                bytesRead += numberOfByteWasRead
                log("readPacketOrTerminate():$numberOfByteWasRead bytes read")
                //To Avoid sharing the same array  returning a new array
                onReading(actualReadBytes)
                //return true for terminate
            }

        } catch (_: IOException) {
            log("readPacketOrTerminate():IOException caught")
        }
        return numberOfByteWasRead <= 0
    }
}


suspend fun fileRead(
    fileInputStream: InputStream,
    onReading: suspend (bytes: ByteArray) -> Unit,
    onReadingFinished: suspend () -> Unit
) {
    val tag = "fileReadFun: "
    try {
        val maxByteToRead = 1024 * 16 // 16 KB, use power of 2
        val buffer = ByteArray(maxByteToRead)
        var readingNotFinished = true
        while (readingNotFinished) {
            withContext(Dispatchers.IO) {
                val numberOfByteWasRead = fileInputStream.read(buffer)
                val noMoreByteToRead = numberOfByteWasRead <= 0
                Log.d(tag, "Read:$numberOfByteWasRead")
                if (noMoreByteToRead) {
                    readingNotFinished = false
                }
                if (!noMoreByteToRead) {
                    val actualReadBytes = buffer.copyOf(numberOfByteWasRead)
                    onReading(actualReadBytes)
                    // Avoid sharing the same array that is why
                    // returning a new array
                }
            }
        }
        Log.d(tag, "Read:Finished")
        // Moved outside the withContext block
        onReadingFinished()

        try {
            withContext(Dispatchers.IO) {
                fileInputStream.close()
            }
        } catch (_: IOException) {
            // Handle the closing exception if needed
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
