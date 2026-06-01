package com.davinza.nalar.ui.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val ColorPremiumGold    = Color(0xFFFFB300)
private val ColorPremiumGoldEnd = Color(0xFFFF8F00)
private val ColorSuccessGreen   = Color(0xFF10B981)

@Composable
fun PremiumSuccessScreen(
    onMulaiBelajar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Celebratory graphics / icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorPremiumGold, ColorPremiumGoldEnd)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "👑", fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Pembayaran Berhasil!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorSuccessGreen,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Selamat! Anda sekarang adalah anggota Premium Nalar. Nikmati akses tanpa batas ke semua materi pembelajaran, latihan soal, dan kunci tanpa batas.",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Premium Badge Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, ColorPremiumGold),
            color = Color(0xFFFFFBE6),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Status: PREMIUM AKTIF ⭐",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = ColorPremiumGoldEnd
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Akses penuh telah diaktifkan secara instan untuk akun Anda.",
                    fontSize = 13.sp,
                    color = Color(0xFF5C5429),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // CTA
        PushableButton(
            text = "Mulai Belajar Sekarang 🚀",
            onClick = {
                // Ensure the memory status is updated instantly in UserProgressManager as well
                UserProgressManager.isPremium = true
                onMulaiBelajar()
            },
            backgroundColor = ColorPremiumGold,
            textColor = Color.Black,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}
