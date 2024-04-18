package desktop
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
enum class NetworkRole {
    Server,
    Client
}
@Composable
fun JoinAsDialog(onDismissRequest: () -> Unit={}, onJoinAs: (NetworkRole) -> Unit) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                onDismissRequest()
            },
            title = { Text("Join Network") },
            text = { Text("Do you want to join as a server or a client?") },
            confirmButton = {
                Button(onClick = {
                    openDialog.value = false
                    onJoinAs(NetworkRole.Server)
                }) {
                    Text("Server")
                }
            },
            dismissButton = {
                Button(onClick = {
                    openDialog.value = false
                    onJoinAs(NetworkRole.Client)
                }) {
                    Text("Client")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }
}