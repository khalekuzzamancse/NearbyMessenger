package chatbynearbyapi.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.DialogProperties

enum class NetworkRole {
    Advertiser, Discovered
}


@Composable
internal fun JoinAsDialog(onDismissRequest: () -> Unit, onJoinAs: (NetworkRole) -> Unit) {
    AlertDialog(
        modifier = Modifier.semantics {
            contentDescription = "Join as dialog"
        },
        onDismissRequest = {
            onDismissRequest()
        },
        title = { Text("Join Network") },
        confirmButton = {
            Button(
                onClick = {
                    onJoinAs(NetworkRole.Advertiser)
                },
            ) {
                Text("Advertiser")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onJoinAs(NetworkRole.Discovered)
                },
            ) {
                Text("Discoverer")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    )
}