import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

internal data class TechnologyInfo(
    val technology: Technology,
    val description: String
)

internal enum class Technology {
    WifiDirect, WifiHotspot, Bluetooth, NearByAPI
}

internal val technologyDetails = listOf(
    TechnologyInfo(
        Technology.NearByAPI,
        "Allows communication over short distances without needing device pairing."
    ),
    TechnologyInfo(
        Technology.WifiDirect,
        "Establishes a peer-to-peer connection without needing internet access."
    ),
    TechnologyInfo(
        Technology.WifiHotspot,
        "Turns your device into a hotspot, sharing your internet connection with others."
    ),
    TechnologyInfo(Technology.Bluetooth, "Pairs devices over short distances using low energy."),

)


@Composable
internal fun TechnologyInputDialog(
    onTechnologySelected: (Technology) -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select a Technology", style = MaterialTheme.typography.headlineSmall)
                technologyDetails.forEach { techInfo ->
                    TechnologyCard(techInfo, onTechnologySelected)
                }
            }
        }
    }
}

@Composable
internal fun TechnologyCard(techInfo: TechnologyInfo, onClick: (Technology) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(techInfo.technology) },
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(techInfo.technology.name, style = MaterialTheme.typography.titleMedium)
            Text(techInfo.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
