package kzcse.wifidirect.data_layer.io

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import kzcse.wifidirect.PermissionIfNeeded


//Bugs:No bug till now
//Tested works fine
@RequiresApi(Build.VERSION_CODES.TIRAMISU)

@Composable
fun AnyFileReadingWriteDemo() {
    PermissionIfNeeded()
    val resolver = LocalContext.current.contentResolver

    var openFilePicker by remember {
        mutableStateOf(false)
    }
    //create the object outside a composition scope so that it does not override
    // or created multiple times
    //make sure because of multiple times override or creating causes data loss
    val writer: FileWriter = remember {
        PacketWriter(resolver)
    }

    if (openFilePicker) {
        FetchFileStream(
            onFileSelected = {
                val extension = FileExtensions.getFileExtension(it)
                if (extension != null) {
                    writer.setFileName(System.currentTimeMillis().toString())
                    writer.setExtension(extension)
                    writer.makeReadyForWrite()
                }

            },
            onReading = {
                writer.write(it)
            },
            onReadingFinished = {
                writer.stopWriting()
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


