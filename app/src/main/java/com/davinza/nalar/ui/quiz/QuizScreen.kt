package com.davinza.nalar.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.davinza.nalar.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.davinza.nalar.ui.courses.UserProgressManager
import com.davinza.nalar.ui.courses.formatMathText
import com.davinza.nalar.AppEventBus
import kotlinx.coroutines.launch

// ── Color palette ────────────────────────────────────────────────────────
private val ColorBackground = Color(0xFFF9F9F9)
private val ColorSurface = Color(0xFFF9F9F9)
private val ColorSurfaceContainerLowest = Color(0xFFFFFFFF)
private val ColorSurfaceContainerLow = Color(0xFFF3F3F3)
private val ColorSurfaceContainer = Color(0xFFEEEEEE)
private val ColorSurfaceContainerHigh = Color(0xFFE8E8E8)
private val ColorSurfaceTint = Color(0xFF5E5E5E)
private val ColorSurfaceVariant = Color(0xFFE2E2E2)
private val ColorOutlineVariant = Color(0xFFCFC4C5)
private val ColorSecondaryContainer = Color(0xFF3E67F9)
private val ColorSecondaryFixed = Color(0xFFDDE1FF)
private val ColorTertiaryFixed = Color(0xFF6BFF84)
private val ColorTertiaryFixedDim = Color(0xFF47E26A)
private val ColorTertiaryContainer = Color(0xFF002107)
private val ColorOnTertiaryFixed = Color(0xFF002107)
private val ColorError = Color(0xFFBA1A1A)
private val ColorOnError = Color(0xFFFFFFFF)
private val ColorErrorContainer = Color(0xFFFFDAD6)
private val ColorOnErrorContainer = Color(0xFF93000A)
private val ColorOnBackground = Color(0xFF1A1C1C)
private val ColorOnSurface = Color(0xFF1A1C1C)
private val ColorOnSurfaceVariant = Color(0xFF4C4546)
private val ColorPrimary = Color(0xFF000000)

// Feedback specific colors
private val ColorFeedbackBg = Color(0xFFD7FFB8)
private val ColorFeedbackBtnBg = Color(0xFF6BFF84)
private val ColorFeedbackBtnShadow = Color(0xFF42CD63)
private val ColorOnTertiaryFixedVariant = Color(0xFF00531C)
private val ColorSparkle = Color(0xFFFFC000)
private val ColorTertiaryBorder = Color(0xFF3EB550)

data class QuizQuestion(
    val title: String,
    val description: String,
    val formula: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val clue: String,
    val explanationSteps: List<Pair<String, String>>
)

