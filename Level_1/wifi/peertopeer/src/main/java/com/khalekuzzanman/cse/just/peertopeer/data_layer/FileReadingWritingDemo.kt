package com.khalekuzzanman.cse.just.peertopeer.data_layer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.khalekuzzanman.cse.just.peertopeer.PermissionIfNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun AnyFileReadingWriteDemo() {
    PermissionIfNeeded()
    val resolver = LocalContext.current.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)
    //
    var openFilePicker by remember {
        mutableStateOf(false)
    }
    //create the object outside a composition scope so that it does not override or created multiple times
    //make sure because of multiple times override or creating causes data loss
    //Bugs: the 1st file some portion may be corrupted why?
    var writer: PacketToFileWriter? = remember {
        Log.i("ContentPicked: ", "Remember")
        null
    }

    if (openFilePicker) {
        FetchFileStream(
            onFileSelected = {
                val extension = FileExtensions.getFileExtension(it)
                if (extension != null) {
                    writer = PacketToFileWriter(
                        resolver = resolver,
                        fileName = System.currentTimeMillis().toString(),
                        extension = extension
                    )
                }

            },
            onReading = {
                scope.launch {
                    writer?.write(it)
                }

            },
            onReadingFinished = {
                scope.launch {
                    writer?.writeFinished()
                }
            }

        )

    }
    Button(onClick = {
        openFilePicker = true
    }
    ) {
        Text(text = "Pick and Write To Download Folder")
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun ImageFileWriteDemo() {
    PermissionIfNeeded()
    val resolver = LocalContext.current.contentResolver
    val scope = CoroutineScope(Dispatchers.IO)
    //
    var openFilePicker by remember {
        mutableStateOf(false)
    }
    //create the object outside a composition scope so that it does not override or created multiple times
    //make sure because of multiple times override or creating causes data loss
    val writer = remember {
        Log.i("ContentPicked: ", "Remember")
        PacketToFileWriter(
            resolver = resolver,
            fileName = "newImage4",
            extension = FileExtensions.JPEG
        )
    }

    if (openFilePicker) {
        FetchFileStream(
            onFileSelected = {
                Log.i("ContentPicked:Ext ", it)
            },
            onReading = {
                scope.launch {
                    writer.write(it)
                }

            },
            onReadingFinished = {
                scope.launch {
                    writer.writeFinished()
                    Log.i("ContentPicked: ", "Completed")
                }
            }

        )

    }
    Button(onClick = {
        openFilePicker = true
    }
    ) {
        Text(text = "WriteJpeg")
        //must choose a jpg or jpeg file
    }
}