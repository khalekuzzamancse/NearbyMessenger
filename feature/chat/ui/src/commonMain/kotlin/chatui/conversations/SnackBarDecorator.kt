package chatui.conversations
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember


@Composable
 fun SnackBarDecorator(
    message: String?,
    content: @Composable (PaddingValues) -> Unit
) {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        if (message != null)
            hostState.showSnackbar(message = message, duration = SnackbarDuration.Long)
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState)
        }
    ) { scaffoldPadding ->
        content(scaffoldPadding)
    }
}

