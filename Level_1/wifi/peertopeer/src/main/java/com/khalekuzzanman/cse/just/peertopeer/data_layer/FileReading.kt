package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
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
        onFileExtensionExtracted = {
            Log.i("ContentPicked:Ext ", it)
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
    onFileExtensionExtracted: (String) -> Unit = {},
    onReading: suspend (bytes: ByteArray) -> Unit = {},
    onReadingFinished: suspend () -> Unit = {}
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)

    FilePicker { contentUri ->
        val mimeType = contentResolver.getType(contentUri)
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        if (fileExtension != null) {
            onFileExtensionExtracted(fileExtension)
        }
        scope.launch {
            val inputStream = contentResolver.openInputStream(contentUri)
            if (inputStream != null) {
                fileRead(
                    fileInputStream = inputStream,
                    onReading = onReading, onReadingFinished = onReadingFinished
                )
            }

        }

    }

}

private suspend fun fileRead(
    fileInputStream: InputStream,
    onReading: suspend (bytes: ByteArray) -> Unit = {},
    onReadingFinished: suspend () -> Unit = {}
) {
    try {
        val maxByteToRead = 1024 * 16 //16KB,use power of 2
        val buffer = ByteArray(maxByteToRead)
        var readingNotFinished = true
        while (readingNotFinished) {
            withContext(Dispatchers.IO) {
                val numberOfByteWasRead = fileInputStream.read(buffer)
                val noMoreByteToRead = numberOfByteWasRead <= 0
                if (noMoreByteToRead) {
                    readingNotFinished = false
                    fileInputStream.close()
                    onReadingFinished()
                } else {
                    val actualReadBytes = buffer.copyOf(numberOfByteWasRead)
                    onReading(actualReadBytes)
                    //avoid sharing the same array that is why
                    //returning a new array
                }
            }

        }
        try {
            withContext(Dispatchers.IO) {
                fileInputStream.close()
            }
        } catch (_: IOException) {

        }

    } catch (e: IOException) {
        e.printStackTrace()
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