package com.davinza.nalar.ui.settings

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.auth.AuthViewModel
import com.davinza.nalar.ui.auth.ChangePasswordState
import com.davinza.nalar.ui.auth.UpdateProfileState
import com.davinza.nalar.ui.components.AppTopBar
import com.davinza.nalar.ui.components.CustomTextField
import com.davinza.nalar.ui.components.PushableButton
import com.davinza.nalar.ui.courses.UserProgressManager
import com.davinza.nalar.ui.profile.ProfileState
import com.davinza.nalar.ui.profile.ProfileViewModel

import com.davinza.nalar.ui.components.NalarAvatar

// ──────────────────────────────────────────────────────────
// 1. EDIT PROFILE SCREEN
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))
    val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(context))

    val profileState by profileViewModel.profileState.collectAsState()
    val updateState by authViewModel.updateProfileState.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf("🦊") }
    var hasInitialized by remember { mutableStateOf(false) }

    val avatars = listOf("🦉", "🦊", "🐨", "🦁", "🐱", "🐼", "👨‍🚀", "😎", "🎓")

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success && !hasInitialized) {
            val user = (profileState as ProfileState.Success).userProfile.user
            name = user.name
            selectedAvatar = user.avatar_url ?: "🦊"
            hasInitialized = true
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UpdateProfileState.Success) {
            Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            authViewModel.resetUpdateProfileState()
            profileViewModel.fetchProfile() // Refresh profile state
            onBackClick()
        } else if (updateState is UpdateProfileState.Error) {
            Toast.makeText(context, (updateState as UpdateProfileState.Error).message, Toast.LENGTH_LONG).show()
            authViewModel.resetUpdateProfileState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Edit Profil",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        if (profileState is ProfileState.Loading && !hasInitialized) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Avatar Bulat Besar
                    NalarAvatar(
                        avatarUrl = selectedAvatar,
                        size = 100.dp,
                        modifier = Modifier
                            .border(3.dp, Color(0xFF4CAF50), CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pilih Avatar Khas Nalar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grid Pilihan Avatar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        avatars.forEach { avatar ->
                            val isSelected = selectedAvatar == avatar
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF3F4F6))
                                    .border(
                                        2.dp,
                                        if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatar = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                NalarAvatar(avatarUrl = avatar, size = 52.dp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Input Nama Lengkap
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nama Lengkap",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        CustomTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Masukkan nama Anda",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Field Email (Read Only)
                    val emailText = if (profileState is ProfileState.Success) {
                        (profileState as ProfileState.Success).userProfile.user.email
                    } else {
                        "..."
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Email (Tidak dapat diubah)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = emailText,
                            onValueChange = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Terkunci", tint = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFF3F4F6),
                                disabledTextColor = Color.Gray,
                                disabledContainerColor = Color(0xFFF9FAFB)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Tombol Simpan
                    val isSaving = updateState is UpdateProfileState.Loading
                    PushableButton(
                        text = if (isSaving) "Menyimpan..." else "Simpan Perubahan",
                        isLoading = isSaving,
                        enabled = name.isNotBlank(),
                        onClick = {
                            authViewModel.updateProfile(name, selectedAvatar)
                        },
                        backgroundColor = Color(0xFF000000),
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// 2. CHANGE PASSWORD SCREEN
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))
    val updateState by authViewModel.changePasswordState.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(updateState) {
        if (updateState is ChangePasswordState.Success) {
            Toast.makeText(context, "Password berhasil diubah!", Toast.LENGTH_SHORT).show()
            authViewModel.resetChangePasswordState()
            onBackClick()
        } else if (updateState is ChangePasswordState.Error) {
            errorMessage = (updateState as ChangePasswordState.Error).message
            authViewModel.resetChangePasswordState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Ganti Password",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password Saat Ini",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = oldPassword,
                        onValueChange = {
                            oldPassword = it
                            errorMessage = null
                        },
                        label = "Masukkan password saat ini",
                        isPassword = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password Baru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            errorMessage = null
                        },
                        label = "Masukkan password baru",
                        isPassword = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Konfirmasi Password Baru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        label = "Ulangi password baru",
                        isPassword = true
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                val isSaving = updateState is ChangePasswordState.Loading
                PushableButton(
                    text = if (isSaving) "Mengubah..." else "Simpan Password Baru",
                    isLoading = isSaving,
                    enabled = oldPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmPassword,
                    onClick = {
                        if (newPassword != confirmPassword) {
                            errorMessage = "Konfirmasi password baru tidak cocok."
                            return@PushableButton
                        }
                        authViewModel.changePassword(oldPassword, newPassword)
                    },
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
// 3. NOTIFICATION SETTINGS SCREEN (LOCAL PREFS)
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val manager = com.davinza.nalar.utils.NalarNotificationManager

    var streakNotification by remember {
        mutableStateOf(manager.isEnabled(context, manager.CHANNEL_STREAK))
    }
    var rankNotification by remember {
        mutableStateOf(manager.isEnabled(context, manager.CHANNEL_RANK))
    }
    var keyNotification by remember {
        mutableStateOf(manager.isEnabled(context, manager.CHANNEL_KEY))
    }

    var manualNotificationEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Notifikasi",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pengaturan Pengingat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NotificationToggleRow(
                title = "Pengingat Harian",
                description = "Ingatkan saya belajar setiap hari agar streak tetap menyala.",
                checked = streakNotification,
                onCheckedChange = { enabled ->
                    streakNotification = enabled
                    manager.setEnabled(context, manager.CHANNEL_STREAK, enabled)
                    if (enabled) {
                        manager.showNotification(
                            context = context,
                            title = "Nalar - Pengingat Harian Aktif! 🔥",
                            message = "Bagus! Kami akan mengingatkanmu belajar setiap hari agar streak belajarmu tetap menyala!",
                            channelId = manager.CHANNEL_STREAK
                        )
                    }
                }
            )

            NotificationToggleRow(
                title = "Update Peringkat (Liga)",
                description = "Beri tahu saya ketika posisi peringkat liga saya berubah.",
                checked = rankNotification,
                onCheckedChange = { enabled ->
                    rankNotification = enabled
                    manager.setEnabled(context, manager.CHANNEL_RANK, enabled)
                    if (enabled) {
                        manager.showNotification(
                            context = context,
                            title = "Nalar - Info Liga & Peringkat! 🏆",
                            message = "Pemberitahuan aktif! Kami akan kabari jika posisi belajarmu disalip lawan di leaderboard!",
                            channelId = manager.CHANNEL_RANK
                        )
                    }
                }
            )

            NotificationToggleRow(
                title = "Notifikasi Isi Kunci",
                description = "Ingatkan saya secara real-time saat kuota Kunci belajar saya terisi penuh.",
                checked = keyNotification,
                onCheckedChange = { enabled ->
                    keyNotification = enabled
                    manager.setEnabled(context, manager.CHANNEL_KEY, enabled)
                    if (enabled) {
                        manager.showNotification(
                            context = context,
                            title = "Nalar - Notifikasi Kunci Aktif! 🔑",
                            message = "Hebat! Kamu akan mendapat notifikasi instan begitu Kunci belajarmu terisi penuh!",
                            channelId = manager.CHANNEL_KEY
                        )
                    }
                }
            )

            NotificationToggleRow(
                title = "Notifikasi Manual",
                description = "Aktifkan untuk membuka opsi uji coba pengiriman notifikasi penyemangat secara manual.",
                checked = manualNotificationEnabled,
                onCheckedChange = { enabled ->
                    manualNotificationEnabled = enabled
                }
            )

            AnimatedVisibility(
                visible = manualNotificationEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PushableButton(
                        text = "Kirim Notifikasi Uji Coba 🚀",
                        onClick = {
                            manager.showNotification(
                                context = context,
                                title = "🦉 Halo Scholar! Ini Notifikasi Uji Coba",
                                message = "Luar biasa! Fitur notifikasi manual Nalar berfungsi dengan baik. Yuk, lanjut belajar dan raih mimpimu! 🧠🚀",
                                channelId = manager.CHANNEL_RANK
                            )
                        },
                        backgroundColor = Color(0xFF194BDF),
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Klik tombol di atas untuk memicu notifikasi secara instan.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF194BDF),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE5E7EB)
            )
        )
    }
    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
}

// ──────────────────────────────────────────────────────────
// 4. SUBSCRIPTION SCREEN
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(onBackClick: () -> Unit, onUpgradeClick: () -> Unit) {
    val isPremium = UserProgressManager.isPremium

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Langganan",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isPremium) {
                // PREMIUM ACTIVE UI (GOLDEN PREMIUM THEME)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Nalar Premium",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Status: Aktif Selamanya (Lifetime)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Terima kasih telah mendukung kami!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Akses Premium Anda",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                PremiumBenefitItem("Kunci belajar tak terbatas (∞) tanpa batas pengerjaan.")
                PremiumBenefitItem("Akses ke semua kategori materi kognitif.")
                PremiumBenefitItem("Tanda lencana eksklusif Premium Scholar di profil.")
                PremiumBenefitItem("Prioritas dukungan bantuan tercepat.")
            } else {
                // FREE USER UI (SILVER CARD + UPGRADE BUTTON)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(1.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Nalar Scholar (Free)",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Buka semua fitur cerdas seumur hidup sekarang juga.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                PushableButton(
                    text = "👑 Upgrade ke Premium",
                    onClick = onUpgradeClick,
                    backgroundColor = Color(0xFFF59E0B),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PremiumBenefitItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFFEF3C7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

// ──────────────────────────────────────────────────────────
// 5. HELP CENTER SCREEN (EXPANDABLE FAQS)
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    val faqs = listOf(
        FAQItem(
            "Apa itu Nalar?",
            "Nalar adalah aplikasi kognitif modern yang dirancang untuk mengasah kemampuan berpikir logis, pemecahan masalah (problem solving), serta penalaran analitis secara terstruktur dan interaktif."
        ),
        FAQItem(
            "Bagaimana cara mendapatkan Kunci?",
            "Pengguna non-premium akan menerima kuota 2 kunci gratis secara otomatis setiap hari pada pukul 00:00. Pengerjaan soal yang sudah selesai dikerjakan tidak akan mengurangi kunci Anda."
        ),
        FAQItem(
            "Apakah ada batas belajar untuk Premium?",
            "Tidak! Pengguna Premium mendapatkan kuota kunci tak terbatas (∞), yang berarti Anda bebas belajar, mengerjakan materi apa saja, kapan saja tanpa batasan."
        ),
        FAQItem(
            "Bagaimana dengan skema berlangganan?",
            "Nalar Premium menggunakan metode pembelian satu kali (One-Time Purchase) untuk akses selamanya (Lifetime). Tidak ada biaya berulang bulanan maupun tersembunyi."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Pusat Bantuan",
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Pertanyaan Umum (FAQ)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(faqs) { faq ->
                FAQExpandableRow(faq)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Butuh bantuan lebih lanjut?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Kirim pesan kepada tim dukungan teknis kami melalui email support kami di:",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFF194BDF))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "support@davinza.com",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF194BDF)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

data class FAQItem(val question: String, val answer: String)

@Composable
fun FAQExpandableRow(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = faq.question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.rotate(rotationState)
            )
        }
        
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = faq.answer,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
    }
}

// ──────────────────────────────────────────────────────────
// 6. STATIC LEGAL CONTENT SCREEN (TERMS & PRIVACY)
// ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticContentScreen(type: String, onBackClick: () -> Unit) {
    val title = if (type == "terms") "Syarat Layanan" else "Kebijakan Privasi"
    val content = if (type == "terms") {
        """
        Selamat datang di Nalar. Dengan mengunduh dan menggunakan aplikasi kami, Anda menyetujui ketentuan berikut:
        
        1. Ketentuan Penggunaan Akun
        Anda setuju untuk menggunakan aplikasi ini untuk keperluan belajar kognitif secara jujur. Segala bentuk manipulasi data/skor tidak diperkenankan.
        
        2. Status Pembelian Premium
        Pembelian Nalar Premium bersifat final, permanen (Lifetime), dan tidak dapat dipindahtangankan ke pengguna lain di luar akun terdaftar Anda.
        
        3. Batasan Hak Cipta
        Seluruh materi logika, soal, desain aset, dan kode sumber adalah hak milik eksklusif Nalar. Penggandaan konten tanpa izin tertulis dilarang keras.
        
        Ketentuan ini diperbarui per tanggal 30 Mei 2026.
        """.trimIndent()
    } else {
        """
        Keamanan data privasi Anda adalah prioritas utama kami. Berikut adalah ringkasan kebijakan pengumpulan data kami:
        
        1. Pengumpulan Informasi
        Kami mengumpulkan data akun dasar Anda (Nama, Email, dan URL Avatar) saat pendaftaran manual atau integrasi masuk menggunakan Google Sign-In.
        
        2. Data Kemajuan Belajar
        Progres belajar, skor latihan, streak harian, dan kepemilikan kunci diunggah ke server database cloud kami agar data Anda tetap aman saat berganti perangkat.
        
        3. Keamanan Informasi
        Kami mengenkripsi pertukaran data API dan password Anda dengan teknologi terkini demi mencegah akses pihak ketiga yang tidak berwenang.
        
        Kebijakan ini diperbarui per tanggal 30 Mei 2026.
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = title,
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            item {
                Text(
                    text = content,
                    fontSize = 15.sp,
                    color = Color.DarkGray,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
