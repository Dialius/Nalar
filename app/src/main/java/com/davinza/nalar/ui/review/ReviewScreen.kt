package com.davinza.nalar.ui.review

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Color palette ────────────────────────────────────────────────────────
private val ColorBackground = Color(0xFFF9F9F9)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorSurfaceContainerLow = Color(0xFFF3F3F3)
private val ColorSurfaceContainer = Color(0xFFEEEEEE)
private val ColorSurfaceContainerHigh = Color(0xFFE8E8E8)
private val ColorSurfaceVariant = Color(0xFFE2E2E2)
private val ColorOnSurface = Color(0xFF1A1C1C)
private val ColorOnSurfaceVariant = Color(0xFF4C4546)
private val ColorPrimary = Color(0xFF000000)
private val ColorOnPrimary = Color(0xFFFFFFFF)
private val ColorSecondary = Color(0xFF194BDF)
private val ColorError = Color(0xFFBA1A1A)
private val ColorErrorContainer = Color(0xFFFFDAD6)
private val ColorTertiaryFixedDim = Color(0xFF47E26A)

@Composable
fun ReviewScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            ReviewTopAppBar()

            // Main Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                // Header Card
                item {
                    MasterWeaknessesCard()
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Topic List Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Topics to Review",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPrimary
                        )
                        Text(
                            text = "4 Modules",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorOnSurfaceVariant
                        )
                    }
                }

                // Topics Grid (We use items to layout pairs for a 2-column grid-like look on wider screens, but on mobile it's usually 1 column. The reference HTML says grid-cols-1 md:grid-cols-2. I'll stick to a vertical list for a standard Android phone layout to match the single column mobile view.)
                item {
                    TopicCard(
                        title = "Algebraic Expressions",
                        mistakes = 12,
                        mastery = 45,
                        icon = Icons.Filled.Build,
                        isCritical = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TopicCard(
                        title = "Kinematics",
                        mistakes = 8,
                        mastery = 60,
                        icon = Icons.Filled.Refresh,
                        isCritical = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TopicCard(
                        title = "Geometry Basics",
                        mistakes = 5,
                        mastery = 75,
                        icon = Icons.Filled.Info,
                        isCritical = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TopicCard(
                        title = "Fractions & Ratios",
                        mistakes = 3,
                        mastery = 82,
                        icon = Icons.Filled.Info,
                        isCritical = false
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ColorSurface)
            .border(width = 1.dp, color = ColorSurfaceContainer)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = ColorPrimary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Math",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = ColorPrimary
            )
        }
        Text(
            text = "5 \uD83D\uDD25 3 ❤️", // 🔥 ❤️
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimary
        )
    }
}

@Composable
fun MasterWeaknessesCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ColorSurface)
            .border(1.dp, ColorSurfaceVariant, RoundedCornerShape(16.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            // Mascot Image Area (Top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(ColorSurfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.davinza.nalar.R.drawable.brain_psychology),
                    contentDescription = "Brain Psychology",
                    modifier = Modifier.size(160.dp)
                )
            }

            // Text Content Area (Bottom)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Warning",
                        tint = ColorError,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEEDS ATTENTION",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorError,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Master Your Weaknesses",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorPrimary,
                    modifier = Modifier.padding(bottom = 16.dp),
                    lineHeight = 36.sp
                )

                Text(
                    text = "You've made a few mistakes recently. Let's turn those into learning moments. Practicing your weak spots is the fastest way to level up!",
                    fontSize = 16.sp,
                    color = ColorOnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp),
                    lineHeight = 24.sp
                )

                StartPracticeButton()
            }
        }
    }
}

@Composable
fun StartPracticeButton(onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val yOffset by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "practiceBtn")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        // Shadow Layer (Black)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(52.dp)
                .background(ColorPrimary, RoundedCornerShape(8.dp))
        )
        // Top Layer (Black)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = yOffset)
                .background(ColorPrimary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info, // Placeholder for psychology icon
                    contentDescription = "Practice",
                    tint = ColorOnPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Practice",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorOnPrimary
                )
            }
        }
    }
}

@Composable
fun TopicCard(
    title: String,
    mistakes: Int,
    mastery: Int,
    icon: ImageVector,
    isCritical: Boolean
) {
    val cardBorderColor = if (isCritical) ColorErrorContainer else ColorSurfaceVariant
    val iconBgColor = if (isCritical) ColorErrorContainer else ColorSurfaceContainerHigh
    val iconColor = if (isCritical) ColorError else ColorOnSurfaceVariant
    val titleColor = ColorPrimary
    val mistakesColor = if (isCritical) ColorError else ColorOnSurfaceVariant
    val progressBarColor = if (isCritical) ColorError else if (mastery < 70) ColorSecondary else ColorTertiaryFixedDim

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorSurface)
            .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(12.dp))
            .clickable { /* Handle topic click */ }
    ) {
        // Decorative background element for critical cards
        if (isCritical) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .clip(RoundedCornerShape(bottomStart = 96.dp))
                    .background(ColorErrorContainer.copy(alpha = 0.5f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Section: Icon & Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title & Mistakes
                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning, // Info/history icon
                            contentDescription = "Mistakes",
                            tint = mistakesColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$mistakes Mistakes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = mistakesColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Section: Mastery Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mastery",
                        fontSize = 12.sp,
                        color = ColorOnSurfaceVariant
                    )
                    Text(
                        text = "$mastery%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ColorSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(mastery / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(progressBarColor)
                    )
                }
            }
        }
    }
}
