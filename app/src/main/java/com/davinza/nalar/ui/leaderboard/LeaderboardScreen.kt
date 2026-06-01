package com.davinza.nalar.ui.leaderboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import com.davinza.nalar.ui.courses.UserProgressManager

// ── Color palette (High-fidelity matching reference) ────────────────────────
private val ColorBackground = Color(0xFFF9F9F9)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorSurfaceContainerHigh = Color(0xFFEEEEEE)
private val ColorSurfaceVariant = Color(0xFFE2E2E2)
private val ColorOnSurfaceVariant = Color(0xFF4C4546)
private val ColorPrimary = Color(0xFF000000)
private val ColorSecondary = Color(0xFF194BDF)
private val ColorOutlineVariant = Color(0xFFCFC4C5)
private val ColorPrimaryFixedDim = Color(0xFFC6C6C6)

// Podium Colors
private val Rank1Color = Color(0xFFFFC000)
private val Rank1Pedestal = Color(0xFFFFF8E1)
private val Rank2Color = Color(0xFFE2E2E2)
private val Rank2Pedestal = Color(0xFFEEEEEE)
private val Rank3Color = Color(0xFFCD7F32)
private val Rank3Pedestal = Color(0xFFFDF5E6)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val xp: Int,
    val avatarUrl: String? = null
)

