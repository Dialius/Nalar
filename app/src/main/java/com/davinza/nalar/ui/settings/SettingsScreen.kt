package com.davinza.nalar.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.auth.AuthViewModel
import com.davinza.nalar.ui.components.PushableButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))
    var showDialog by remember { mutableStateOf(false) }
    var isSigningOut by remember { mutableStateOf(false) }

    // Konfirmasi dialog Sign Out (Premium Bottom Sheet)
    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = { showDialog = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                BottomSheetDefaults.DragHandle(color = Color(0xFFE5E7EB))
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Icon Bulat Merah Cantik
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚪", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Yakin ingin keluar?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Semua progres belajar dan streak Anda akan tersimpan dengan aman.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                PushableButton(
                    text = if (isSigningOut) "Keluar..." else "Ya, Keluar",
                    isLoading = isSigningOut,
                    onClick = {
                        showDialog = false
                        isSigningOut = true
                        viewModel.signOut()
                        onSignOut()
                    },
                    backgroundColor = Color(0xFFEF4444),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                PushableButton(
                    text = "Batal",
                    onClick = { showDialog = false },
                    backgroundColor = Color.White,
                    textColor = Color(0xFF111827),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        com.davinza.nalar.ui.components.AppTopBar(
            title = "Pengaturan",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            },
            actions = {}
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Akun",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SettingsRow("Edit Profile") { onNavigate("settings/profile") }
            SettingsRow("Change Password") { onNavigate("settings/password") }
            SettingsRow("Notifications") { onNavigate("settings/notifications") }
            SettingsRow("Subscription") { onNavigate("settings/subscription") }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Dukungan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SettingsRow("Help Center") { onNavigate("settings/help") }
            SettingsRow("Terms of Service") { onNavigate("settings/terms") }
            SettingsRow("Privacy Policy") { onNavigate("settings/privacy") }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PushableButton(
                text = if (isSigningOut) "Signing out..." else "Sign Out",
                isLoading = isSigningOut,
                onClick = { showDialog = true },
                backgroundColor = Color(0xFFEF4444),
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
}
