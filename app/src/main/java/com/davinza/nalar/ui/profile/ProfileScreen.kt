package com.davinza.nalar.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.davinza.nalar.R
import com.davinza.nalar.ui.components.NalarAvatar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.data.remote.model.User
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.courses.UserProgressManager

// ── Color Palette (Aesthetic Premium Custom Colors) ─────────────────
private val ColorBackground = Color(0xFFF9F9F9)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorPrimary = Color(0xFF1A1C1C)
private val ColorSecondary = Color(0xFF194BDF)
private val ColorOnSurfaceVariant = Color(0xFF4C4546)
private val ColorOutlineVariant = Color(0xFFE2E2E2)
private val ColorTertiaryFixedDim = Color(0xFF47E26A)

@Composable
fun ProfileScreen(onNavigateToSettings: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(context))
    val profileState by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    val userProfile = if (profileState is ProfileState.Success) {
        (profileState as ProfileState.Success).userProfile.user
    } else {
        null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        if (profileState is ProfileState.Loading) {
            ProfileSkeletonScreen(onNavigateToSettings = onNavigateToSettings)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                ProfileTopAppBar(onNavigateToSettings = onNavigateToSettings)

                // Main Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Header (Avatar, Name, Badge, Bio)
                    ProfileHeaderSection(userProfile = userProfile)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Bento-Style Stats Grid (2x2)
                    StatsBentoGrid(userProfile = userProfile)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Achievements Section
                    AchievementsSection()

                    Spacer(modifier = Modifier.height(100.dp)) // Padding at bottom for navigation bar spacer
                }
            }
        }
    }
}

