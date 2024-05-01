package chatbynearbyapi.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.DialogProperties

 enum class NetworkRole{
    Advertiser,Discovered
}

@Composable
internal fun JoinAsDialog(onDismissRequest: () -> Unit = {}, onJoinAs: (NetworkRole) -> Unit) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            modifier =  Modifier.semantics {
                contentDescription = "Join as dialog"
            },
            onDismissRequest = {
                openDialog.value = false
                onDismissRequest()
            },
            title = { Text("Join Network") },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        onJoinAs(NetworkRole.Advertiser)
                    },
                ) {
                    Text("Advertiser")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        onJoinAs(NetworkRole.Discovered)
                    },
                ) {
                    Text("Discoverer")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }
}