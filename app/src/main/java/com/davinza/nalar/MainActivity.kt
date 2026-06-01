package com.davinza.nalar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.davinza.nalar.ui.auth.SignInScreen
import com.davinza.nalar.ui.auth.SignUpScreen
import com.davinza.nalar.ui.home.HomeScreen
import com.davinza.nalar.ui.onboarding.OnboardingHost
import com.davinza.nalar.ui.splash.SplashScreen
import com.davinza.nalar.ui.welcome.WelcomeScreen
import com.davinza.nalar.ui.quiz.QuizScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.davinza.nalar.data.local.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize UserProgressManager
        com.davinza.nalar.ui.courses.UserProgressManager.initialize(applicationContext)
        
        // Initialize Notification Channels
        com.davinza.nalar.utils.NalarNotificationManager.initialize(applicationContext)

        // Programmatically fetch and log the current FCM Token on startup
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                android.util.Log.d("NalarFCM", "Falar FCM Token: $token")
            } else {
                android.util.Log.w("NalarFCM", "Failed to fetch FCM Token", task.exception)
            }
        }

        // Request runtime notification permission dynamically for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable("splash") {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            val authToken by sessionManager.getAuthToken().collectAsState(initial = null)

            SplashScreen(
                onSplashFinished = {
                    if (authToken != null) {
                        navController.navigate("main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("welcome") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("welcome") {
            WelcomeScreen(
                onLearnerClick = { navController.navigate("onboarding/learner") },
                onParentClick = { navController.navigate("onboarding/parent") },
                onSignInClick = { navController.navigate("signin") }
            )
        }
        composable("signin") {
            SignInScreen(
                onBackClick = { navController.popBackStack() },
                onSignInSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "onboarding/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "learner"
            OnboardingHost(
                role = role,
                onFinished = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            com.davinza.nalar.ui.main.MainScreen(rootNavController = navController)
        }
        composable("settings") {
            com.davinza.nalar.ui.settings.SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) },
                onSignOut = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("settings/profile") {
            com.davinza.nalar.ui.settings.EditProfileScreen(onBackClick = { navController.popBackStack() })
        }
        composable("settings/password") {
            com.davinza.nalar.ui.settings.ChangePasswordScreen(onBackClick = { navController.popBackStack() })
        }
        composable("settings/notifications") {
            com.davinza.nalar.ui.settings.NotificationSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("settings/subscription") {
            com.davinza.nalar.ui.settings.SubscriptionScreen(
                onBackClick = { navController.popBackStack() },
                onUpgradeClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = false }
                    }
                }
            )
        }
        composable("settings/help") {
            com.davinza.nalar.ui.settings.HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }
        composable("settings/terms") {
            com.davinza.nalar.ui.settings.StaticContentScreen(type = "terms", onBackClick = { navController.popBackStack() })
        }
        composable("settings/privacy") {
            com.davinza.nalar.ui.settings.StaticContentScreen(type = "privacy", onBackClick = { navController.popBackStack() })
        }
        composable(
            route = "quiz/{subject}/{unitIndex}/{nodeIndex}",
            arguments = listOf(
                navArgument("subject") { type = NavType.StringType },
                navArgument("unitIndex") { type = NavType.IntType },
                navArgument("nodeIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject") ?: "Math"
            val unitIndex = backStackEntry.arguments?.getInt("unitIndex") ?: 0
            val nodeIndex = backStackEntry.arguments?.getInt("nodeIndex") ?: 0
            QuizScreen(
                subject = subject,
                unitIndex = unitIndex,
                nodeIndex = nodeIndex,
                onCloseClick = { navController.popBackStack() },
                onContinueClick = { navController.popBackStack() }
            )
        }
    }
}