@Composable
fun ProfileTopAppBar(onNavigateToSettings: () -> Unit) {
    com.davinza.nalar.ui.components.AppTopBar(
        title = "Profil",
        actions = {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    tint = ColorPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
fun ProfileHeaderSection(userProfile: User?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Container with shadow and white border
        NalarAvatar(
            avatarUrl = userProfile?.avatar_url,
            size = 128.dp,
            modifier = Modifier
                .shadow(elevation = 8.dp, shape = CircleShape)
                .border(width = 4.dp, color = Color.White, shape = CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        Text(
            text = userProfile?.name ?: "Learner",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Verified Badge
        val isPremium = UserProgressManager.isPremium
        val badgeText = if (isPremium) "Premium Scholar 👑" else "Nalar Scholar ⚡"
        val badgeBg = if (isPremium) Color(0xFFFEF3C7) else Color(0xFFE0F2FE)
        val badgeTextColor = if (isPremium) Color(0xFFD97706) else Color(0xFF0284C7)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(badgeBg)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Verified",
                tint = badgeTextColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = badgeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bio Description
        Text(
            text = "Exploring the beautiful patterns of the universe, one concept at a time.",
            fontSize = 14.sp,
            color = ColorOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(top = 4.dp),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun StatsBentoGrid(userProfile: User?) {
    // Compute live accuracy from API fields
    val accuracyText = run {
        val correct = userProfile?.total_correct ?: 0
        val total = userProfile?.total_answered ?: 0
        if (total > 0) "${(correct * 100 / total)}%" else "0%"
    }
    // Streak bound to real-time UserProgressManager
    val streakText = "${UserProgressManager.streakCount} Hari"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCard(
                modifier = Modifier.weight(1f),
                icon = { Image(painter = painterResource(id = R.drawable.ic_exp), contentDescription = "EXP", modifier = Modifier.size(24.dp)) },
                title = "Total XP",
                value = userProfile?.points?.toString() ?: "0",
                glowColor = ColorSecondary
            )
            BentoStatCard(
                modifier = Modifier.weight(1f),
                icon = { Image(painter = painterResource(id = R.drawable.ic_streak), contentDescription = "Streak", modifier = Modifier.size(24.dp)) },
                title = "Streak Aktif",
                value = streakText,
                glowColor = Color(0xFFF97316)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCard(
                modifier = Modifier.weight(1f),
                icon = { Image(painter = painterResource(id = R.drawable.ic_league), contentDescription = "League", modifier = Modifier.size(24.dp)) },
                title = "Current League",
                value = when (userProfile?.rank_name) {
                    "Beginner", "Novice", null -> "Bronze Pioneer"
                    else -> userProfile.rank_name
                },
                glowColor = Color(0xFFA855F7)
            )
            BentoStatCard(
                modifier = Modifier.weight(1f),
                icon = { TargetIcon(modifier = Modifier.size(24.dp), color = ColorTertiaryFixedDim) },
                title = "Akurasi",
                value = accuracyText,
                glowColor = ColorTertiaryFixedDim
            )
        }
    }
}

@Composable
fun BentoStatCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    title: String,
    value: String,
    glowColor: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(ColorSurface, RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = ColorOutlineVariant, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Aesthetic Glow Effect in the top-right corner
        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopEnd)
                .offset(x = 15.dp, y = (-15).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon on Top-Left
            icon()
            
            // Text at the bottom
            Column {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorOnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = when {
                        value.length > 15 -> 13.sp
                        value.length > 11 -> 15.sp
                        else -> 20.sp
                    },
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorPrimary,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun AchievementsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Achievements",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorSecondary,
                modifier = Modifier.clickable { /* View All Click */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AchievementBadgeCard(
                modifier = Modifier.weight(1f),
                title = "First Blood",
                icon = { TrophyIcon(modifier = Modifier.size(26.dp), color = Color.White) },
                gradientStart = Color(0xFFFDE047),
                gradientEnd = Color(0xFFF59E0B),
                isLocked = false
            )
            AchievementBadgeCard(
                modifier = Modifier.weight(1f),
                title = "30 Day Streak",
                icon = { FlameIcon(modifier = Modifier.size(26.dp), color = Color.White) },
                gradientStart = Color(0xFF93C5FD),
                gradientEnd = Color(0xFF3B82F6),
                isLocked = false
            )
            AchievementBadgeCard(
                modifier = Modifier.weight(1f),
                title = "Perfect Month",
                icon = { StarIcon(modifier = Modifier.size(26.dp), color = Color.White) },
                gradientStart = Color(0xFFE2E8F0),
                gradientEnd = Color(0xFF94A3B8),
                isLocked = true
            )
        }
    }
}

@Composable
fun AchievementBadgeCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit,
    gradientStart: Color,
    gradientEnd: Color,
    isLocked: Boolean
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(
                color = if (isLocked) Color(0xFFF1F5F9) else ColorSurface,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = ColorOutlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            // Badge Circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = if (isLocked) 0.dp else 4.dp,
                        shape = CircleShape
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isLocked) {
                                listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8))
                            } else {
                                listOf(gradientStart, gradientEnd)
                            }
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.alpha(if (isLocked) 0.5f else 1f)) {
                    icon()
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLocked) ColorOnSurfaceVariant.copy(alpha = 0.6f) else ColorPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

// ── CUSTOM PREMIUM CANVAS VECTOR DRAWINGS (No OS Emojis) ─────────────────

@Composable
fun LightningIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width * 0.6f, 0f)
            lineTo(width * 0.15f, height * 0.55f)
            lineTo(width * 0.5f, height * 0.55f)
            lineTo(width * 0.4f, height)
            lineTo(width * 0.85f, height * 0.45f)
            lineTo(width * 0.5f, height * 0.45f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun FlameIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width * 0.5f, 0f)
            cubicTo(width * 0.75f, height * 0.25f, width * 0.9f, height * 0.45f, width * 0.85f, height * 0.72f)
            cubicTo(width * 0.8f, height * 0.98f, width * 0.2f, height * 0.98f, width * 0.15f, height * 0.72f)
            cubicTo(width * 0.1f, height * 0.45f, width * 0.25f, height * 0.25f, width * 0.5f, 0f)
            close()
        }
        drawPath(path = path, color = color)
        
        // Inner flame layer for dynamic 3D gradient look
        val innerPath = Path().apply {
            moveTo(width * 0.5f, height * 0.35f)
            cubicTo(width * 0.65f, height * 0.5f, width * 0.72f, height * 0.62f, width * 0.68f, height * 0.8f)
            cubicTo(width * 0.65f, height * 0.93f, width * 0.35f, height * 0.93f, width * 0.32f, height * 0.8f)
            cubicTo(width * 0.28f, height * 0.62f, width * 0.35f, height * 0.5f, width * 0.5f, height * 0.35f)
            close()
        }
        drawPath(
            path = innerPath, 
            color = if (color == Color.White) Color.White.copy(alpha = 0.5f) else Color(0xFFFFD166)
        )
    }
}

@Composable
fun TrophyIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Base plate
        drawRoundRect(
            color = color,
            topLeft = Offset(width * 0.2f, height * 0.88f),
            size = Size(width * 0.6f, height * 0.12f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Stem
        drawRect(
            color = color,
            topLeft = Offset(width * 0.42f, height * 0.65f),
            size = Size(width * 0.16f, height * 0.25f)
        )
        // Cup Bowl
        val cupPath = Path().apply {
            moveTo(width * 0.22f, height * 0.15f)
            lineTo(width * 0.78f, height * 0.15f)
            cubicTo(width * 0.78f, height * 0.52f, width * 0.68f, height * 0.68f, width * 0.5f, height * 0.68f)
            cubicTo(width * 0.32f, height * 0.68f, width * 0.22f, height * 0.52f, width * 0.22f, height * 0.15f)
            close()
        }
        drawPath(path = cupPath, color = color)
        
        // Handles (left & right ears)
        drawPath(
            path = Path().apply {
                moveTo(width * 0.22f, height * 0.22f)
                cubicTo(width * 0.08f, height * 0.22f, width * 0.08f, height * 0.48f, width * 0.22f, height * 0.48f)
            },
            color = color,
            style = Stroke(width = width * 0.08f)
        )
        drawPath(
            path = Path().apply {
                moveTo(width * 0.78f, height * 0.22f)
                cubicTo(width * 0.92f, height * 0.22f, width * 0.92f, height * 0.48f, width * 0.78f, height * 0.48f)
            },
            color = color,
            style = Stroke(width = width * 0.08f)
        )
    }
}

