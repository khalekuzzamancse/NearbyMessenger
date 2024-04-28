package kzcse.wifidirect.deviceInfo

import android.content.Context
import android.content.SharedPreferences

class UserNameManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val nameKey = "user_name"

    var userName: String
        get() = sharedPreferences.getString(nameKey, "") ?: ""
        set(value) {
            sharedPreferences.edit().putString(nameKey, value).apply()
        }
}