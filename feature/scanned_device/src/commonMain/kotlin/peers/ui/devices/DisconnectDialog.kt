package peers.ui.devices

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun DisconnectDialog(
    onConfirm:()->Unit,
    onCancel:()->Unit,
) {
        AlertDialog(
            onDismissRequest = {  },
            title = {
                Text(text = "Confirm Disconnect")
            },
            text = {
                Text("Are you sure you want to disconnect?")
            },
            confirmButton = {
                Button(
                    onClick = onConfirm
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }
            }
        )
    }

