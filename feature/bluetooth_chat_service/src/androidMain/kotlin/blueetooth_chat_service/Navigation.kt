package blueetooth_chat_service

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.startActivityForResult
import blueetooth.BluetoothFactory

class  Controller{
    fun isBluetoothEnableGranted(resultCode:Int)= resultCode==Activity.RESULT_OK
}
@Composable
fun BluetoothChatServiceNavGraph() {
    val controller= remember { Controller() }
    val activity=LocalContext.current as ComponentActivity
    val startActivityIntent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {result->
        println("BluetoothChatServiceNavGraph: ${controller.isBluetoothEnableGranted(result.resultCode)}")
    }
    Column {
        Button(onClick = {
            startActivityIntent.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

        }){
            Text("Launch Bluetooth")
        }
        Button(onClick = {
          BluetoothFactory.bluetoothController.getPairedDevices()
        }){
            Text("Paired Devices")
        }
        Button(onClick = {
            val requestCode = 1
            val discoverableIntent=BluetoothFactory.bluetoothController.createDiscoverableIntent()
            startActivityForResult(activity,discoverableIntent, requestCode,null)
        }){
            Text("Discover")
        }
    }

}