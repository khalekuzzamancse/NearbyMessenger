package navigation.tech_select_dialouge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
internal fun _NonCompactLayout(
    modifier: Modifier = Modifier,
    features: List<Feature>,
    selected: Int?,
    onChatRequest:()->Unit,
    onDetailsRequested: (Int) -> Unit,
) {

    if (selected == null)//No item selected
    {
        _SinglePaneLayout(
            features = features,
            selected = null,
            onDetailsRequested = onDetailsRequested
        )

    } else {
        _TwoPanesLayout(
            features = features,
            selected = selected,
            onDetailsRequested = onDetailsRequested,
            onChatRequest =onChatRequest
        )
    }


}

@Composable
private fun _SinglePaneLayout(
    modifier: Modifier = Modifier,
    features: List<Feature>,
    selected: Int?,
    onDetailsRequested: (Int) -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        FeatureList(
            modifier = Modifier.wrapContentWidth(),
            features = features,
            selected = null,//No item selected,
            showUserManualButton = true,
            onUserManualRequest = onDetailsRequested
        )
    }

}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun _TwoPanesLayout(
    modifier: Modifier = Modifier,
    features: List<Feature>,
    selected: Int,
    onChatRequest:()->Unit,
    onDetailsRequested: (Int) -> Unit,
) {
    val windowSize = calculateWindowSizeClass().widthSizeClass
    val featureSectionWeight =
        if (windowSize == WindowWidthSizeClass.Expanded) 0.35f else 0.5f//On medium take 50%,on Expanded takes 35%
    Row(modifier = modifier) {
        Box(Modifier.weight(featureSectionWeight)) {
            FeatureList(
                features = features,
                selected = selected,
                showUserManualButton = false,
                onUserManualRequest = onDetailsRequested
            )
        }
        Box(Modifier.weight(1f - featureSectionWeight)) {
            TechNUserManual(
                modifier = Modifier,
                details = features[selected].details,
                onChatRequest =onChatRequest
            )
        }

    }
}

