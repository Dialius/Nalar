package com.davinza.nalar.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davinza.nalar.ui.components.*
import kotlinx.coroutines.delay

@Composable
fun OnboardingHost(onFinished: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "info",
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
    ) {
        composable("info") {
            InfoStep(
                onNext = { navController.navigate("target_choice") }
            )
        }
        composable("target_choice") {
            TargetChoiceStep(
                onNext = { navController.navigate("subject_choice") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("subject_choice") {
            SubjectChoiceStep(
                onNext = { navController.navigate("email_input") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("email_input") {
            EmailInputStep(
                onNext = { navController.navigate("password_input") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("password_input") {
            PasswordInputStep(
                onNext = { navController.navigate("loading_path") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("loading_path") {
            LoadingPathStep(
                onNext = { navController.navigate("premium_upsell") }
            )
        }
        composable("premium_upsell") {
            PremiumUpsellStep(
                onNext = { onFinished() }
            )
        }
    }
}

@Composable
fun InfoStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Mascot Placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Text("Mascot 3D", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "You'll get a little smarter every day — starting now.",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(text = "Continue", onClick = onNext)
    }
}

@Composable
fun TargetChoiceStep(onNext: () -> Unit, onBack: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val options = listOf("Saintek (Sains & Teknologi)", "Soshum (Sosial & Humaniora)", "Campuran")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = 0.2f, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Apa rumpun tes pilihanmu?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            SelectionPill(
                text = option,
                isSelected = selectedOption == option,
                onClick = { selectedOption = option }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = "Continue",
            enabled = selectedOption != null,
            onClick = onNext
        )
    }
}

@Composable
fun SubjectChoiceStep(onNext: () -> Unit, onBack: () -> Unit) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val options = listOf(
        "Penalaran Kuantitatif",
        "Fisika",
        "Biologi",
        "Literasi Bahasa Inggris"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = 0.4f, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Materi apa yang ingin kamu kuasai lebih dulu?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            SelectionPill(
                text = option,
                isSelected = selectedOption == option,
                onClick = { selectedOption = option }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = "Continue",
            enabled = selectedOption != null,
            onClick = onNext
        )
    }
}

@Composable
fun EmailInputStep(onNext: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = 0.6f, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        // Mascot Placeholder small
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF4CAF50))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What's your email?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            backgroundColor = Color(0xFFFFF9C4) // Yellowish bg like reference
        )

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = "Continue",
            enabled = email.isNotEmpty() && email.contains("@"),
            onClick = onNext
        )
    }
}

@Composable
fun PasswordInputStep(onNext: () -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = 0.8f, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        // Mascot Placeholder small
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF4CAF50))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create your password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "By clicking create profile, I agree to Nalar's Terms and Privacy policy.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = "Create profile",
            enabled = password.length >= 6,
            onClick = onNext
        )
    }
}

@Composable
fun LoadingPathStep(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500) // Simulate loading
        onNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularGradientSpinner()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Loading your learning path recommendations",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
fun PremiumUpsellStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Reach your goals faster with Premium",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Comparison Table Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text("Free vs Premium Table Component", color = Color.Gray)
        }

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = "Learn more",
            onClick = onNext
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNext) {
            Text("Skip for now", color = Color.Gray)
        }
    }
}