// ── Centralized Dynamic Question Repository (Multiple Questions Per Node) ────
val subjectQuestions = mapOf(
    "Math" to mapOf(
        0 to mapOf( // Unit 1
            0 to listOf( // Node 0: Persamaan Kuadrat
                QuizQuestion(
                    title = "Persamaan Kuadrat (1/2)",
                    description = "Jika akar-akar persamaan kuadrat x^2 - 5x + 6 = 0 adalah x_1 dan x_2, berapakah nilai x_1 + x_2?",
                    formula = "x^2 - 5x + 6 = 0",
                    options = listOf("5", "-5", "6", "-6"),
                    correctAnswerIndex = 0,
                    clue = "Ingat rumus Vieta untuk jumlah akar: x_1 + x_2 = -b/a.",
                    explanationSteps = listOf(
                        "Gunakan Rumus Vieta:" to "x_1 + x_2 = -b/a",
                        "Substitusi nilai koefisien (a = 1, b = -5):" to "x_1 + x_2 = -(-5)/1 = 5"
                    )
                ),
                QuizQuestion(
                    title = "Persamaan Kuadrat (2/2)",
                    description = "Dari persamaan kuadrat x^2 - 5x + 6 = 0, berapakah hasil perkalian kedua akar-akarnya (x_1 * x_2)?",
                    formula = "x^2 - 5x + 6 = 0",
                    options = listOf("6", "5", "-6", "2"),
                    correctAnswerIndex = 0,
                    clue = "Ingat rumus Vieta untuk perkalian akar: x_1 * x_2 = c/a.",
                    explanationSteps = listOf(
                        "Gunakan Rumus Vieta:" to "x_1 * x_2 = c/a",
                        "Substitusi nilai koefisien (a = 1, c = 6):" to "x_1 * x_2 = 6/1 = 6"
                    )
                )
            ),
            1 to listOf( // Node 1: Sistem Persamaan Linear
                QuizQuestion(
                    title = "Sistem Persamaan Linear (1/2)",
                    description = "Diberikan sistem persamaan x + y = 5 dan 2x - y = 4. Tentukan hasil kali x * y!",
                    formula = "x + y = 5 , 2x - y = 4",
                    options = listOf("6", "4", "8", "10"),
                    correctAnswerIndex = 0,
                    clue = "Gunakan metode eliminasi dengan menjumlahkan kedua persamaan untuk mengeliminasi y.",
                    explanationSteps = listOf(
                        "Jumlahkan kedua persamaan:" to "(x + y) + (2x - y) = 5 + 4 -> 3x = 9 -> x = 3",
                        "Substitusi x ke persamaan pertama:" to "3 + y = 5 -> y = 2",
                        "Hitung hasil kali x * y:" to "3 * 2 = 6"
                    )
                ),
                QuizQuestion(
                    title = "Sistem Persamaan Linear (2/2)",
                    description = "Dari sistem persamaan yang sama, berapakah nilai dari x - y?",
                    formula = "x + y = 5 , 2x - y = 4",
                    options = listOf("1", "2", "0", "-1"),
                    correctAnswerIndex = 0,
                    clue = "Cari nilai x dan y terlebih dahulu, kemudian hitung selisihnya.",
                    explanationSteps = listOf(
                        "Kita sudah mendapatkan nilai x = 3 dan y = 2 dari langkah sebelumnya." to "",
                        "Hitung selisih x - y:" to "3 - 2 = 1"
                    )
                )
            ),
            2 to listOf( // Node 2: Fungsi Kuadrat
                QuizQuestion(
                    title = "Fungsi Kuadrat (1/2)",
                    description = "Tentukan titik puncak (vertex) dari fungsi kuadrat f(x) = x^2 - 4x + 5!",
                    formula = "f(x) = x^2 - 4x + 5",
                    options = listOf("(2, 1)", "(2, 5)", "(-2, 1)", "(1, 2)"),
                    correctAnswerIndex = 0,
                    clue = "Gunakan rumus sumbu simetri x_p = -b/2a, lalu substitusikan ke f(x) untuk mencari y_p.",
                    explanationSteps = listOf(
                        "Cari nilai x puncak menggunakan x = -b/2a:" to "x = -(-4) / (2*1) = 2",
                        "Substitusi x = 2 ke dalam fungsi f(x):" to "y = 2^2 - 4*2 + 5 = 4 - 8 + 5 = 1",
                        "Titik puncak diperoleh:" to "(x_p, y_p) = (2, 1)"
                    )
                ),
                QuizQuestion(
                    title = "Fungsi Kuadrat (2/2)",
                    description = "Tentukan titik potong dengan sumbu y untuk fungsi kuadrat f(x) = x^2 - 4x + 5!",
                    formula = "f(x) = x^2 - 4x + 5",
                    options = listOf("(0, 5)", "(0, -5)", "(0, 4)", "(5, 0)"),
                    correctAnswerIndex = 0,
                    clue = "Titik potong sumbu y terjadi saat nilai x = 0.",
                    explanationSteps = listOf(
                        "Substitusikan nilai x = 0 ke dalam f(x):" to "f(0) = 0^2 - 4(0) + 5 = 5",
                        "Maka titik potong sumbu y adalah:" to "(0, 5)"
                    )
                )
            )
        ),
        1 to mapOf( // Unit 2
            0 to listOf( // Node 0
                QuizQuestion(
                    title = "Identitas Trigonometri (1/2)",
                    description = "Jika sin(x) = 3/5 dan x adalah sudut lancip, berapakah nilai cos(x)?",
                    formula = "sin(x) = 3/5",
                    options = listOf("4/5", "3/4", "5/4", "2/5"),
                    correctAnswerIndex = 0,
                    clue = "Gunakan Identitas Trigonometri Dasar: sin^2(x) + cos^2(x) = 1.",
                    explanationSteps = listOf(
                        "Gunakan Identitas Trigonometri Dasar:" to "sin^2(x) + cos^2(x) = 1",
                        "Substitusi sin(x):" to "(3/5)^2 + cos^2(x) = 1 -> 9/25 + cos^2(x) = 1",
                        "Hitung nilai cos(x):" to "cos^2(x) = 16/25 -> cos(x) = 4/5"
                    )
                ),
                QuizQuestion(
                    title = "Identitas Trigonometri (2/2)",
                    description = "Dengan nilai sin(x) = 3/5 dan cos(x) = 4/5, berapakah nilai dari tan(x)?",
                    formula = "sin(x) = 3/5, cos(x) = 4/5",
                    options = listOf("3/4", "4/3", "3/5", "4/5"),
                    correctAnswerIndex = 0,
                    clue = "Ingat definisi tan(x) = sin(x) / cos(x).",
                    explanationSteps = listOf(
                        "Substitusi nilai sin(x) dan cos(x):" to "tan(x) = (3/5) / (4/5)",
                        "Sederhanakan pecahan:" to "tan(x) = 3/4"
                    )
                )
            ),
            1 to listOf( // Node 1
                QuizQuestion(
                    title = "Geometri Lingkaran (1/2)",
                    description = "Tentukan panjang jari-jari lingkaran dengan persamaan x^2 + y^2 - 4x + 6y - 12 = 0!",
                    formula = "x^2 + y^2 - 4x + 6y - 12 = 0",
                    options = listOf("5", "4", "6", "7"),
                    correctAnswerIndex = 0,
                    clue = "Ubah ke bentuk standar lingkaran (x-a)^2 + (y-b)^2 = r^2.",
                    explanationSteps = listOf(
                        "Ubah ke bentuk standar lingkaran (x-a)^2 + (y-b)^2 = r^2:" to "(x-2)^2 - 4 + (y+3)^2 - 9 - 12 = 0",
                        "Sederhanakan persamaan:" to "(x-2)^2 + (y+3)^2 = 25",
                        "Tentukan jari-jari r:" to "r^2 = 25 -> r = 5"
                    )
                ),
                QuizQuestion(
                    title = "Geometri Lingkaran (2/2)",
                    description = "Tentukan pusat lingkaran dari persamaan x^2 + y^2 - 4x + 6y - 12 = 0!",
                    formula = "x^2 + y^2 - 4x + 6y - 12 = 0",
                    options = listOf("(2, -3)", "(-2, 3)", "(4, -6)", "(2, 3)"),
                    correctAnswerIndex = 0,
                    clue = "Pusat lingkaran (a, b) diperoleh dari bentuk standar (x-a)^2 + (y-b)^2 = r^2.",
                    explanationSteps = listOf(
                        "Persamaan lingkaran standar:" to "(x-2)^2 + (y+3)^2 = 25",
                        "Bandingkan dengan (x-a)^2 + (y-b)^2 = r^2:" to "a = 2, b = -3",
                        "Pusat lingkaran adalah:" to "(2, -3)"
                    )
                )
            )
        )
    ),
    "Physics" to mapOf(
        0 to mapOf(
            0 to listOf(
                QuizQuestion(
                    title = "Kinematika Gerak Lurus (1/2)",
                    description = "Sebuah benda bergerak dengan persamaan posisi s(t) = 2t^2 + 5t + 3 meter. Berapakah kecepatan benda pada saat t = 3 sekon?",
                    formula = "s(t) = 2t^2 + 5t + 3",
                    options = listOf("17 m/s", "11 m/s", "20 m/s", "15 m/s"),
                    correctAnswerIndex = 0,
                    clue = "Turunkan persamaan posisi s(t) terhadap waktu t untuk mendapatkan persamaan kecepatan v(t) = ds/dt.",
                    explanationSteps = listOf(
                        "Dapatkan fungsi kecepatan dengan menurunkan fungsi posisi:" to "v(t) = ds/dt = 4t + 5",
                        "Substitusi nilai t = 3 sekon:" to "v(3) = 4*3 + 5 = 17 m/s"
                    )
                ),
                QuizQuestion(
                    title = "Kinematika Gerak Lurus (2/2)",
                    description = "Berapakah percepatan benda tersebut pada saat t = 3 sekon?",
                    formula = "s(t) = 2t^2 + 5t + 3",
                    options = listOf("4 m/s^2", "2 m/s^2", "5 m/s^2", "0 m/s^2"),
                    correctAnswerIndex = 0,
                    clue = "Percepatan adalah turunan pertama dari fungsi kecepatan v(t), atau turunan kedua dari fungsi posisi s(t).",
                    explanationSteps = listOf(
                        "Dapatkan fungsi percepatan dengan menurunkan fungsi kecepatan:" to "a(t) = dv/dt = d(4t+5)/dt = 4",
                        "Karena konstan, percepatannya adalah:" to "4 m/s^2"
                    )
                )
            ),
            1 to listOf(
                QuizQuestion(
                    title = "Hukum Newton & Gaya Gesek (1/2)",
                    description = "Sebuah balok bermassa 5 kg ditarik dengan gaya 20 N di atas lantai kasar dengan koefisien gesek kinetik 0.2. Berapakah percepatan balok? (g = 10 m/s^2)",
                    formula = "m = 5 kg, F = 20 N, mu_k = 0.2",
                    options = listOf("2 m/s^2", "3 m/s^2", "4 m/s^2", "1 m/s^2"),
                    correctAnswerIndex = 0,
                    clue = "Hitung gaya gesek kinetik f_k terlebih dahulu menggunakan rumus f_k = mu_k * N, lalu gunakan Hukum II Newton.",
                    explanationSteps = listOf(
                        "Hitung gaya normal balok:" to "N = m * g = 5 * 10 = 50 N",
                        "Hitung gaya gesek kinetik:" to "f_k = mu_k * N = 0.2 * 50 = 10 N",
                        "Hitung percepatan menggunakan Hukum II Newton:" to "a = (F - f_k) / m = (20 - 10) / 5 = 2 m/s^2"
                    )
                ),
                QuizQuestion(
                    title = "Hukum Newton & Gaya Gesek (2/2)",
                    description = "Jika koefisien gesek lantai diubah menjadi 0 (lantai licin sempurna), berapakah percepatan balok sekarang?",
                    formula = "m = 5 kg, F = 20 N, mu_k = 0",
                    options = listOf("4 m/s^2", "5 m/s^2", "2 m/s^2", "3 m/s^2"),
                    correctAnswerIndex = 0,
                    clue = "Karena lantai licin (mu_k = 0), gaya gesek kinetik f_k = 0.",
                    explanationSteps = listOf(
                        "Hukum II Newton saat f_k = 0:" to "a = F / m",
                        "Substitusi nilai:" to "a = 20 / 5 = 4 m/s^2"
                    )
                )
            )
        )
    ),
    "Chemistry" to mapOf(
        0 to mapOf(
            0 to listOf(
                QuizQuestion(
                    title = "Konfigurasi Elektron (1/2)",
                    description = "Tentukan keempat bilangan kuantum untuk elektron terakhir pada atom besi dengan nomor atom 26!",
                    formula = "Fe (Z = 26)",
                    options = listOf("n=3, l=2, m=-2, s=-1/2", "n=4, l=0, m=0, s=+1/2", "n=3, l=1, m=+1, s=-1/2", "n=3, l=2, m=0, s=+1/2"),
                    correctAnswerIndex = 0,
                    clue = "Tuliskan konfigurasi elektron Fe terlebih dahulu. Elektron terakhir masuk ke subkulit 3d.",
                    explanationSteps = listOf(
                        "Tuliskan konfigurasi elektron Fe:" to "[Ar] 4s^2 3d^6",
                        "Tentukan bilangan kuantum utama (n) dan azimut (l):" to "n = 3 (kulit d), l = 2 (orbital d)",
                        "Isi 6 elektron ke dalam 5 orbital d (m = -2, -1, 0, +1, +2):" to "Elektron ke-6 menempati orbital m = -2 dengan spin berlawanan (s = -1/2)"
                    )
                ),
                QuizQuestion(
                    title = "Konfigurasi Elektron (2/2)",
                    description = "Berapakah jumlah elektron yang tidak berpasangan (unpaired electrons) pada atom besi (Fe)?",
                    formula = "Fe (Z = 26)",
                    options = listOf("4", "5", "6", "2"),
                    correctAnswerIndex = 0,
                    clue = "Orbital d memiliki 5 ruang. Masukkan 6 elektron ke dalam 5 ruang tersebut sesuai Aturan Hund.",
                    explanationSteps = listOf(
                        "Aturan Hund mengisi orbital d (5 orbital):" to "1 orbital terisi berpasangan (2 elektron), 4 orbital terisi tunggal (4 elektron)",
                        "Maka jumlah elektron tidak berpasangan:" to "4 elektron"
                    )
                )
            )
        )
    )
)

