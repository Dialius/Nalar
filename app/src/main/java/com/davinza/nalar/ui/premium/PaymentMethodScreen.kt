package com.davinza.nalar.ui.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davinza.nalar.di.AppViewModelFactory

// ── Color Schemes ────────────────────────────────────────────────────────────
private val ColorDarkIndigoFirst = Color(0xFF1A237E)
private val ColorDarkIndigoSecond = Color(0xFF3949AB)
private val ColorOutline = Color(0xFFE5E7EB)

@Composable
fun PaymentMethodScreen(
    onBack: () -> Unit,
    onPaymentSuccess: (bank: String, vaNumber: String, orderId: String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: PremiumViewModel = viewModel(factory = AppViewModelFactory(context))
    val paymentState by viewModel.paymentState.collectAsState()
    val scrollState = rememberScrollState()

    var selectedBank by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(paymentState) {
        when (paymentState) {
            is PaymentState.Success -> {
                val data = (paymentState as PaymentState.Success).paymentData
                val va = data.va_numbers?.firstOrNull()?.va_number ?: ""
                val bank = data.va_numbers?.firstOrNull()?.bank?.uppercase() ?: data.bank?.uppercase() ?: ""
                onPaymentSuccess(bank, va, data.order_id)
                viewModel.resetState()
            }
            is PaymentState.Error -> {
                val errorMsg = (paymentState as PaymentState.Error).message
                snackbarHostState.showSnackbar(errorMsg)
                selectedBank = null
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = ColorOutline)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pilih Pembayaran",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // ── Price Summary Card ──────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(ColorDarkIndigoFirst, ColorDarkIndigoSecond)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Nalar Premium",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        text = "1 Bulan",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Total Pembayaran",
                                    fontSize = 13.sp,
                                    color = Color(0xFFB0B8D1)
                                )
                                Text(
                                    text = "Rp 49.900",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // ── Bank List Title ──────────────────────────────────────────
                Text(
                    text = "Virtual Account Bank",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)
                )

                // ── Payment Methods ──────────────────────────────────────────
                PaymentMethodCard(
                    bankCode = "BCA",
                    bankColor = Color(0xFF0066AE),
                    textColor = Color.White,
                    title = "Bank BCA",
                    subtitle = "Virtual Account BCA",
                    isLoading = selectedBank == "bca" && paymentState is PaymentState.Loading,
                    onClick = {
                        if (paymentState !is PaymentState.Loading) {
                            selectedBank = "bca"
                            viewModel.startPayment("bank_transfer", "bca")
                        }
                    }
                )

                PaymentMethodCard(
                    bankCode = "MDR",
                    bankColor = Color(0xFFFACC15),
                    textColor = Color.Black,
                    title = "Bank Mandiri",
                    subtitle = "Virtual Account Mandiri",
                    isLoading = selectedBank == "echannel" && paymentState is PaymentState.Loading,
                    onClick = {
                        if (paymentState !is PaymentState.Loading) {
                            selectedBank = "echannel"
                            viewModel.startPayment("bank_transfer", "echannel")
                        }
                    }
                )

                PaymentMethodCard(
                    bankCode = "BNI",
                    bankColor = Color(0xFFFF6600),
                    textColor = Color.White,
                    title = "Bank BNI",
                    subtitle = "Virtual Account BNI",
                    isLoading = selectedBank == "bni" && paymentState is PaymentState.Loading,
                    onClick = {
                        if (paymentState !is PaymentState.Loading) {
                            selectedBank = "bni"
                            viewModel.startPayment("bank_transfer", "bni")
                        }
                    }
                )

                PaymentMethodCard(
                    bankCode = "BRI",
                    bankColor = Color(0xFF003E8C),
                    textColor = Color.White,
                    title = "Bank BRI",
                    subtitle = "Virtual Account BRI",
                    isLoading = selectedBank == "bri" && paymentState is PaymentState.Loading,
                    onClick = {
                        if (paymentState !is PaymentState.Loading) {
                            selectedBank = "bri"
                            viewModel.startPayment("bank_transfer", "bri")
                        }
                    }
                )

                // ── Footer ────────────────────────────────────────────────────
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "🔒 Pembayaran aman & terenkripsi",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    bankCode: String,
    bankColor: Color,
    textColor: Color,
    title: String,
    subtitle: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, ColorOutline),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(bankColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bankCode,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
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
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF1A237E)
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Pilih",
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
