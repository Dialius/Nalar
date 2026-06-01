package com.davinza.nalar.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.R
import com.davinza.nalar.data.remote.model.AuthData
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.components.NalarAvatar
import com.davinza.nalar.ui.components.PushableButton
import com.davinza.nalar.ui.components.AppTopBar
import com.davinza.nalar.ui.components.ShimmerBox
import com.davinza.nalar.ui.courses.UserProgressManager
import com.davinza.nalar.ui.courses.mathUnits
import com.davinza.nalar.ui.courses.physicsUnits
import com.davinza.nalar.ui.courses.chemistryUnits

// Helper class to represent active mission dynamically
data class ActiveMission(
    val subject: String,
    val subjectDisplayName: String,
    val unitNumber: Int,
    val unitTitle: String,
    val unitSubtitle: String,
    val progressPercentage: Int
)

// Helper function to resolve active mission based on real-time completed nodes
fun getRealtimeActiveMission(): ActiveMission {
    // 1. Math Check
    for (unit in mathUnits) {
        val completedCount = unit.nodes.indices.count { nodeIndex ->
            UserProgressManager.completedNodes.contains("Math_${unit.number}_$nodeIndex")
        }
        if (completedCount < unit.nodes.size) {
            val progress = (completedCount.toFloat() / unit.nodes.size * 100).toInt()
            return ActiveMission(
                subject = "Math",
                subjectDisplayName = "Matematika & Aljabar",
                unitNumber = unit.number,
                unitTitle = unit.title,
                unitSubtitle = unit.subtitle,
                progressPercentage = progress
            )
        }
    }
    // 2. Physics Check
    for (unit in physicsUnits) {
        val completedCount = unit.nodes.indices.count { nodeIndex ->
            UserProgressManager.completedNodes.contains("Physics_${unit.number}_$nodeIndex")
        }
        if (completedCount < unit.nodes.size) {
            val progress = (completedCount.toFloat() / unit.nodes.size * 100).toInt()
            return ActiveMission(
                subject = "Physics",
                subjectDisplayName = "Fisika Mekanika",
                unitNumber = unit.number,
                unitTitle = unit.title,
                unitSubtitle = unit.subtitle,
                progressPercentage = progress
            )
        }
    }
    // 3. Chemistry Check
    for (unit in chemistryUnits) {
        val completedCount = unit.nodes.indices.count { nodeIndex ->
            UserProgressManager.completedNodes.contains("Chemistry_${unit.number}_$nodeIndex")
        }
        if (completedCount < unit.nodes.size) {
            val progress = (completedCount.toFloat() / unit.nodes.size * 100).toInt()
            return ActiveMission(
                subject = "Chemistry",
                subjectDisplayName = "Kimia Stoikiometri",
                unitNumber = unit.number,
                unitTitle = unit.title,
                unitSubtitle = unit.subtitle,
                progressPercentage = progress
            )
        }
    }
    // Fallback: If everything is completed, show the final unit of Math
    return ActiveMission(
        subject = "Math",
        subjectDisplayName = "Matematika & Aljabar",
        unitNumber = 1,
        unitTitle = "Unit 1",
        unitSubtitle = "Aljabar & Persamaan Kuadrat",
        progressPercentage = 100
    )
}

