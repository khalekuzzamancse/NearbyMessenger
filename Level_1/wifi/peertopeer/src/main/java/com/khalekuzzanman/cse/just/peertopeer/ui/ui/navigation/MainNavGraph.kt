package com.khalekuzzanman.cse.just.peertopeer.ui.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen.ConversionScreenPreview
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.devices_list.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(

) {
    val navController: NavHostController = rememberNavController()
    val navigationAction = NavigationActions(navController)

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = "DeviceScreen"
    ) {

        //
        composable(route = "DeviceScreen") {
            NearByDeviceScreen(
                onConversionScreenOpen = {
                    navController.navigate("ConversionScreen")
                }
            )

        }

        composable(route = "ConversionScreen") {
            ConversionScreenPreview(
                onBackArrowClick = {
                    navController.popBackStack()
                    navController.navigate("DeviceScreen")

                }
            )

        }

    }
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: String) {
        try {
            navController.navigate(destination) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } catch (_: Exception) {

        }
    }
}