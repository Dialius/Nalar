package com.davinza.nalar.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.auth.AuthState
import com.davinza.nalar.ui.auth.AuthViewModel

@Composable
fun OnboardingHost(role: String = "learner", onFinished: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))

    NavHost(
        navController = navController,
        startDestination = "intro_1",
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) }
    ) {
        composable("intro_1") {
            InfoStepTemplate(
                title = if (role == "learner") "You'll get a little smarter every day — starting now." else "And the Parent of the Year award goes to...",
                subtitle = "",
                imageRes = com.davinza.nalar.R.drawable.mascot_happy,
                onNext = { 
                    if (role == "parent") navController.navigate("intro_2")
                    else navController.navigate("target_choice")
                }
            )
        }

        composable("intro_2") {
            InfoStepTemplate(
                title = "We're here to help you support your child's learning.",
                subtitle = "Track progress, assign lessons, and celebrate their wins together.",
                imageRes = com.davinza.nalar.R.drawable.mascot_studying,
                onNext = { navController.navigate("target_choice") }
            )
        }
        
        composable("target_choice") {
            PillSelectionStepTemplate(
                title = if (role == "learner") "Apa rumpun tes pilihanmu?" else "Apa rumpun tes pilihan anakmu?",
                progress = 0.2f,
                options = listOf("Saintek (Sains & Teknologi)", "Soshum (Sosial & Humaniora)", "Campuran"),
                onNext = { navController.navigate("subject_choice") },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("subject_choice") {
            GridSelectionStepTemplate(
                title = if (role == "learner") "Materi apa yang ingin kamu kuasai lebih dulu?" else "Materi apa yang ingin anakmu kuasai lebih dulu?",
                progress = 0.35f,
                options = listOf(
                    "Penalaran Kuantitatif" to com.davinza.nalar.R.drawable.subject_math_geometric,
                    "Fisika" to com.davinza.nalar.R.drawable.subject_physics_geometric,
                    "Biologi" to com.davinza.nalar.R.drawable.subject_biology_geometric,
                    "Literasi Bahasa" to com.davinza.nalar.R.drawable.subject_language_geometric
                ),
                onNext = { navController.navigate("goal_choice") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("goal_choice") {
            PillSelectionStepTemplate(
                title = if (role == "learner") "What's your top goal?" else "What's your top goal for them?",
                progress = 0.5f,
                options = listOf("Build a strong foundation", "Ace upcoming exams", "Learn something new", "Just for fun"),
                onNext = { navController.navigate("affirmation") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("affirmation") {
            InfoStepTemplate(
                title = if (role == "learner") "A logical choice." else "That adds up.",
                subtitle = "Let's see what we can do.",
                imageRes = com.davinza.nalar.R.drawable.mascot_studying,
                onNext = { navController.navigate("time_commitment") }
            )
        }

        composable("time_commitment") {
            PillSelectionStepTemplate(
                title = if (role == "learner") "How much time do you want to commit?" else "How much time should they commit?",
                progress = 0.65f,
                options = listOf("3 mins a day", "10 mins a day", "15 mins a day", "30 mins a day"),
                onNext = { navController.navigate("routine") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("routine") {
            PillSelectionStepTemplate(
                title = "Let's make it a routine!",
                progress = 0.8f,
                options = listOf("Morning", "Afternoon", "Evening"),
                onNext = { navController.navigate("loading_path") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("loading_path") {
            LoadingPathStep(
                title = if (role == "learner") "Loading your path..." else "Loading their path...",
                onNext = { navController.navigate("path_ready") }
            )
        }

        composable("path_ready") {
            InfoStepTemplate(
                title = if (role == "learner") "Your path is ready!" else "Their path is ready!",
                subtitle = "We've created a personalized learning path to help reach the goal.",
                imageRes = com.davinza.nalar.R.drawable.mascot_happy,
                onNext = { navController.navigate("create_profile_intro") }
            )
        }

        composable("create_profile_intro") {
            InfoStepTemplate(
                title = "Let's create your profile",
                subtitle = "Save your progress and access it from anywhere.",
                imageRes = com.davinza.nalar.R.drawable.mascot_happy,
                onNext = { navController.navigate("step_final") }
            )
        }
        
        composable("step_final") {
            CreateAccountStep(
                viewModel = viewModel,
                onNext = { onFinished() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
fun InfoStepTemplate(
    title: String,
    subtitle: String,
    buttonText: String = "Continue",
    imageRes: Int = com.davinza.nalar.R.drawable.mascot_happy,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(180.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = subtitle,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(text = buttonText, onClick = onNext)
    }
}

@Composable
fun PillSelectionStepTemplate(
    title: String,
    progress: Float,
    options: List<String>,
    buttonText: String = "Continue",
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = progress, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
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
            text = buttonText,
            enabled = selectedOption != null,
            onClick = { selectedOption?.let { onNext(it) } }
        )
    }
}

@Composable
fun LoadingPathStep(title: String, onNext: () -> Unit) {
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
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
fun GridSelectionStepTemplate(
    title: String,
    progress: Float,
    options: List<Pair<String, Int?>>,
    buttonText: String = "Continue",
    onNext: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = progress, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(options) { (text, iconRes) ->
                GridSelectionCard(
                    text = text,
                    iconRes = iconRes,
                    isSelected = selectedOptions.contains(text),
                    onClick = {
                        selectedOptions = if (selectedOptions.contains(text)) {
                            selectedOptions - text
                        } else {
                            selectedOptions + text
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PushableButton(
            text = buttonText,
            enabled = selectedOptions.isNotEmpty(),
            onClick = { onNext(selectedOptions.toList()) }
        )
    }
}

@Composable
fun CreateAccountStep(
    viewModel: AuthViewModel,
    onNext: () -> Unit, 
    onBack: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNext()
        } else if (authState is AuthState.Error) {
            android.widget.Toast.makeText(context, (authState as AuthState.Error).message, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingProgressBar(progress = 0.9f, onBackClick = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create your profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            backgroundColor = Color(0xFFF5F5F5)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            backgroundColor = Color(0xFFF5F5F5)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            backgroundColor = Color(0xFFF5F5F5)
        )

        Spacer(modifier = Modifier.weight(1f))

        PushableButton(
            text = if (authState is AuthState.Loading) "Creating..." else "Create Account",
            isLoading = authState is AuthState.Loading,
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty(),
            onClick = {
                viewModel.register(name, email, password)
            }
        )
    }
}
