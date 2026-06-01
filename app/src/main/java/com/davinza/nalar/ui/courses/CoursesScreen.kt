package com.davinza.nalar.ui.courses

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.davinza.nalar.R
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.*
import com.davinza.nalar.data.local.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

// ── Color palette ─────────────────────────────────────────────────────────
private val ColorBackground           = Color(0xFFF9F9F9)
private val ColorSurface              = Color(0xFFFFFFFF)
private val ColorOutlineVariant       = Color(0xFFCFC4C5)
private val ColorOutline              = Color(0xFF7E7576)
private val ColorOnSurface            = Color(0xFF1A1C1C)
private val ColorOnSurfaceVariant     = Color(0xFF4C4546)
private val ColorPrimary              = Color(0xFF000000)
private val ColorOnPrimary            = Color(0xFFFFFFFF)
private val ColorSecondary            = Color(0xFF194BDF)
private val ColorOnSecondary          = Color(0xFFFFFFFF)
private val ColorSecondaryFixed       = Color(0xFF0037B9)
private val ColorSecondaryContainer   = Color(0xFF3E67F9)
private val ColorOnSecondaryContainer = Color(0xFFFFFFFF)
private val ColorTertiaryFixed        = Color(0xFF47E26A)
private val ColorTertiaryBorder       = Color(0xFF3EB550)
private val ColorSurfaceDim           = Color(0xFFDADADA)
private val ColorSurfaceContainer     = Color(0xFFEEEEEE)
private val ColorSurfaceVariant       = Color(0xFFE2E2E2)

// ── State Manager for Streak, Keys & Progress ──────────────────────────────
object UserProgressManager {
    private var context: Context? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var _streakCount by mutableIntStateOf(0)
    var streakCount: Int
        get() = _streakCount
        set(value) {
            if (_streakCount != value) {
                _streakCount = value
                persist()
            }
        }

    var streakActive by mutableStateOf(false)
    var streakRestoresLeft by mutableIntStateOf(3)

    private var _keysCount by mutableIntStateOf(2)
    var keysCount: Int
        get() = _keysCount
        set(value) {
            if (_keysCount != value) {
                _keysCount = value
                persist()
            }
        }

    private var _isPremium by mutableStateOf(false)
    var isPremium: Boolean
        get() = _isPremium
        set(value) {
            if (_isPremium != value) {
                _isPremium = value
                persist()
            }
        }
    
    val keysDisplay: String
        get() = if (isPremium) "∞" else keysCount.toString()
    
    // Tracks completed node IDs: e.g. "Math_1_0" (Subject_Unit_Node)
    val completedNodes = mutableStateListOf<String>()

    fun initialize(ctx: Context) {
        context = ctx.applicationContext
        scope.launch {
            ctx.dataStore.data.firstOrNull()?.let { prefs ->
                val userId = prefs[intPreferencesKey("user_id")] ?: 0
                val keySuffix = "_$userId"
                
                val loadedStreak = prefs[intPreferencesKey("streak_count$keySuffix")] ?: 0
                val loadedStreakActive = prefs[booleanPreferencesKey("streak_active$keySuffix")] ?: false
                val loadedStreakRestores = prefs[intPreferencesKey("streak_restores$keySuffix")] ?: 3
                val loadedKeys = prefs[intPreferencesKey("keys_count$keySuffix")] ?: 2
                val loadedPremium = prefs[intPreferencesKey("is_premium")] ?: 0
                val savedNodes = prefs[stringSetPreferencesKey("completed_nodes$keySuffix")] ?: emptySet()

                withContext(Dispatchers.Main) {
                    _streakCount = loadedStreak
                    streakActive = loadedStreakActive
                    streakRestoresLeft = loadedStreakRestores
                    _keysCount = loadedKeys
                    _isPremium = loadedPremium == 1
                    completedNodes.clear()
                    completedNodes.addAll(savedNodes)
                }
            }
        }
    }

