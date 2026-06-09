package com.bitaxeballer.mobile

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bitaxeballer.mobile.data.DeviceRepository
import com.bitaxeballer.mobile.ui.BitaxeBallerTheme
import com.bitaxeballer.mobile.ui.DeviceDetailScreen
import com.bitaxeballer.mobile.ui.HomeScreen

@Composable
fun BitaxeBallerApp() {
    val navController = rememberNavController()
    val repository = remember { DeviceRepository() }

    BitaxeBallerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        repository = repository,
                        onOpenDevice = { ip -> navController.navigate("device/$ip") }
                    )
                }
                composable(
                    route = "device/{ip}",
                    arguments = listOf(navArgument("ip") { type = NavType.StringType })
                ) { backStackEntry ->
                    val ip = backStackEntry.arguments?.getString("ip").orEmpty()
                    DeviceDetailScreen(
                        ip = ip,
                        repository = repository,
                        viewModelStoreOwner = backStackEntry,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