@Composable
fun LeaderboardScreen() {
    var isWeeklyLeague by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val viewModel: LeaderboardViewModel = viewModel(factory = AppViewModelFactory(context))
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        when (val s = state) {
            is LeaderboardState.Loading -> {
                LeaderboardSkeletonScreen()
            }
            is LeaderboardState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Gagal memuat data leaderboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = s.message,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.loadData() },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorSecondary)
                    ) {
                        Text("Coba Lagi", color = Color.White)
                    }
                }
            }
            is LeaderboardState.Success -> {
                val currentUserName = s.profile.user.name
                
                // Map remote model to local LeaderboardUser
                val remoteList = s.leaderboard.leaderboard
                val localList = remoteList.map { entry ->
                    LeaderboardUser(
                        rank = entry.position,
                        name = entry.name,
                        xp = entry.points,
                        avatarUrl = entry.avatar_url
                    )
                }

                val first = localList.find { it.rank == 1 }
                val second = localList.find { it.rank == 2 }
                val third = localList.find { it.rank == 3 }
                val rest = localList.filter { it.rank > 3 }

                Column(modifier = Modifier.fillMaxSize()) {
                    LeaderboardTopAppBar()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Header & Toggle Buttons
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp, bottom = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Leaderboard",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ColorPrimary,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )

                                LeagueToggle(
                                    isWeeklyLeague = isWeeklyLeague,
                                    onToggle = { isWeeklyLeague = it }
                                )
                            }
                        }

                        // High-fidelity Podium Section
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            PodiumSection(first = first, second = second, third = third, currentUserName = currentUserName)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Leaderboard List
                        items(rest) { user ->
                            val isMe = user.name == currentUserName
                            LeaderboardListItem(user = user, isMe = isMe)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardTopAppBar() {
    com.davinza.nalar.ui.components.AppTopBar(title = "Ranks")
}

@Composable
fun LeagueToggle(
    isWeeklyLeague: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(340.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(ColorSurfaceContainerHigh)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weekly League
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(if (isWeeklyLeague) ColorSurface else Color.Transparent)
                    .clickable { onToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Weekly League",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWeeklyLeague) ColorPrimary else ColorOnSurfaceVariant
                )
            }

            // All Time
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(if (!isWeeklyLeague) ColorSurface else Color.Transparent)
                    .clickable { onToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isWeeklyLeague) ColorPrimary else ColorOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PodiumSection(
    first: LeaderboardUser?,
    second: LeaderboardUser?,
    third: LeaderboardUser?,
    currentUserName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2 (Left)
        PodiumItem(
            rank = 2,
            name = second?.name ?: "No Player",
            xp = second?.let { String.format("%,d XP", it.xp) } ?: "0 XP",
            avatarUrl = second?.avatarUrl,
            color = Rank2Color,
            textColor = Color(0xFF7F8C8D), // Silver grey contrast
            pedestalColor = Rank2Pedestal,
            pedestalHeight = 100.dp,
            avatarSize = 72.dp,
            isMe = second?.name == currentUserName
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Rank 1 (Center - taller and highlighted)
        PodiumItem(
            rank = 1,
            name = first?.name ?: "No Player",
            xp = first?.let { String.format("%,d XP", it.xp) } ?: "0 XP",
            avatarUrl = first?.avatarUrl,
            color = Rank1Color,
            textColor = Color(0xFFD4AF37), // Rich gold contrast
            pedestalColor = Rank1Pedestal,
            pedestalHeight = 135.dp,
            avatarSize = 88.dp,
            isFirst = true,
            isMe = first?.name == currentUserName
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Rank 3 (Right)
        PodiumItem(
            rank = 3,
            name = third?.name ?: "No Player",
            xp = third?.let { String.format("%,d XP", it.xp) } ?: "0 XP",
            avatarUrl = third?.avatarUrl,
            color = Rank3Color,
            textColor = Color(0xFFA0522D), // Rich bronze contrast
            pedestalColor = Rank3Pedestal,
            pedestalHeight = 75.dp,
            avatarSize = 72.dp,
            isMe = third?.name == currentUserName
        )
    }
}

@Composable
fun PodiumItem(
    rank: Int,
    name: String,
    xp: String,
    avatarUrl: String?,
    color: Color,
    textColor: Color,
    pedestalColor: Color,
    pedestalHeight: Dp,
    avatarSize: Dp,
    isFirst: Boolean = false,
    isMe: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (isFirst) {
            CrownIcon(
                modifier = Modifier
                    .size(36.dp)
                    .padding(bottom = 4.dp)
            )
        }

        // Avatar circle with badge
        Box(
            modifier = Modifier.padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            LeaderboardAvatar(
                name = name,
                avatarUrl = avatarUrl,
                borderWidth = if (isMe) 5.dp else 4.dp,
                borderColor = if (isMe) Color(0xFF4CAF50) else color,
                size = avatarSize
            )

            // Rank Badge
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(if (isMe) Color(0xFF4CAF50) else color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (rank == 2 && !isMe) ColorPrimary else Color.White
                )
            }
        }

        if (isMe) {
            Box(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4CAF50))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Kamu",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isMe) Color(0xFF2E7D32) else ColorPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = xp,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMe) Color(0xFF2E7D32) else textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pedestal matching reference: solid fill + custom top border highlight + large bold rank text
        Box(
            modifier = Modifier
                .width(avatarSize + 24.dp)
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(if (isMe) Color(0xFFC8E6C9) else pedestalColor),
            contentAlignment = Alignment.Center
        ) {
            // High-fidelity top accent border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(if (isMe) Color(0xFF4CAF50) else color)
                    .align(Alignment.TopCenter)
            )

            // Large, semi-transparent rank number matching modern sport pedestals
            Text(
                text = rank.toString(),
                fontSize = (pedestalHeight.value * 0.45f).sp,
                fontWeight = FontWeight.Black,
                color = (if (isMe) Color(0xFF4CAF50) else if (rank == 2) Color(0xFF9E9E9E) else color).copy(alpha = 0.3f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LeaderboardAvatar(
    name: String,
    avatarUrl: String?,
    borderWidth: Dp,
    borderColor: Color,
    size: Dp
) {
    if (avatarUrl != null && avatarUrl.isNotEmpty()) {
        com.davinza.nalar.ui.components.NalarAvatar(
            avatarUrl = avatarUrl,
            size = size,
            modifier = Modifier.border(borderWidth, borderColor, CircleShape)
        )
    } else {
        // Generate beautiful, deterministic gradient colors based on the user's name
        val gradientColors = remember(name) {
            val hash = name.hashCode()
            val c1 = Color(
                red = ((hash and 0xFF0000) shr 16).coerceIn(100, 220),
                green = ((hash and 0x00FF00) shr 8).coerceIn(100, 220),
                blue = (hash and 0x0000FF).coerceIn(100, 220)
            )
            val c2 = Color(
                red = ((hash.inv() and 0xFF0000) shr 16).coerceIn(120, 240),
                green = ((hash.inv() and 0x00FF00) shr 8).coerceIn(120, 240),
                blue = (hash.inv() and 0x0000FF).coerceIn(120, 240)
            )
            listOf(c1, c2)
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradientColors))
                .border(borderWidth, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CrownIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Crown Path
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.8f)
            lineTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.65f, h * 0.55f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.8f)
            close()
        }

        // Base strip path
        val base = Path().apply {
            moveTo(w * 0.1f, h * 0.8f)
            lineTo(w * 0.9f, h * 0.8f)
            lineTo(w * 0.9f, h * 0.9f)
            lineTo(w * 0.1f, h * 0.9f)
            close()
        }

        drawPath(path = path, color = Color(0xFFFFC000))
        drawPath(path = base, color = Color(0xFFFFC000))

        // Crown tip jewels
        drawCircle(color = Color(0xFFFFC000), radius = w * 0.05f, center = Offset(w * 0.15f, h * 0.3f))
        drawCircle(color = Color(0xFFFFC000), radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.17f))
        drawCircle(color = Color(0xFFFFC000), radius = w * 0.05f, center = Offset(w * 0.85f, h * 0.3f))
    }
}

@Composable
fun LeaderboardListItem(user: LeaderboardUser, isMe: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isMe) Color(0xFFE8F5E9) else ColorSurface)
            .border(
                width = if (isMe) 2.dp else 1.dp,
                color = if (isMe) Color(0xFF4CAF50) else ColorOutlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = user.rank.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isMe) Color(0xFF2E7D32) else ColorOnSurfaceVariant,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Start
        )

        // Colorful dynamic Avatar
        LeaderboardAvatar(
            name = user.name,
            avatarUrl = user.avatarUrl,
            borderWidth = 2.dp,
            borderColor = if (isMe) Color(0xFF4CAF50) else ColorSurfaceContainerHigh,
            size = 46.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Username
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMe) Color(0xFF2E7D32) else ColorPrimary
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Kamu",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Points
        Text(
            text = String.format("%,d XP", user.xp),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isMe) Color(0xFF2E7D32) else ColorSecondary
        )
    }
}