    private fun persist() {
        val ctx = context ?: return
        scope.launch {
            ctx.dataStore.edit { prefs ->
                val userId = prefs[intPreferencesKey("user_id")] ?: 0
                val keySuffix = "_$userId"
                
                prefs[intPreferencesKey("streak_count$keySuffix")] = _streakCount
                prefs[booleanPreferencesKey("streak_active$keySuffix")] = streakActive
                prefs[intPreferencesKey("streak_restores$keySuffix")] = streakRestoresLeft
                prefs[intPreferencesKey("keys_count$keySuffix")] = _keysCount
                prefs[intPreferencesKey("is_premium")] = if (isPremium) 1 else 0
                prefs[stringSetPreferencesKey("completed_nodes$keySuffix")] = completedNodes.toSet()
            }
        }
    }
    
    fun completeNode(subject: String, unitNumber: Int, nodeIndex: Int) {
        val key = "${subject}_${unitNumber}_${nodeIndex}"
        if (!completedNodes.contains(key)) {
            completedNodes.add(key)
            if (!streakActive) {
                streakActive = true
                streakCount++
            }
            persist()
        }
    }
    
    fun useKey(): Boolean {
        if (isPremium) return true // Premium users have unlimited keys and do not consume them!
        if (keysCount > 0) {
            keysCount--
            persist()
            return true
        }
        return false
    }
    
    fun restoreStreak(): Boolean {
        if (streakRestoresLeft > 0 && !streakActive) {
            streakRestoresLeft--
            streakActive = true
            persist()
            return true
        }
        return false
    }
}

// ── Math Renderer ─────────────────────────────────────────────────────────
fun formatMathText(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val char = text[i]
            when {
                char == '^' && i + 1 < text.length -> {
                    withStyle(style = SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 12.sp, fontWeight = FontWeight.Bold)) {
                        append(text[i + 1])
                    }
                    i += 2
                }
                char == '_' && i + 1 < text.length -> {
                    withStyle(style = SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 11.sp)) {
                        append(text[i + 1])
                    }
                    i += 2
                }
                char == '*' -> {
                    append(" × ")
                    i++
                }
                char == '/' -> {
                    append(" ÷ ")
                    i++
                }
                else -> {
                    append(char)
                    i++
                }
            }
        }
    }
}

// ── Data models ───────────────────────────────────────────────────────────
enum class NodeState { COMPLETED, ACTIVE, LOCKED }

data class LearsonNode(
    val state: NodeState,
    val offsetDirection: Int = 0  // -1 left, 0 center, 1 right
)

data class LearningUnit(
    val number: Int,
    val title: String,
    val subtitle: String,
    val nodes: List<LearsonNode>,
    val isLocked: Boolean = false
)

// ── Dynamic Curriculum Data ───────────────────────────────────────────────
val mathUnits = listOf(
    LearningUnit(
        number = 1, title = "Unit 1", subtitle = "Aljabar & Persamaan Kuadrat",
        nodes = listOf(
            LearsonNode(NodeState.LOCKED, offsetDirection = -1), // Node 0
            LearsonNode(NodeState.LOCKED, offsetDirection =  1), // Node 1
            LearsonNode(NodeState.LOCKED, offsetDirection =  0), // Node 2
        )
    ),
    LearningUnit(
        number = 2, title = "Unit 2", subtitle = "Trigonometri & Geometri",
        nodes = listOf(
            LearsonNode(NodeState.LOCKED, offsetDirection = -1), // Node 0
            LearsonNode(NodeState.LOCKED, offsetDirection =  1), // Node 1
        )
    )
)

val physicsUnits = listOf(
    LearningUnit(
        number = 1, title = "Unit 1", subtitle = "Mekanika & Dinamika",
        nodes = listOf(
            LearsonNode(NodeState.LOCKED, offsetDirection = -1), // Node 0
            LearsonNode(NodeState.LOCKED, offsetDirection =  1), // Node 1
            LearsonNode(NodeState.LOCKED, offsetDirection =  0), // Node 2
        )
    )
)

