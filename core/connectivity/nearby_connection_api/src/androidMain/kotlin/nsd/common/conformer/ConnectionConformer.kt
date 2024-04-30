package nsd.common.conformer

import android.app.AlertDialog
import android.content.Context
import com.google.android.gms.nearby.Nearby
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
/**
 * Shows a confirmation dialog to the user for accepting a connection with an endpoint. The confirmation requires a matched authentication digit (PIN).
 * Upon user confirmation, the connection will be accepted. If the user cancels, no action is taken.
 *
 * @param endpointId The unique identifier of the endpoint attempting to connect.
 * @param info The information about the endpoint which includes the endpoint name and authentication digits.
 * @return Result<Unit> A result of the operation which can be success or failure based on the user's choice and operation success.
 */
internal class ConnectionConformer {
     suspend fun showDialog(
         context:Context,
         authToken: AuthToken,
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {//Access UI so use Main Thread
            suspendCancellableCoroutine { continuation ->
                showConfirmationDialog(
                    context = context,
                    endpointId = authToken.endpointId,
                    endpointName = authToken.pin,
                    authDigit = authToken.pin,
                    onConfirm = {
                        continuation.resume(Result.success(Unit))
                    },
                    onCancel = {
                        continuation.resume(Result.failure(Exception("Connection cancelled by user.")))
                    }
                )
            }
        }
    }
    private fun showConfirmationDialog(
        context: Context,
        endpointId: String,
        endpointName: String,
        authDigit: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {},
    ) {
        AlertDialog.Builder(context)
            .setTitle("Accept connection to $endpointName}")
            .setMessage("Confirm the code matches on both devices:$authDigit")
            .setPositiveButton("Accept") { _, _ ->
                onConfirm()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                Nearby.getConnectionsClient(context).rejectConnection(endpointId)
                dialog.dismiss()
                onCancel()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }
}