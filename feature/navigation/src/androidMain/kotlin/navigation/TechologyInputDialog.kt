package navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

internal data class TechnologyInfo(
    val technology: Technology,
    val description: String
)


internal val technologyDetails = listOf(
    TechnologyInfo(
        Technology.NearByAPI,
        "Uses combination of Bluetooth,BLE and Wi-Fi technologies"
    ),
    TechnologyInfo(
        Technology.WifiDirect,
        "Uses Wifi-Direct technology.Data can be transfer faster"
    ),
    TechnologyInfo(
        Technology.WifiHotspot,
        "Uses Wifi technology.Slower than Wifi-Direct"
    ),
    TechnologyInfo(
        Technology.Bluetooth,
        "Uses Blueetooth and BLE"
    ),

    )


@Composable
fun TechnologyInputDialog(
    onTechnologySelected: (Technology) -> Unit
) {
    Dialog(
        onDismissRequest = {},
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics {
                    contentDescription = "Tech input dialog"
                }
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
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
