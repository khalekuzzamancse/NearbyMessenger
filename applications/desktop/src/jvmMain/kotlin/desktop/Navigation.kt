package desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import chatui.ConversionScreenPreview

@Composable
fun RootNavGraph() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog){
        JoinAsDialog(
            onJoinAs = {
                println(it)
                showDialog=false
            },
        )
    }
    else{
        ConversionScreenPreview()
    }

}