val chemistryUnits = listOf(
    LearningUnit(
        number = 1, title = "Unit 1", subtitle = "Struktur Atom & Stoikiometri",
        nodes = listOf(
            LearsonNode(NodeState.LOCKED, offsetDirection = -1), // Node 0
            LearsonNode(NodeState.LOCKED, offsetDirection =  1), // Node 1
        )
    )
)

private val subjectTabs = listOf(
    Pair("Math", "calculate"),
    Pair("Physics", "science"),
    Pair("Chemistry", "experiment")
)

// ── Main Screen ───────────────────────────────────────────────────────────
@Composable
fun CoursesScreen(onNavigateToQuiz: (subject: String, unitIndex: Int, nodeIndex: Int) -> Unit = { _, _, _ -> }) {
    var selectedTab by remember { mutableStateOf(0) }
    val activeSubject = subjectTabs[selectedTab].first
    
    val units = remember(selectedTab) {
        when (activeSubject) {
            "Math" -> mathUnits
            "Physics" -> physicsUnits
            "Chemistry" -> chemistryUnits
            else -> mathUnits
        }
    }

    var showNoKeysDialog by remember { mutableStateOf(false) }
    var activeGuideInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        isLoading = true
        kotlinx.coroutines.delay(400)
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(ColorBackground)) {
        TopAppBarSection()
        SubjectTabRow(
            tabs = subjectTabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        if (isLoading) {
            CoursesSkeletonScreen()
        } else {
            // Scrollable learning path with a full-height dashed center line
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                // Dashed line spans the full scrollable content height
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val x = size.width / 2f
                            drawDashedLine(
                                color = ColorSurfaceVariant,
                                start = Offset(x, 0f),
                                end   = Offset(x, size.height),
                                strokeWidth = 8f,
                                dashLength  = 20f,
                                gapLength   = 14f
                            )
                        }
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    items(units.size) { i ->
                        LearningUnitSection(
                            subject = activeSubject,
                            unit = units[i],
                            allUnits = units,
                            onNodeClick = { sub, uIdx, nIdx ->
                                val nodeKey = "${sub}_${uIdx}_${nIdx}"
                                val isCompleted = UserProgressManager.completedNodes.contains(nodeKey)
                                if (isCompleted || UserProgressManager.isPremium || UserProgressManager.keysCount > 0) {
                                    if (!isCompleted) {
                                        UserProgressManager.useKey()
                                    }
                                    onNavigateToQuiz(sub, uIdx, nIdx)
                                } else {
                                    showNoKeysDialog = true
                                }
                            },
                            onGuideClick = { sub, unitNum ->
                                val guideContent = when (sub) {
                                    "Math" -> if (unitNum == 1) {
                                        "ax^2 + bx + c = 0" to "Persamaan Kuadrat:\n• Rumus ABC: x_1,_2 = (-b ± √(b^2 - 4ac)) / 2a\n• Jumlah Akar: x_1 + x_2 = -b/a\n• Kali Akar: x_1 * x_2 = c/a"
                                    } else {
                                        "sin^2(x) + cos^2(x) = 1" to "Identitas Trigonometri:\n• Tan(x) = sin(x) / cos(x)\n• Lingkaran Standar: x^2 + y^2 = r^2\n• Pusat Lingkaran (a,b): (x-a)^2 + (y-b)^2 = r^2"
                                    }
                                    "Physics" -> "v_t = v_0 + a*t" to "Kinematika & Dinamika:\n• Posisi GLBB: s = v_0*t + 0.5*a*t^2\n• Hukum II Newton: F = m*a\n• Gaya Gesek Kinetik: f_k = mu_k * N\n• Usaha: W = F * s\n• Energi Kinetik: Ek = 0.5 * m * v^2"
                                    "Chemistry" -> "n = massa / Mr" to "Stoikiometri & Atom:\n• Konfigurasi Kulit d: n=3, l=2 (orbital d)\n• Jumlah Mol: n = gram / Mr\n• Asam Kuat: [H^+] = M * valensi\n• Derajat Keasaman: pH = -log[H^+]"
                                    else -> "" to ""
                                }
                                activeGuideInfo = guideContent
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    // ── Warnings and Guide Dialogs ─────────────────────────────────────────
    if (showNoKeysDialog) {
        Dialog(onDismissRequest = { showNoKeysDialog = false }) {
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
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Alert",
                        tint = Color(0xFFBA1A1A),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Kunci Habis!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A1C1C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Anda tidak memiliki kunci untuk memulai latihan soal. Silakan tunggu pemulihan harian atau upgrade ke Premium!",
                        fontSize = 14.sp,
                        color = ColorOnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ThreeDButton(
                        onClick = { showNoKeysDialog = false },
                        shadowHeight = 4.dp,
                        shadowColor = Color(0xFF1240CC),
                        faceColor = ColorSecondary,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Saya Mengerti", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    activeGuideInfo?.let { guide ->
        Dialog(onDismissRequest = { activeGuideInfo = null }) {
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
                    Text(
                        "Lembar Formula",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorSecondary.copy(alpha = 0.08f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatMathText(guide.first),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = formatMathText(guide.second),
                        fontSize = 14.sp,
                        color = ColorOnSurface,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ThreeDButton(
                        onClick = { activeGuideInfo = null },
                        shadowHeight = 4.dp,
                        shadowColor = ColorTertiaryBorder,
                        faceColor = ColorTertiaryFixed,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Siap Belajar", color = Color(0xFF002107), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Top App Bar ───────────────────────────────────────────────────────────
@Composable
fun TopAppBarSection() {
    com.davinza.nalar.ui.components.AppTopBar(title = "Courses")
}

// ── Subject Tab Row ───────────────────────────────────────────────────────
@Composable
fun SubjectTabRow(tabs: List<Pair<String, String>>, selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    Surface(color = ColorBackground, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(tabs.size) { index ->
                val isSelected = index == selectedIndex
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) ColorPrimary else ColorSurfaceContainer)
                        .border(width = if (isSelected) 0.dp else 1.dp, color = ColorOutlineVariant, shape = CircleShape)
                        .clickable(interactionSource = interactionSource, indication = null) { onTabSelected(index) }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val ic = if (isSelected) ColorOnPrimary else ColorOnSurfaceVariant
                        TabIcon(type = tabs[index].second, color = ic, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(tabs[index].first, color = ic, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Tab Icon ──────────────────────────────────────────────────────────────
@Composable
fun TabIcon(type: String, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        when (type) {
            "calculate" -> {
                drawRect(color = color, topLeft = Offset(w*.2f,h*.2f), size = Size(w*.6f,h*.6f), style = Stroke(2.dp.toPx()))
                drawLine(color, Offset(w*.2f,h*.5f), Offset(w*.8f,h*.5f), 2.dp.toPx())
                drawCircle(color, 1.5.dp.toPx(), Offset(w*.35f,h*.65f))
                drawCircle(color, 1.5.dp.toPx(), Offset(w*.5f,h*.65f))
                drawCircle(color, 1.5.dp.toPx(), Offset(w*.65f,h*.65f))
            }
            "science" -> {
                drawCircle(color, w*.25f, Offset(w*.5f,h*.5f), style = Stroke(2.dp.toPx()))
                drawCircle(color, w*.25f, Offset(w*.38f,h*.5f), style = Stroke(1.5.dp.toPx()))
                drawCircle(color, w*.25f, Offset(w*.62f,h*.5f), style = Stroke(1.5.dp.toPx()))
            }
            "experiment" -> {
                val path = Path().apply {
                    moveTo(w*.45f,h*.2f); lineTo(w*.55f,h*.2f); lineTo(w*.55f,h*.42f)
                    lineTo(w*.8f,h*.78f); lineTo(w*.2f,h*.78f); lineTo(w*.45f,h*.42f); close()
                }
                drawPath(path, color, style = Stroke(2.dp.toPx()))
            }
            else -> drawCircle(color, w*.2f)
        }
    }
}

// ── Learning Unit Section ─────────────────────────────────────────────────
@Composable
fun LearningUnitSection(
    subject: String,
    unit: LearningUnit,
    allUnits: List<LearningUnit>,
    onNodeClick: (subject: String, unitIndex: Int, nodeIndex: Int) -> Unit,
    onGuideClick: (subject: String, unitNumber: Int) -> Unit
) {
    val previousUnits = allUnits.filter { it.number < unit.number }
    
    // Check if the previous unit is completely unlocked/completed
    val isUnitLocked = remember(UserProgressManager.completedNodes.size) {
        if (unit.number == 1) false
        else {
            val prev = previousUnits.find { it.number == unit.number - 1 }
            if (prev != null) {
                val lastNodeIdx = prev.nodes.size - 1
                val prevKey = "${subject}_${prev.number}_$lastNodeIdx"
                !UserProgressManager.completedNodes.contains(prevKey)
            } else true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isUnitLocked) 0.6f else 1f)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UnitHeaderCard(
            subject = subject,
            unitNumber = unit.number,
            title = unit.title,
            subtitle = unit.subtitle,
            isLocked = isUnitLocked,
            onGuideClick = { onGuideClick(subject, unit.number) }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            unit.nodes.forEachIndexed { nodeIndex, node ->
                val nodeState = remember(UserProgressManager.completedNodes.size) {
                    val key = "${subject}_${unit.number}_$nodeIndex"
                    if (UserProgressManager.completedNodes.contains(key)) {
                        NodeState.COMPLETED
                    } else {
                        // Sequential check: Build list of all keys before this node
                        val keysBefore = mutableListOf<String>()
                        for (u in allUnits) {
                            if (u.number < unit.number) {
                                u.nodes.forEachIndexed { idx, _ -> keysBefore.add("${subject}_${u.number}_$idx") }
                            } else if (u.number == unit.number) {
                                for (idx in 0 until nodeIndex) {
                                    keysBefore.add("${subject}_${u.number}_$idx")
                                }
                                break
                            }
                        }
                        val allPrevCompleted = keysBefore.all { UserProgressManager.completedNodes.contains(it) }
                        if (allPrevCompleted) NodeState.ACTIVE else NodeState.LOCKED
                    }
                }

                PathNode(
                    subject = subject,
                    unitNumber = unit.number,
                    nodeIndex = nodeIndex,
                    state = nodeState,
                    onClick = onNodeClick
                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ── Unit Header Card ──────────────────────────────────────────────────────
@Composable
fun UnitHeaderCard(
    subject: String,
    unitNumber: Int,
    title: String,
    subtitle: String,
    isLocked: Boolean,
    onGuideClick: () -> Unit
) {
    val bgColor     = if (isLocked) ColorSurfaceContainer else ColorSecondaryContainer
    val borderColor = if (isLocked) ColorSurfaceDim else Color(0xFF1240CC)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(bgColor)
                .drawBehind {
                    drawCircle(Color.White.copy(alpha = 0.08f), size.width*.35f, Offset(size.width*.85f, size.height*.2f))
                    drawCircle(Color.White.copy(alpha = 0.06f), size.width*.2f,  Offset(size.width*.1f,  size.height*.8f))
                }
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (isLocked) ColorOnSurface else ColorOnSecondaryContainer)
                Text(subtitle, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = if (isLocked) ColorOnSurfaceVariant else ColorOnSecondaryContainer.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (isLocked) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ColorSurfaceVariant),
                    contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.ic_key), contentDescription = "Locked",
                        modifier = Modifier.size(22.dp))
                }
            } else {
                GuideButton(subject = subject, onClick = onGuideClick)
            }
        }
    }
}

// ── 3D Button Wrapper (Satisfying, Organic bouncy state) ─────────────────────
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
                        // 1. Press down quickly
                        pressOffset.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 70, easing = LinearEasing)
                        )
                        // 2. Spring back up bouncy
                        pressOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                        // 3. Fire click callback
                        onClick()
                    }
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow Layer (static)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = shadowHeight)
                .background(color = shadowColor, shape = shape)
        )
        // Face Layer (animates offset down to cover shadow)
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

// ── Guide Button (3D) ─────────────────────────────────────────────────────
@Composable
fun GuideButton(subject: String, onClick: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    val pressOffset = remember { Animatable(0f) }
    val shadowDp = 4.dp

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
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowDp)
                .background(ColorSurfaceDim, RoundedCornerShape(12.dp))
        )
        // Face Layer
        Row(
            modifier = Modifier
                .offset(y = shadowDp * pressOffset.value)
                .background(ColorSurface, RoundedCornerShape(12.dp))
                .border(1.dp, ColorOutlineVariant, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val themeColor = when (subject) {
                "Math" -> ColorSecondary
                "Physics" -> Color(0xFFBA1A1A)
                "Chemistry" -> Color(0xFF3EB550)
                else -> ColorSecondary
            }
            Canvas(modifier = Modifier.size(16.dp)) {
                val w = size.width; val h = size.height
                drawRect(themeColor, Offset(0f, h*.1f), Size(w*.45f, h*.8f), style = Stroke(1.5.dp.toPx()))
                drawRect(themeColor, Offset(w*.55f, h*.1f), Size(w*.45f, h*.8f), style = Stroke(1.5.dp.toPx()))
                drawLine(themeColor, Offset(w*.5f, h*.1f), Offset(w*.5f, h*.9f), 1.5.dp.toPx())
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Guide", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = themeColor)
        }
    }
}

// ── Path Node ─────────────────────────────────────────────────────────────
@Composable
fun PathNode(
    subject: String,
    unitNumber: Int,
    nodeIndex: Int,
    state: NodeState,
    onClick: (subject: String, unitIndex: Int, nodeIndex: Int) -> Unit
) {
    val offsetX: Dp = when (nodeIndex) {
        0 -> (-48).dp
        1 ->   40.dp
        else -> 0.dp
    }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.offset(x = offsetX)) {
            when (state) {
                NodeState.COMPLETED -> CompletedNode(onClick = { onClick(subject, unitNumber - 1, nodeIndex) })
                NodeState.ACTIVE    -> ActiveNode(onClick = { onClick(subject, unitNumber - 1, nodeIndex) })
                NodeState.LOCKED    -> LockedNode()
            }
        }
    }
}

// ── Completed Node ────────────────────────────────────────────────────────
@Composable
fun CompletedNode(onClick: () -> Unit = {}) {
    val shadowDp = 8.dp
    ThreeDButton(
        onClick = onClick,
        shadowHeight = shadowDp,
        shadowColor = ColorTertiaryBorder,
        faceColor = ColorTertiaryFixed,
        modifier = Modifier.size(width = 84.dp, height = 84.dp + shadowDp)
    ) {
        Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = "Completed",
            modifier = Modifier.size(34.dp))
    }
}

// ── Active Node ───────────────────────────────────────────────────────────
@Composable
fun ActiveNode(onClick: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val tooltipFloat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(700, easing = EaseInOut), RepeatMode.Reverse),
        label = "tooltipBounce"
    )
    val shadowDp = 12.dp

    Column(
        modifier = Modifier.padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tooltip
        Column(
            modifier = Modifier
                .offset(y = tooltipFloat.dp)
                .zIndex(10f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(ColorPrimary)
                    .border(2.dp, ColorSurfaceVariant, RoundedCornerShape(14.dp))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text("START", color = ColorOnPrimary, fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 10.dp)
                    .drawBehind {
                        drawPath(Path().apply {
                            moveTo(0f, 0f); lineTo(size.width, 0f)
                            lineTo(size.width / 2f, size.height); close()
                        }, ColorPrimary)
                    }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play Button with Glow
        Box(
            modifier = Modifier.size(width = 104.dp, height = 104.dp + shadowDp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)
                    .background(ColorSecondary.copy(alpha = 0.18f), CircleShape)
            )
            ThreeDButton(
                onClick = onClick,
                shadowHeight = shadowDp,
                shadowColor = ColorSecondaryFixed,
                faceColor = ColorSecondary,
                modifier = Modifier.size(104.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(12.dp)
                        .border(5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )
                PlayIcon(modifier = Modifier.size(40.dp), color = ColorOnSecondary)
            }
        }
    }
}

// ── Locked Node ───────────────────────────────────────────────────────────
@Composable
fun LockedNode() {
    val shadowDp = 8.dp
    Box(
        modifier = Modifier
            .size(width = 84.dp, height = 84.dp + shadowDp)
            .alpha(0.8f),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .offset(y = shadowDp)
                .background(ColorSurfaceDim, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(84.dp)
                .background(ColorSurfaceVariant, CircleShape)
                .border(1.dp, ColorOutlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.ic_key), contentDescription = "Locked",
                modifier = Modifier.size(28.dp))
        }
    }
}

// ── Canvas Icons ──────────────────────────────────────────────────────────
@Composable
fun PlayIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        drawPath(Path().apply {
            moveTo(w*.35f, h*.22f); lineTo(w*.78f, h*.5f); lineTo(w*.35f, h*.78f); close()
        }, color)
    }
}

