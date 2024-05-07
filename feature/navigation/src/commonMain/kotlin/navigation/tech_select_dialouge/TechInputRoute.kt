package navigation.tech_select_dialouge

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * @param onTechSelected will be used,if any item is selected that means it details is opened,
 *@param backHandler will be called when back button is pressed,onBackButtonPress will return true if
 * it is consumed the back press by closing the details,
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TechInputRoute(
    modifier: Modifier = Modifier,
    onTechSelected: (Technology) -> Unit,
    backHandler: @Composable (onBackButtonPress: () -> Boolean) -> Unit,
) {
    val windowSize = calculateWindowSizeClass().widthSizeClass
    val compact = WindowWidthSizeClass.Compact
    val medium = WindowWidthSizeClass.Medium
    val expanded = WindowWidthSizeClass.Expanded
    var selectedTechIndex by remember { mutableStateOf<Int?>(null) }//initially no index selected
    val features = FeatureRepository.getFeatures()
    backHandler {
        val isDetailsOpened = selectedTechIndex != null
        if (isDetailsOpened)
            selectedTechIndex = null
        isDetailsOpened
    }


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