enum class FeedbackState { NONE, CORRECT, WRONG }

@Composable
fun QuizScreen(
    subject: String = "Math",
    unitIndex: Int = 0,
    nodeIndex: Int = 0,
    onCloseClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    val subjectData = subjectQuestions[subject] ?: subjectQuestions["Math"]!!
    val unitData = subjectData[unitIndex] ?: subjectData[0]!!
    val questionsList = unitData[nodeIndex] ?: unitData[0]!!

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questionsList.getOrElse(currentQuestionIndex) { questionsList.first() }

    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var feedbackState by remember { mutableStateOf(FeedbackState.NONE) }
    var lives by remember { mutableIntStateOf(3) }
    var showClueDialog by remember { mutableStateOf(false) }

    // Gamification & EXP Tracking variables
    var showCompletionScreen by remember { mutableStateOf(false) }
    var isClaimingExp by remember { mutableStateOf(false) }
    var lightningBonuses by remember { mutableIntStateOf(0) }
    var perfectStreakBonus by remember { mutableStateOf(false) }
    var consecutiveCorrectAnswers by remember { mutableIntStateOf(0) }
    var questionStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val earnedRewards = remember { mutableStateListOf<Pair<String, Int>>() }
    // Accuracy tracking: correct answers / total answers attempted in this session
    var correctAnswers by remember { mutableIntStateOf(0) }
    var totalAnswers by remember { mutableIntStateOf(0) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val apiService = remember { com.davinza.nalar.data.remote.ApiClient.getApiService(context) }

    LaunchedEffect(showCompletionScreen) {
        if (showCompletionScreen) {
            isClaimingExp = true
            earnedRewards.clear()
            
            val nodeKey = "${subject}_${unitIndex + 1}_${nodeIndex}"
            val isAlreadyCompleted = UserProgressManager.completedNodes.contains(nodeKey)
            
            // 1. Base Complete/Review EXP
            val baseType = if (isAlreadyCompleted) "review_session" else "difficulty_medium"
            val basePoints = if (isAlreadyCompleted) 20 else 10
            val baseLabel = if (isAlreadyCompleted) "Ulang Soal (Review)" else "Latihan Soal"
            
            try {
                val res = apiService.claimExp(
                    com.davinza.nalar.data.remote.model.ClaimExpRequest(
                        activityType = baseType,
                        correct = correctAnswers,
                        total = totalAnswers
                    )
                )
                if (res.isSuccessful && res.body()?.success == true) {
                    earnedRewards.add(baseLabel to basePoints)
                }
            } catch (e: Exception) { e.printStackTrace() }
            
            // 2. Lightning Accuracy Bonus (if earned)
            if (lightningBonuses > 0) {
                try {
                    val res = apiService.claimExp(com.davinza.nalar.data.remote.model.ClaimExpRequest("lightning_accuracy"))
                    if (res.isSuccessful && res.body()?.success == true) {
                        earnedRewards.add("Bonus Kilat (Lightning)" to 15)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            
            // 3. Perfect Streak Bonus (if earned)
            if (perfectStreakBonus) {
                try {
                    val res = apiService.claimExp(com.davinza.nalar.data.remote.model.ClaimExpRequest("perfect_streak"))
                    if (res.isSuccessful && res.body()?.success == true) {
                        earnedRewards.add("Bonus Sempurna (Perfect Streak)" to 25)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            
            // 4. Flawless Session Bonus (if earned)
            if (lives == 3) {
                try {
                    val res = apiService.claimExp(com.davinza.nalar.data.remote.model.ClaimExpRequest("flawless_session"))
                    if (res.isSuccessful && res.body()?.success == true) {
                        earnedRewards.add("Sesi Tanpa Cacat (Flawless)" to 50)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            
            // Mark node completed locally
            UserProgressManager.completeNode(subject, unitIndex + 1, nodeIndex)
            isClaimingExp = false
            // Notify Profile & Leaderboard screens to refresh their data in realtime
            AppEventBus.emitQuizCompleted()
        }
    }

    val progress = ((currentQuestionIndex + 1).toFloat() / questionsList.size).coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        
        // ── Main Content ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorBackground)
        ) {
            // Top App Bar with customized Clue click listener
            QuizHeader(
                progress = progress,
                lives = lives,
                onCloseClick = onCloseClick,
                onClueClick = { showClueDialog = true }
            )

            // Main Content Canvas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentQuestion.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorOnBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = formatMathText(currentQuestion.description),
                    fontSize = 17.sp,
                    color = ColorOnSurfaceVariant,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 20.dp),
                    textAlign = TextAlign.Center
                )

                // Equation Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ColorSurfaceContainerLowest)
                        .border(2.dp, ColorSurfaceContainer, RoundedCornerShape(16.dp))
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatMathText(currentQuestion.formula),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Options List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val prefixes = listOf("A", "B", "C", "D")
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isOptionSelected = selectedOption == index
                        val isWrongSelection = feedbackState == FeedbackState.WRONG && isOptionSelected
                        
                        AnswerButton(
                            prefix = prefixes[index],
                            text = option,
                            isSelected = isOptionSelected,
                            isWrong = isWrongSelection,
                            onClick = {
                                if (feedbackState == FeedbackState.NONE) {
                                    selectedOption = index
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Action Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorBackground)
                    .border(width = 1.dp, color = ColorSurfaceContainer)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                CheckAnswerButton(onClick = {
                if (selectedOption != null && feedbackState == FeedbackState.NONE) {
                        val isCorrect = selectedOption == currentQuestion.correctAnswerIndex
                        totalAnswers++
                        if (isCorrect) {
                            feedbackState = FeedbackState.CORRECT
                            correctAnswers++
                            val timeTakenSec = (System.currentTimeMillis() - questionStartTime) / 1000
                            if (timeTakenSec <= 5) {
                                lightningBonuses++
                            }
                            consecutiveCorrectAnswers++
                            if (consecutiveCorrectAnswers >= 5) {
                                perfectStreakBonus = true
                            }
                        } else {
                            feedbackState = FeedbackState.WRONG
                            if (lives > 0) lives -= 1
                            consecutiveCorrectAnswers = 0
                        }
                    }
                })
            }
        }

        // ── Dim Overlay ──────────────────────────────────────────────────
        if (feedbackState != FeedbackState.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorPrimary.copy(alpha = 0.2f))
                    .zIndex(10f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept clicks
                    )
            )
        }

        // ── Feedback Bottom Sheets ───────────────────────────────────────
        AnimatedVisibility(
            visible = feedbackState != FeedbackState.NONE,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(20f)
        ) {
            if (feedbackState == FeedbackState.CORRECT) {
                CorrectFeedbackSheet(
                    question = currentQuestion,
                    onContinueClick = {
                        feedbackState = FeedbackState.NONE
                        selectedOption = null
                        if (currentQuestionIndex < questionsList.lastIndex) {
                            currentQuestionIndex++
                            questionStartTime = System.currentTimeMillis()
                        } else {
                            // Completed all unit node questions successfully!
                            showCompletionScreen = true
                        }
                    }
                )
            } else if (feedbackState == FeedbackState.WRONG) {
                WrongFeedbackSheet(
                    question = currentQuestion,
                    onGotItClick = {
                        feedbackState = FeedbackState.NONE
                        if (lives <= 0) {
                            onCloseClick() // Kicked out if lives depleted
                        }
                    }
                )
            }
        }

        // ── Clue Dialog ──────────────────────────────────────────────────
        if (showClueDialog) {
            Dialog(onDismissRequest = { showClueDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF9C4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(24.dp)) {
                                val w = size.width; val h = size.height
                                drawCircle(Color(0xFFFFD54F), w*.25f, Offset(w*.5f, h*.4f))
                                val path = Path().apply {
                                    moveTo(w*.35f, h*.4f)
                                    lineTo(w*.35f, h*.75f)
                                    lineTo(w*.65f, h*.75f)
                                    lineTo(w*.65f, h*.4f)
                                }
                                drawPath(path, Color(0xFFFFD54F))
                                drawLine(Color(0xFF5E5E5E), Offset(w*.35f, h*.75f), Offset(w*.65f, h*.75f), 2.dp.toPx())
                                drawLine(Color(0xFF5E5E5E), Offset(w*.4f, h*.83f), Offset(w*.6f, h*.83f), 2.dp.toPx())
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Petunjuk (Clue)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB300)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = formatMathText(currentQuestion.clue),
                            fontSize = 15.sp,
                            color = ColorOnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        ThreeDButton(
                            onClick = { showClueDialog = false },
                            shadowHeight = 4.dp,
                            shadowColor = Color(0xFFCC9600),
                            faceColor = Color(0xFFFFD54F),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Saya Mengerti", color = Color(0xFF5E5E5E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showCompletionScreen) {
            QuizCompletionScreen(
                subject = subject,
                unitIndex = unitIndex,
                nodeIndex = nodeIndex,
                lives = lives,
                lightningBonuses = lightningBonuses,
                perfectStreakBonus = perfectStreakBonus,
                earnedRewards = earnedRewards,
                isClaimingExp = isClaimingExp,
                onContinueClick = onContinueClick
            )
        }
    }
}

// ── Shared components ──────────────────────────────────────────────────

@Composable
fun QuizHeader(
    progress: Float,
    lives: Int,
    onCloseClick: () -> Unit,
    onClueClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close",
            tint = ColorSurfaceTint,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable { onCloseClick() }
                .padding(4.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(ColorSurfaceContainerHigh)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(50))
                    .background(ColorSecondaryContainer)
            )
        }

        // Bouncy Yellow 3D Clue (Lightbulb) Button
        ClueHeaderButton(onClick = onClueClick)

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$lives",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorPrimary
            )
            Image(
                painter = painterResource(id = R.drawable.ic_key),
                contentDescription = "Lives",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ClueHeaderButton(onClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val pressOffset = remember { Animatable(0f) }
    val shadowDp = 3.dp

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    coroutineScope.launch {
                        pressOffset.animateTo(1f, tween(60, easing = LinearEasing))
                        pressOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                        onClick()
                    }
                }
            )
            .size(width = 36.dp, height = 36.dp + shadowDp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = shadowDp)
                .background(Color(0xFFCC9600), CircleShape)
        )
        // Face Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowDp * pressOffset.value)
                .background(Color(0xFFFFD54F), CircleShape)
                .border(1.5.dp, Color(0xFFFFB300), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(16.dp)) {
                val w = size.width; val h = size.height
                drawCircle(Color.White, w*.25f, Offset(w*.5f, h*.4f))
                val path = Path().apply {
                    moveTo(w*.35f, h*.4f)
                    lineTo(w*.35f, h*.75f)
                    lineTo(w*.65f, h*.75f)
                    lineTo(w*.65f, h*.4f)
                }
                drawPath(path, Color.White)
                drawLine(Color(0xFF5E5E5E), Offset(w*.35f, h*.75f), Offset(w*.65f, h*.75f), 1.5.dp.toPx())
                drawLine(Color(0xFF5E5E5E), Offset(w*.4f, h*.83f), Offset(w*.6f, h*.83f), 1.5.dp.toPx())
            }
        }
    }
}

@Composable
fun AnswerButton(
    prefix: String,
    text: String,
    isSelected: Boolean,
    isWrong: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        label = "answerBtnOffset"
    )

    val bgColor = if (isWrong) ColorErrorContainer else if (isSelected) ColorSecondaryFixed else ColorSurfaceContainerLowest
    val borderColor = if (isWrong) ColorError else if (isSelected) ColorSecondaryContainer else ColorOutlineVariant
    val textColor = if (isWrong) ColorError else if (isSelected) ColorSecondaryContainer else ColorOnBackground
    
    val shadowColor = borderColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(60.dp)
                .background(shadowColor, RoundedCornerShape(12.dp))
        )
        // Top Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .offset(y = yOffset)
                .background(bgColor, RoundedCornerShape(12.dp))
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
             Row(
                 modifier = Modifier.fillMaxWidth().padding(end = if(isWrong) 8.dp else 0.dp),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Box(
                         modifier = Modifier
                             .size(32.dp)
                             .border(2.dp, if (isSelected && !isWrong) ColorSecondaryContainer else ColorOutlineVariant, CircleShape)
                             .background(if (isSelected && !isWrong) ColorSecondaryContainer else Color.Transparent, CircleShape),
                         contentAlignment = Alignment.Center
                     ) {
                         Text(
                             text = prefix,
                             fontSize = 14.sp,
                             fontWeight = FontWeight.Bold,
                             color = if (isSelected && !isWrong) Color.White else ColorSurfaceTint
                         )
                     }
                     Spacer(modifier = Modifier.width(16.dp))
                     Text(
                         text = formatMathText(text),
                         fontSize = 18.sp,
                         fontWeight = FontWeight.SemiBold,
                         color = textColor
                     )
                 }

                 if (isWrong) {
                     Icon(
                         imageVector = Icons.Filled.Close,
                         contentDescription = "Wrong",
                         tint = ColorError,
                         modifier = Modifier.size(24.dp)
                     )
                 }
             }
        }
    }
}