@Composable
fun FlameIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w*.5f, 0f)
            cubicTo(w*.75f, h*.25f, w*.9f, h*.45f, w*.85f, h*.72f)
            cubicTo(w*.8f, h*.98f, w*.2f, h*.98f, w*.15f, h*.72f)
            cubicTo(w*.1f, h*.45f, w*.25f, h*.25f, w*.5f, 0f); close()
        }
        drawPath(path, color)
    }
}

@Composable
fun KeyIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width; val h = size.height
        drawCircle(color, w*.2f, Offset(w*.4f, h*.4f), style = Stroke(2.dp.toPx()))
        val path = Path().apply {
            moveTo(w*.55f, h*.55f); lineTo(w*.85f, h*.85f)
            lineTo(w*.75f, h*.95f); lineTo(w*.65f, h*.85f)
            lineTo(w*.75f, h*.75f); lineTo(w*.65f, h*.65f)
            lineTo(w*.55f, h*.55f); close()
        }
        drawPath(path, color)
    }
}

// ── Dashed Line Helper ────────────────────────────────────────────────────
private fun DrawScope.drawDashedLine(
    color: Color, start: Offset, end: Offset,
    strokeWidth: Float, dashLength: Float, gapLength: Float
) {
    val paint = androidx.compose.ui.graphics.Paint().apply {
        this.color = color
        this.strokeWidth = strokeWidth
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
    }
    drawContext.canvas.drawLine(start, end, paint)
}

@Composable
fun CoursesSkeletonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Unit 1 Title
        com.davinza.nalar.ui.components.ShimmerBox(
            modifier = Modifier
                .width(180.dp)
                .height(24.dp),
            shape = RoundedCornerShape(6.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Node 1 (Center aligned layout shimmer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(56.dp), shape = CircleShape)
            Spacer(modifier = Modifier.width(16.dp))
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Node 2 (Center aligned layout shimmer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(100.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
            Spacer(modifier = Modifier.width(16.dp))
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(56.dp), shape = CircleShape)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Unit 2 Title
        com.davinza.nalar.ui.components.ShimmerBox(
            modifier = Modifier
                .width(140.dp)
                .height(24.dp),
            shape = RoundedCornerShape(6.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Node 3 (Center aligned layout shimmer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.size(56.dp), shape = CircleShape)
            Spacer(modifier = Modifier.width(16.dp))
            com.davinza.nalar.ui.components.ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp), shape = RoundedCornerShape(4.dp))
        }
    }
}
