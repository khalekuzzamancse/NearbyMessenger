package kzcse.wifidirect.deviceInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

@Composable
fun UserNameDialog(
    userNameManager: UserNameManager,
    onDeviceNameSaved: () -> Unit,
) {
    var tempName by remember { mutableStateOf(userNameManager.userName) }

    AlertDialog(
        onDismissRequest = {
            // Optional: Close the dialog when user taps outside it or presses back.
        },
        title = {
            Text(text = "Enter Your Name")
        },
        text = {
            Column {
                Text("Please enter your name. This name will be used in chats and will be visible to other participants. " +
                        "Note: Your name is only saved locally. If you uninstall the app or clear the app data, you will need to re-enter your name.")
                TextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    userNameManager.userName = tempName
                    onDeviceNameSaved()
                }) {
                Text("Save")
            }
        },
        dismissButton = {
        }
    )
}