@Composable
fun TargetIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f
        
        // Outer ring
        drawCircle(color = color, radius = radius, style = Stroke(width = size.width * 0.08f))
        // Middle ring
        drawCircle(color = color, radius = radius * 0.6f, style = Stroke(width = size.width * 0.08f))
        // Center Bullseye
        drawCircle(color = color, radius = radius * 0.22f)
    }
}

@Composable
fun StarIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width * 0.5f, 0f)
            lineTo(width * 0.63f, height * 0.36f)
            lineTo(width, height * 0.36f)
            lineTo(width * 0.7f, height * 0.58f)
            lineTo(width * 0.81f, height)
            lineTo(width * 0.5f, height * 0.78f)
            lineTo(width * 0.19f, height)
            lineTo(width * 0.3f, height * 0.58f)
            lineTo(0f, height * 0.36f)
            lineTo(width * 0.37f, height * 0.36f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun HeartIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width * 0.5f, height * 0.25f)
            cubicTo(width * 0.28f, 0f, 0f, height * 0.08f, 0f, height * 0.45f)
            cubicTo(0f, height * 0.72f, width * 0.28f, height * 0.92f, width * 0.5f, height)
            cubicTo(width * 0.72f, height * 0.92f, width, height * 0.72f, width, height * 0.45f)
            cubicTo(width, height * 0.08f, width * 0.72f, 0f, width * 0.5f, height * 0.25f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun StudentAvatarIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Head / Face circle
        drawCircle(
            color = Color(0xFFFFD166),
            radius = width * 0.22f,
            center = Offset(width / 2f, height * 0.45f)
        )
        
        // Body / Shoulders
        val bodyPath = Path().apply {
            moveTo(width * 0.2f, height * 0.9f)
            quadraticTo(width * 0.2f, height * 0.65f, width * 0.35f, height * 0.65f)
            lineTo(width * 0.65f, height * 0.65f)
            quadraticTo(width * 0.8f, height * 0.65f, width * 0.8f, height * 0.9f)
            close()
        }
        drawPath(path = bodyPath, color = Color(0xFF1E293B)) // Slate body gown
        
        // Graduation Cap (Rhombus top)
        val capPath = Path().apply {
            moveTo(width * 0.5f, height * 0.16f)
            lineTo(width * 0.82f, height * 0.27f)
            lineTo(width * 0.5f, height * 0.38f)
            lineTo(width * 0.18f, height * 0.27f)
            close()
        }
        drawPath(path = capPath, color = Color(0xFF0F172A)) // Dark slate cap
        
        // Cap base
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(width * 0.38f, height * 0.27f),
            size = Size(width * 0.24f, height * 0.08f)
        )
        
        // Tassel
        drawPath(
            path = Path().apply {
                moveTo(width * 0.5f, height * 0.27f)
                lineTo(width * 0.25f, height * 0.34f)
                lineTo(width * 0.25f, height * 0.46f)
            },
            color = Color(0xFFF59E0B),
            style = Stroke(width = width * 0.025f)
        )
    }
}

@Composable
fun ProfileSkeletonScreen(onNavigateToSettings: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ProfileTopAppBar(onNavigateToSettings = onNavigateToSettings)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Avatar shimmer
            com.davinza.nalar.ui.components.ShimmerBox(
                modifier = Modifier.size(90.dp),
                shape = CircleShape
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name shimmer
            com.davinza.nalar.ui.components.ShimmerBox(
                modifier = Modifier.width(160.dp).height(24.dp),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bio shimmer
            com.davinza.nalar.ui.components.ShimmerBox(
                modifier = Modifier.width(220.dp).height(16.dp),
                shape = RoundedCornerShape(4.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Bento stats grid shimmer (2x2)
            Row(modifier = Modifier.fillMaxWidth()) {
                com.davinza.nalar.ui.components.ShimmerBox(
                    modifier = Modifier.weight(1f).height(100.dp).padding(end = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                com.davinza.nalar.ui.components.ShimmerBox(
                    modifier = Modifier.weight(1f).height(100.dp).padding(start = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                com.davinza.nalar.ui.components.ShimmerBox(
                    modifier = Modifier.weight(1f).height(100.dp).padding(end = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                com.davinza.nalar.ui.components.ShimmerBox(
                    modifier = Modifier.weight(1f).height(100.dp).padding(start = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Achievements Section Title
            Row(modifier = Modifier.fillMaxWidth()) {
                com.davinza.nalar.ui.components.ShimmerBox(
                    modifier = Modifier.width(140.dp).height(22.dp),
                    shape = RoundedCornerShape(6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Achievement cards shimmer
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(2) {
                    com.davinza.nalar.ui.components.ShimmerBox(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }
    }
}

