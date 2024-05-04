package navigation.tech_select_dialouge

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TechInputRoute(
    modifier: Modifier = Modifier,
    onTechSelected: (Technology) -> Unit = {},
) {
    val windowSize = calculateWindowSizeClass().widthSizeClass
    val compact = WindowWidthSizeClass.Compact
    val medium = WindowWidthSizeClass.Medium
    val expanded = WindowWidthSizeClass.Expanded
    var selectedTechIndex by remember { mutableStateOf<Int?>(null) }//initially no index selected
    val features = FeatureRepository.getFeatures()
    AnimatedContent(
        modifier = Modifier,
        targetState = windowSize
    ) { window ->
        when (window) {
            compact -> {
                _CompactModeLayout(
                    features = features,
                    selected = selectedTechIndex,
                    onDetailsRequested = {
                        selectedTechIndex = it
                    },
                    onDetailsCloseRequested = {
                        selectedTechIndex = null
                    },
                    onChatRequest = {
                        onTechSelected(selectedTechIndex._toTechnologySelected())
                    }
                )
            }

            medium, expanded -> {
                _NonCompactLayout(
                    features = features,
                    selected = selectedTechIndex,
                    onDetailsRequested = {
                        selectedTechIndex = it
                    },
                    onChatRequest = {
                        onTechSelected(selectedTechIndex._toTechnologySelected())
                    }
                )
            }

        }
    }

}

private fun Int?._toTechnologySelected() = when (this) {
    0 -> Technology.NearByAPI
    1 -> Technology.WifiDirect
    2 -> Technology.WifiHotspot
    3 -> Technology.Bluetooth
    else -> Technology.NearByAPI //when nothing selected

}