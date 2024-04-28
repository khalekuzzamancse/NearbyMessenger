package peers.ui.route

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun GroupConversationCard(
    modifier: Modifier,
    connectedParticipants: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Groups,
            contentDescription = "Group conversation icon",
            tint = MaterialTheme.colorScheme.primary//Important,since whole group is clickable
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Group Conversation",
                style = MaterialTheme.typography.titleMedium.copy(
                    //using title  because it  not long paragraph or label ....
                    fontWeight = FontWeight.Bold//Telegram App uses bold
                )
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                val text =
                    if (connectedParticipants == 0) "No Active Participants" else "$connectedParticipants Active Participants"
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.typography.labelSmall.color.copy(alpha = 0.5f)
                        ////Telegram App small text with lower alpha for the participants last message color
                    )
                )
            }
        }

    }
}