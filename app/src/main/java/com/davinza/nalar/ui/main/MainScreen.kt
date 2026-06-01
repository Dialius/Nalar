package com.davinza.nalar.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.davinza.nalar.ui.home.HomeScreen
import com.davinza.nalar.ui.courses.CoursesScreen
import com.davinza.nalar.ui.premium.PremiumScreen
import com.davinza.nalar.ui.premium.PaymentMethodScreen
import com.davinza.nalar.ui.premium.PaymentInstructionScreen
import com.davinza.nalar.ui.profile.ProfileScreen
import com.davinza.nalar.ui.leaderboard.LeaderboardScreen
import com.davinza.nalar.ui.premium.PremiumSuccessScreen

import com.davinza.nalar.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image

private val ColorSecondary = Color(0xFF194BDF)
private val ColorOutlineVariant = Color(0xFFE2E2E2)

sealed class BottomNavItem(var title: String, var iconRes: Int, var screen_route: String) {
    object Home : BottomNavItem("Home", R.drawable.ic_home_page, "home")
    object Courses : BottomNavItem("Courses", R.drawable.ic_list, "courses")
    object Leaderboard : BottomNavItem("Ranks", R.drawable.ic_rank, "leaderboard")
    object Premium : BottomNavItem("Premium", R.drawable.ic_star, "premium")
    object You : BottomNavItem("You", R.drawable.ic_profile, "you")
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(), // Ensures all screens start below the status bar seamlessly
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.screen_route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.screen_route) {
                HomeScreen(onStartClick = {
                    navController.navigate(BottomNavItem.Courses.screen_route) {
                        popUpTo(BottomNavItem.Home.screen_route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
            composable(BottomNavItem.Courses.screen_route) {
                CoursesScreen(onNavigateToQuiz = { subject, unitIndex, nodeIndex ->
                    rootNavController.navigate("quiz/$subject/$unitIndex/$nodeIndex")
                })
            }
            composable(BottomNavItem.Leaderboard.screen_route) {
                LeaderboardScreen()
            }
            composable(BottomNavItem.Premium.screen_route) {
                PremiumScreen(
                    onNavigateToPaymentMethod = {
                        navController.navigate("payment_method")
                    }
                )
            }
            composable("payment_method") {
                PaymentMethodScreen(
                    onBack = { navController.popBackStack() },
                    onPaymentSuccess = { bank, vaNumber, orderId ->
                        navController.navigate("payment_instruction/$bank/$vaNumber/$orderId")
                    }
                )
            }
            composable(
                route = "payment_instruction/{bank}/{vaNumber}/{orderId}",
                arguments = listOf(
                    navArgument("bank") { type = NavType.StringType },
                    navArgument("vaNumber") { type = NavType.StringType },
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bank = backStackEntry.arguments?.getString("bank") ?: ""
                val vaNumber = backStackEntry.arguments?.getString("vaNumber") ?: ""
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                PaymentInstructionScreen(
                    bank = bank,
                    vaNumber = vaNumber,
                    orderId = orderId,
                    onPaymentSuccess = {
                        navController.navigate("premium_success") {
                            popUpTo(BottomNavItem.Premium.screen_route) { inclusive = false }
                        }
                    },
                    onPaymentExpired = {
                        navController.navigate(BottomNavItem.Home.screen_route) {
                            popUpTo(0)
                        }
                    },
                    onBackToHome = {
                        navController.navigate(BottomNavItem.Home.screen_route) {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable("premium_success") {
                PremiumSuccessScreen(
                    onMulaiBelajar = {
                        navController.navigate(BottomNavItem.Courses.screen_route) {
                            popUpTo(0)
                        }
                    }
                )
            }
            composable(BottomNavItem.You.screen_route) {
                ProfileScreen(onNavigateToSettings = {
                    rootNavController.navigate("settings")
                })
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Courses,
        BottomNavItem.Leaderboard,
        BottomNavItem.Premium,
        BottomNavItem.You
    )
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp)
            .border(width = 1.dp, color = ColorOutlineVariant)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.screen_route
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(if (isSelected) 1f else 0.45f)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) ColorSecondary else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // Removes the basic Material 3 purple pill background
                ),
                onClick = {
                    navController.navigate(item.screen_route) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
