package com.davinza.nalar.ui.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.ui.components.PushableButton
import com.davinza.nalar.ui.courses.UserProgressManager

// ── Design Tokens ────────────────────────────────────────────────────────────
private val ColorPremiumGold    = Color(0xFFFFB300)
private val ColorPremiumGoldEnd = Color(0xFFFF8F00)
private val ColorDeepDarkBlue   = Color(0xFF1A1C2E)
private val ColorDeepPurple     = Color(0xFF2D1B69)
private val ColorLightBlueGlow  = Color(0xFFF8F9FF)
private val ColorLightBlueBorder = Color(0xFFE8EEFF)
private val ColorHighlightYellow = Color(0xFFFFFBE6)

@Composable
fun PremiumScreen(
    onNavigateToPaymentMethod: () -> Unit
) {
    val scrollState = rememberScrollState()

    if (UserProgressManager.isPremium) {
        PremiumActiveScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        com.davinza.nalar.ui.components.AppTopBar(
            title = "Premium",
            actions = {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ColorPremiumGold,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = "⭐ Khusus",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        )

        // ── Hero Section ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorDeepDarkBlue, ColorDeepPurple)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(vertical = 32.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "👑", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Nalar Premium",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Belajar tanpa batas, raih yang terbaik",
                    fontSize = 14.sp,
                    color = Color(0xFFB0B8D1),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFD54F)
                ) {
                    Text(
                        text = "Rp 49.900 / bulan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // ── Feature Cards Section ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Yang Kamu Dapatkan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 28.dp, bottom = 16.dp)
            )

            BenefitCard(
                emoji = "📚",
                circleColor = Color(0xFFE8F4FF),
                title = "100+ Kursus Penuh",
                description = "Akses semua materi dari dasar hingga tingkat lanjut tanpa terkunci."
            )
            Spacer(modifier = Modifier.height(12.dp))
            BenefitCard(
                emoji = "🔑",
                circleColor = Color(0xFFFFF8E1),
                title = "Kunci Tak Terbatas",
                description = "Tidak perlu khawatir kehabisan kunci saat belajar setiap harinya."
            )
            Spacer(modifier = Modifier.height(12.dp))
            BenefitCard(
                emoji = "📶",
                circleColor = Color(0xFFE8FFE8),
                title = "Mode Offline",
                description = "Belajar dan selesaikan tantangan di mana saja tanpa koneksi internet."
            )
            Spacer(modifier = Modifier.height(12.dp))
            BenefitCard(
                emoji = "🏆",
                circleColor = Color(0xFFF3E8FF),
                title = "League Eksklusif",
                description = "Masuk leaderboard khusus pengguna Premium dan tunjukkan prestasimu."
            )

            // ── Comparison Section ────────────────────────────────────────────
            Text(
                text = "Free vs Premium",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1.5f))
                        Text(
                            text = "Gratis",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Surface(
                            color = ColorHighlightYellow,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = "Premium ⭐",
                                fontWeight = FontWeight.Bold,
                                color = ColorPremiumGoldEnd,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                    ComparisonRow("Kursus Dasar", true, true)
                    ComparisonRow("Progress Tracking", true, true)
                    ComparisonRow("100+ Kursus Lengkap", false, true)
                    ComparisonRow("Kunci Tak Terbatas", false, true)
                    ComparisonRow("Mode Offline", false, true)
                    ComparisonRow("League Premium", false, true)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── CTA Button ────────────────────────────────────────────────────
            PushableButton(
                text = "Mulai Premium Sekarang 🚀",
                onClick = onNavigateToPaymentMethod,
                backgroundColor = ColorPremiumGold,
                textColor = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dapat dibatalkan kapan saja",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Restore Purchase",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun BenefitCard(
    emoji: String,
    circleColor: Color,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ColorLightBlueBorder),
        color = ColorLightBlueGlow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(circleColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ComparisonRow(
    feature: String,
    freeHas: Boolean,
    premiumHas: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1.5f)
        )
        // Free Column
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (freeHas) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Yes",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "No",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        // Premium Column
        Box(
            modifier = Modifier
                .weight(1.2f)
                .background(ColorHighlightYellow.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (premiumHas) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFF10B981), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Yes",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "No",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
}

@Composable
fun PremiumActiveScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        com.davinza.nalar.ui.components.AppTopBar(title = "Premium")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorPremiumGold, ColorPremiumGoldEnd)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👑", fontSize = 48.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Nalar Premium Aktif!",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Terima kasih telah berlangganan! Semua fitur premium Nalar sekarang terbuka sepenuhnya untuk Anda.",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(36.dp))

        // Benefits box
        Surface(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, ColorLightBlueBorder),
            color = ColorLightBlueGlow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Akses Premium Anda:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActiveBenefitRow(emoji = "🔑", text = "Kunci Belajar Tak Terbatas (Aktif)")
                Spacer(modifier = Modifier.height(12.dp))
                ActiveBenefitRow(emoji = "📚", text = "100+ Kursus & Latihan Terbuka (Aktif)")
                Spacer(modifier = Modifier.height(12.dp))
                ActiveBenefitRow(emoji = "🏆", text = "Leaderboard & League Premium (Aktif)")
            }
        }
    }
}
}

@Composable
fun ActiveBenefitRow(emoji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE8FFE8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B5E20)
        )
    }
}
