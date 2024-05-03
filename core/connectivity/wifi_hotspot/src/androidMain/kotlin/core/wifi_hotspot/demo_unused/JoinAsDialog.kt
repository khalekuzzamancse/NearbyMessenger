package core.wifi_hotspot.demo_unused

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties


sealed interface NetworkRole {
   data object Server: NetworkRole
    data class Client(val serverIP:String,val serverPort:Int): NetworkRole
}

@Composable
fun JoinAsDialog(onDismissRequest: () -> Unit = {}, onJoinAs: (NetworkRole) -> Unit) {
    val openDialog = remember { mutableStateOf(true) }
    var ipAddress by remember { mutableStateOf("192.168.12.252") }
    var port by remember { mutableStateOf("12345") }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                onDismissRequest()
            },
            title = { Text("Join Network") },
            text = {
                Column {
                    TextField(
                        value = ipAddress, onValueChange = { ipAddress = it },
                        placeholder = {
                            Text("Server IP")
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    TextField(
                        value = port, onValueChange = { port = it },
                        placeholder = {
                            Text("Server port")
                        }
                    )

                }

            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        onJoinAs(NetworkRole.Server)
                    },
                    //enabled = ipAddress.isEmpty() && port.isEmpty()
                ) {
                    Text("Server")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        onJoinAs(NetworkRole.Client(ipAddress, port.toInt()))
                    },
                    //enabled = ipAddress.isNotEmpty() && port.isNotEmpty()
                ) {
                    Text("Client")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }
}