@Composable
fun CheckAnswerButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        label = "checkBtnOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(52.dp)
                .background(ColorTertiaryBorder, RoundedCornerShape(16.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = yOffset)
                .background(ColorTertiaryFixed, RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PERIKSA",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = ColorTertiaryContainer,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CorrectFeedbackSheet(
    question: QuizQuestion,
    onContinueClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(ColorFeedbackBg)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ColorSurfaceContainerLowest)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Face,
                        contentDescription = "Happy",
                        tint = ColorTertiaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Sparkle",
                        tint = ColorSparkle,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Luar Biasa!",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorTertiaryContainer,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = "Jawaban kamu benar sekali.",
                        fontSize = 16.sp,
                        color = ColorOnTertiaryFixedVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Explanation Card with dynamic math support
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = ColorTertiaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cara Pengerjaan:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTertiaryContainer
                        )
                    }

                    question.explanationSteps.forEach { step ->
                        Text(
                            text = formatMathText(step.first),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorOnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorSurfaceContainerLowest)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = formatMathText(step.second),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button (3D)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val yOffset by animateDpAsState(
                targetValue = if (isPressed) 4.dp else 0.dp,
                label = "continueBtnOffset"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onContinueClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(52.dp)
                        .background(ColorFeedbackBtnShadow, RoundedCornerShape(16.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = yOffset)
                        .background(ColorFeedbackBtnBg, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LANJUTKAN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorOnTertiaryFixed,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WrongFeedbackSheet(
    question: QuizQuestion,
    onGotItClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(ColorErrorContainer)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Sad",
                        tint = ColorError,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Kurang Tepat!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorOnErrorContainer,
                        lineHeight = 32.sp
                    )
                    Text(
                        text = "Jangan menyerah, coba lagi ya!",
                        fontSize = 15.sp,
                        color = ColorOnErrorContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Got it / Try again Button (3D)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val yOffset by animateDpAsState(
                targetValue = if (isPressed) 4.dp else 0.dp,
                label = "gotItBtnOffset"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onGotItClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(52.dp)
                        .background(ColorOnErrorContainer, RoundedCornerShape(16.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = yOffset)
                        .background(ColorError, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MENGERTI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorOnError,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// ── Satisfying 3D Button helper ───────────────────────────────────────────
@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shadowHeight: Dp = 8.dp,
    shadowColor: Color,
    faceColor: Color,
    shape: RoundedCornerShape = CircleShape,
    content: @Composable BoxScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pressOffset = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    coroutineScope.launch {
                        pressOffset.animateTo(1f, tween(70, easing = LinearEasing))
                        pressOffset.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                        onClick()
                    }
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = shadowHeight)
                .background(color = shadowColor, shape = shape)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowHeight * pressOffset.value)
                .background(color = faceColor, shape = shape),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
fun QuizCompletionScreen(
    subject: String,
    unitIndex: Int,
    nodeIndex: Int,
    lives: Int,
    lightningBonuses: Int,
    perfectStreakBonus: Boolean,
    earnedRewards: List<Pair<String, Int>>,
    isClaimingExp: Boolean,
    onContinueClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Trophy or Crown Visual Icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFFFF9C4), CircleShape)
                    .border(4.dp, Color(0xFFFFD54F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_league),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Latihan Selesai! 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorOnBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kerja bagus! Kamu berhasil menyelesaikan tantangan ini.",
                fontSize = 16.sp,
                color = ColorOnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ColorSurfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎯 Akurasi", fontSize = 14.sp, color = ColorOnSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("100%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorSecondaryContainer)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ColorSurfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("❤️ Sisa Nyawa", fontSize = 14.sp, color = ColorOnSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$lives/3", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorError)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rewards Breakdown List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, ColorSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rincian EXP Diperoleh",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorOnBackground
                    )

                    HorizontalDivider(color = ColorSurfaceContainer, thickness = 1.dp)

                    if (isClaimingExp) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ColorSecondaryContainer)
                        }
                    } else {
                        if (earnedRewards.isEmpty()) {
                            Text(
                                text = "Tidak ada bonus tambahan saat ini.",
                                fontSize = 14.sp,
                                color = ColorOnSurfaceVariant
                            )
                        } else {
                            earnedRewards.forEach { (label, points) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val iconRes = when(label) {
                                            "Bonus Kilat (Lightning)" -> R.drawable.ic_exp
                                            "Bonus Sempurna (Perfect Streak)" -> R.drawable.ic_streak
                                            "Sesi Tanpa Cacat (Flawless)" -> R.drawable.ic_star
                                            else -> null
                                        }
                                        if (iconRes != null) {
                                            Image(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = label,
                                                modifier = Modifier.size(24.dp).padding(end = 8.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "📝",
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                        Text(
                                            text = label,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ColorOnBackground
                                        )
                                    }
                                    Text(
                                        text = "+$points EXP",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3EB550)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            ThreeDButton(
                onClick = onContinueClick,
                faceColor = Color(0xFFFFC000),
                shadowColor = Color(0xFFD49E00),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Lanjutkan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
