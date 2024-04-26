package kzcse.wifidirect

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class DeviceUUIDManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val uuidKey = "device_uuid"

    val deviceUUID: String
        get() = sharedPreferences.getString(uuidKey, null) ?: generateAndStoreUUID()

    private fun generateAndStoreUUID(): String {
        val newUUID = UUID.randomUUID().toString().replace("-", "").take(32)
        sharedPreferences.edit().putString(uuidKey, newUUID).apply()
        return newUUID
    }
}