@Composable
fun LeaderboardSkeletonScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        LeaderboardTopAppBar()
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title shimmer
            com.davinza.nalar.ui.components.ShimmerBox(
                modifier = Modifier
                    .width(180.dp)
                    .height(28.dp),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Toggle shimmer
            com.davinza.nalar.ui.components.ShimmerBox(
                modifier = Modifier
                    .width(340.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Podium shimmer (3 staggered pillars)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Rank 2 Pillar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(60.dp), shape = CircleShape)
                Spacer(modifier = Modifier.height(8.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(60.dp).height(12.dp), shape = RoundedCornerShape(4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(70.dp).height(80.dp), shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp))
            }
            
            // Rank 1 Pillar (tallest)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(70.dp), shape = CircleShape)
                Spacer(modifier = Modifier.height(8.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(70.dp).height(12.dp), shape = RoundedCornerShape(4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(80.dp).height(120.dp), shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp))
            }
            
            // Rank 3 Pillar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(56.dp), shape = CircleShape)
                Spacer(modifier = Modifier.height(8.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(56.dp).height(12.dp), shape = RoundedCornerShape(4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(70.dp).height(60.dp), shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // List item shimmer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(24.dp), shape = CircleShape)
                    Spacer(modifier = Modifier.width(16.dp))
                    com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(40.dp), shape = CircleShape)
                    Spacer(modifier = Modifier.width(16.dp))
                    com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(50.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
                }
            }
        }
    }
}
