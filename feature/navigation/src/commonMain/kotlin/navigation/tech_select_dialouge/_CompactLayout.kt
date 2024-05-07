package navigation.tech_select_dialouge

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun _CompactModeLayout(
    modifier: Modifier = Modifier,
    features: List<Feature>,
    selected: Int?,
    onDetailsRequested: (Int) -> Unit,
    onDetailsCloseRequested: () -> Unit,
    onChatRequest: () -> Unit,
) {

    if (selected == null)//No item selected
    {
        FeatureList(
            modifier = modifier,
            features = features,
            selected = null,
            showUserManualButton = true,
            onUserManualRequest = onDetailsRequested,
        )

    } else //a details request
    {
        TechNUserManual(
            modifier = Modifier, details = features[selected].details,
            onChatRequest = onChatRequest,
            navigationIcon = {
                IconButton(onClick = onDetailsCloseRequested) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Details close")
                }
            }
        )


    }


}