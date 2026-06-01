package com.davinza.nalar.ui.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.ui.components.PushableButton
import kotlinx.coroutines.delay

import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory
import androidx.compose.ui.platform.LocalContext

@Composable
fun PaymentInstructionScreen(
    bank: String,
    vaNumber: String,
    orderId: String,
    onPaymentSuccess: () -> Unit,
    onPaymentExpired: () -> Unit,
    onBackToHome: () -> Unit
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val viewModel: PremiumViewModel = viewModel(factory = AppViewModelFactory(context))
    val pollingState by viewModel.pollingState.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.startPolling(orderId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPolling()
        }
    }

    LaunchedEffect(pollingState) {
        if (!hasNavigated) {
            when (pollingState) {
                is PaymentPollingState.Paid -> {
                    hasNavigated = true
                    onPaymentSuccess()
                }
                is PaymentPollingState.Expired -> {
                    hasNavigated = true
                    onPaymentExpired()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Custom Top Bar ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFE5E7EB))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToHome) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Beranda",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Instruksi Pembayaran",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Status Header ────────────────────────────────────────────────
            Text(text = "⏳", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Menunggu Pembayaran",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selesaikan pembayaran sebelum 24 jam",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            if (pollingState is PaymentPollingState.Polling) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF194BDF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mengecek pembayaran otomatis...",
                        fontSize = 12.sp,
                        color = Color(0xFF194BDF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── VA Card ──────────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 2.dp,
                color = Color(0xFFF9FAFB),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BANK ${bank.uppercase()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nomor Virtual Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = vaNumber,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(vaNumber))
                            isCopied = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981))
                    ) {
                        Text(
                            text = if (isCopied) "✓ Tersalin!" else "Salin Nomor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Step Instructions ────────────────────────────────────────────
            Text(
                text = "Cara Pembayaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            InstructionStep(
                stepNumber = "1",
                text = "Buka aplikasi mobile banking atau pergi ke ATM Bank ${bank.uppercase()} terdekat."
            )
            Spacer(modifier = Modifier.height(16.dp))
            InstructionStep(
                stepNumber = "2",
                text = "Pilih menu 'Transfer' > 'Virtual Account' (atau bayar echannel) lalu masukkan nomor Virtual Account di atas."
            )
            Spacer(modifier = Modifier.height(16.dp))
            InstructionStep(
                stepNumber = "3",
                text = "Konfirmasi detail tagihan sebesar Rp 49.900 dan tekan 'Bayar' untuk menyelesaikan pembayaran."
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Home Button ──────────────────────────────────────────────────
            PushableButton(
                text = "Kembali ke Beranda",
                onClick = onBackToHome,
                backgroundColor = Color(0xFF1E3A8A),
                textColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun InstructionStep(
    stepNumber: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF194BDF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF4C4546),
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
