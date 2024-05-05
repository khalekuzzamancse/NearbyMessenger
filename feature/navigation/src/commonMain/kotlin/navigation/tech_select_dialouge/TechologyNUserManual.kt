package navigation.tech_select_dialouge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TechNUserManual(
    modifier: Modifier = Modifier,
    details: Details,
    navigationIcon: (@Composable () -> Unit)?=null,
    onChatRequest: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            StartChatButton(onChatRequest)
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (navigationIcon != null) {
                        navigationIcon()
                    }
                },
                title = {

                }

            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues).verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TechnologyOverview(overview = details.techUsed)
            UserGuidelines(userGuide = details.userManual)
        }
    }

}

@Composable
fun TechnologyOverview(
    modifier: Modifier = Modifier,
    overview: List<AnnotatedString>,
) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Technology Used", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        val bullet = "•"
        overview.forEach { line ->
            Text(
                text = "$bullet $line",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }

    }

}

@Composable
fun UserGuidelines(
    modifier: Modifier = Modifier,
    userGuide: List<AnnotatedString>,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start //so that each line of text begin from start
    ) {
        Text("User Guidelines", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        val bullet = "•"
        userGuide.forEach { line ->
            Text(
                text = "$bullet $line",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }

    }

}

@Composable
internal fun StartChatButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Start Chat")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Chat")
        }
    }
}
