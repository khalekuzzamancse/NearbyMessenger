package chatui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

//
//
//@Suppress("Unused")
//
//@Composable
//fun MessagePreview() {
//    Column(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxSize()
//    ) {
//        Message(
//            senderName = "Md Abdul Kalam",
//            message = "It looks ?",
//            timeStamp = "11:20 AM",
//            shape = RoundedCornerShape(
//                topStart = 8.dp,
//                topEnd = 8.dp,
//                bottomStart = 8.dp,
//                bottomEnd = 16.dp
//            ),
//            alignment = Alignment.End,
//            backgroundColor = MaterialTheme.colorScheme.tertiary
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Message(
//            senderName = null,
//            message = "It looks like you have",
//            timeStamp = "11:20 AM",
//            shape = RoundedCornerShape(
//                topStart = 8.dp,
//                topEnd = 16.dp,
//                bottomStart = 16.dp,
//                bottomEnd = 16.dp
//            ),
//            alignment = Alignment.Start,
//            backgroundColor = MaterialTheme.colorScheme.secondaryContainer
//        )
//
//    }
//
//}

/**
 * @param senderName ,if device is sender then null
 */
@Composable
fun ColumnScope.Message(
    modifier: Modifier=Modifier,
    senderName:String?,
    message: String,
    timeStamp: String,
    shape: Shape,
    alignment: Alignment.Horizontal,
    backgroundColor: Color
) {
  Column (modifier.align(alignment)){
      Text(text = senderName ?: "Me", style = MaterialTheme.typography.bodyMedium,)
      Surface(
          shape = shape,
          modifier =Modifier,
          shadowElevation = 1.dp,
          color = backgroundColor
      ) {
          Column(modifier = Modifier.padding(12.dp)) {
              Text(
                  text = message,
                  style = MaterialTheme.typography.bodyLarge,
              )
              Text(
                  text = timeStamp,
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier.align(Alignment.End).alpha(0.5f) // Set the alpha value (0.5f for 50% transparency)
              )
          }

      }
  }

}


