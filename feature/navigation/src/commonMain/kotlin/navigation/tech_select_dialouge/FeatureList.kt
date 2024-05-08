package navigation.tech_select_dialouge

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @param usedTechNames use the name of the Technology used up to 8 words
 */
internal data class Feature(
    val name: String,
    val icon: ImageVector,
    val usedTechNames: String,
    val details: Details
)

/**
 * @param techUsed give the description of bullet point which tech used and what their capability
 * @param userManual mention the feature and user guidelines,mention the necessary bug
 */
internal data class Details(
    val techUsed: List<AnnotatedString>,
    val userManual:  List<AnnotatedString>
)

@Composable
internal fun FeatureList(
    modifier: Modifier = Modifier,
    selected: Int?,
    features: List<Feature>,
    showUserManualButton: Boolean,
    onUserManualRequest: (index: Int) -> Unit,
) {
    /*
    Instead of lazy column,using Column so that can use IntrinsicSize.Max for width
     */
  Column(
        modifier = Modifier
            .padding(16.dp)
            .width(IntrinsicSize.Max)
            .verticalScroll(rememberScrollState())
    ) {
      Text(
          text = "Tap on a technology to start your chat session",
          fontSize = 18.sp,
      )
      features.forEachIndexed{ index, feature ->
            FeatureCard(
                feature = feature,
                selected = selected == index,
                onClick = { onUserManualRequest(index) },
                onUserManualClick = { onUserManualRequest(index) }
            )
        }
    }
}

@Composable
internal fun FeatureCard(
    feature: Feature,
    selected: Boolean,
    onClick: () -> Unit,
    onUserManualClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }.then(
                if (selected) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.name,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary//Important:Since clickable

            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feature.usedTechNames,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )

            }

        }
    }
}
