package com.davinza.nalar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.R
import com.davinza.nalar.ui.components.CustomTextField
import com.davinza.nalar.ui.components.PushableButton
import com.davinza.nalar.ui.components.PushableIconButton
import com.davinza.nalar.ui.components.PushableSocialButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.davinza.nalar.data.local.SessionManager
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onBackClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))
    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var googleLoginError by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    
    // Web Client ID asli dari Firebase Console Anda
    val webClientId = "488734978021-q2gt4t3gjjli2tgf8ebp3ks72l9c5ug7.apps.googleusercontent.com"

    // Google Sign-In Setup
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            android.util.Log.d("NALAR_DEBUG", "Google ID Token: ${account.idToken}")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        account.idToken?.let { idToken ->
                            viewModel.googleLogin(idToken)
                        } ?: run {
                            googleLoginError = "Failed to obtain Google ID Token"
                        }
                    } else {
                        googleLoginError = authTask.exception?.message ?: "Firebase Auth failed"
                    }
                }
        } catch (e: Exception) {
            googleLoginError = e.message ?: "Google Sign-In failed"
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            com.davinza.nalar.ui.courses.UserProgressManager.initialize(context)
            onSignInSuccess()
        } else if (authState is AuthState.Error) {
            googleLoginError = (authState as AuthState.Error).message
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        
        // Premium 3D Google Social Sign-In Button
        PushableSocialButton(
            text = "Continue with Google",
            iconRes = R.drawable.ic_google,
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White,
            textColor = Color.Black
        )
        
        if (googleLoginError != null) {
            Text(
                text = googleLoginError ?: "",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // OR Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
            Text(
                text = "OR",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Input Fields
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Sign In Button
        PushableButton(
            text = if (authState is AuthState.Loading) "Signing in..." else "Sign in",
            isLoading = authState is AuthState.Loading,
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            backgroundColor = Color(0xFFF5F5F5),
            textColor = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Forgot Password
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = { /* TODO: Forgot password */ }) {
                Text(
                    text = "Forgot password?",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Sign Up Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "No account yet? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            TextButton(
                onClick = onSignUpClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Sign up",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