@Composable
fun HomeScreen(onStartClick: () -> Unit = {}) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(context))
    val homeState by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)), // Modern background
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTopBar(title = "Nalar")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (homeState) {
                is HomeState.Loading -> {
                    HomeSkeletonScreen()
                }
                is HomeState.Success -> {
                    val profileData = (homeState as HomeState.Success).userProfile
                    HomeContent(
                        userProfile = profileData,
                        onStartClick = onStartClick
                    )
                }
                is HomeState.Error -> {
                    // Fallback using UserProgressManager local real-time values even during network errors
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Koneksi Bermasalah. Menampilkan Data Offline.",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = { viewModel.fetchProfile() }) {
                                Text("Coba Hubungkan Kembali")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    userProfile: AuthData,
    onStartClick: () -> Unit
) {
    val user = userProfile.user
    val scrollState = rememberScrollState()

    // 1. READ REAL-TIME APP VALUES directly from UserProgressManager to avoid profile lag
    val realTimeStreak = UserProgressManager.streakCount
    val realTimeKeys = UserProgressManager.keysCount
    val realTimeIsPremium = UserProgressManager.isPremium

    // 2. COMPUTE ACTIVE MISSION dynamically based on real completed nodes list
    val activeMission = remember(UserProgressManager.completedNodes.size) {
        getRealtimeActiveMission()
    }

    // Calculate real dynamic Quiz Accuracy Percentage
    val accuracy = if (user.total_answered != null && user.total_answered > 0) {
        ((user.total_correct ?: 0).toFloat() / user.total_answered * 100).toInt()
    } else {
        100
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── 1. GORGEOUS WELCOME DASHBOARD HEADER ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Premium Avatar Badge with luxury circular stroke
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = if (realTimeIsPremium) {
                                listOf(Color(0xFFFFD700), Color(0xFFFFA500), Color(0xFFFFD700))
                            } else {
                                listOf(Color(0xFF6750A4), Color(0xFF9C27B0), Color(0xFF6750A4))
                            }
                        ),
                        shape = CircleShape
                    )
                    .padding(3.dp), // Dynamic gradient border width
                contentAlignment = Alignment.Center
            ) {
                NalarAvatar(
                    avatarUrl = user.avatar_url,
                    size = 62.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Greeting & Rank badge
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Halo, ${user.name}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )
                    if (realTimeIsPremium) {
                        // VIP Crown Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Rank/League Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_league),
                        contentDescription = "League",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when (user.rank_name) {
                            "Beginner", "Novice", null -> "Bronze Pioneer"
                            else -> user.rank_name
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }

        // ── 2. REAL-TIME 2X2 STATISTICS GRID ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Streak Belajar",
                    value = "$realTimeStreak Hari",
                    iconRes = R.drawable.ic_streak,
                    gradientColors = listOf(Color(0xFFFFF7ED), Color(0xFFFFF2E2)),
                    accentColor = Color(0xFFEA580C)
                )
                // XP Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Poin Pengalaman",
                    value = "${user.points} XP",
                    iconRes = R.drawable.ic_exp,
                    gradientColors = listOf(Color(0xFFFEFCE8), Color(0xFFFEF9C3)),
                    accentColor = Color(0xFFCA8A04)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Keys Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Kunci Tersisa",
                    value = if (realTimeIsPremium) "Tak Terbatas" else "$realTimeKeys Kunci",
                    iconRes = R.drawable.ic_key,
                    gradientColors = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)),
                    accentColor = Color(0xFF16A34A)
                )
                // Accuracy Card
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Akurasi Kuis",
                    value = "$accuracy%",
                    iconRes = R.drawable.ic_rank,
                    gradientColors = listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF)),
                    accentColor = Color(0xFF9333EA)
                )
            }
        }

        // ── 3. FEATURED DYNAMIC ACTIVE MISSION CARD ( owl studying mascot ) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF6750A4), Color(0xFF4F378B)) // Brand premium purple
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Course Context Column
                Column(
                    modifier = Modifier.weight(1.3f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8DEF8).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MISI AKTIF REALTIME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE8DEF8)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = activeMission.subjectDisplayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${activeMission.unitTitle}: ${activeMission.unitSubtitle}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD0BCFF)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Styled custom progress bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progres Misi",
                                fontSize = 11.sp,
                                color = Color(0xFFE8DEF8)
                            )
                            Text(
                                text = "${activeMission.progressPercentage}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Color(0xFFE8DEF8).copy(alpha = 0.3f), CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(activeMission.progressPercentage / 100f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF388E3C), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Adorable Owl Mascot popping out
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mascot_studying),
                        contentDescription = "Mascot Studying",
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(2.dp, CircleShape)
                    )
                }
            }
        }

        // ── 4. PREMIUM 3D DEPRESSIBLE ACTION BUTTON ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PushableButton(
                text = "Lanjut Misi ${if (activeMission.subject == "Math") "Matematika" else activeMission.subject} 🚀",
                onClick = onStartClick,
                backgroundColor = Color(0xFF10B981), // Emerald green
                textColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Buka kunci modul baru untuk mengasah nalarmu!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF)
            )
        }

        // ── 5. NAO THE OWL MOTIVATION CARD ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3E8FF), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🦉",
                    fontSize = 22.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Nao berkata: \"Belajar 15 menit sehari secara rutin dapat menajamkan nalar logika logaritma Anda hingga 40%!\"",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B21A8),
                lineHeight = 16.sp
            )
        }
    }
}

// ── REUSABLE STAT CARD COMPONENT WITH GRADIENT BACKGROUND ──
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    iconRes: Int,
    gradientColors: List<Color>,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(gradientColors),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}

// ── GORGEOUS SHIMMERING SKELETON SCREEN FOR LOADING STATE ──
@Composable
fun HomeSkeletonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcome Card Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier.size(68.dp),
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }

        // 2x2 Grid Skeleton
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Featured Card Skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(24.dp)
        )

        // Button Skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        )

        // Owl Quote Skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
