package com.parsec.molfunnel

import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parsec.molfunnel.data.MolFunnelRepository
import com.parsec.molfunnel.model.AppBundle
import com.parsec.molfunnel.ui.screens.*
import com.parsec.molfunnel.ui.splash.AnimatedSplash
import com.parsec.molfunnel.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MolFunnelRepository(applicationContext)

        setContent {
            MolFunnelTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    AnimatedSplash(
                        appName = "Iris",
                        tagline = "Quantum drug discovery, filtered.",
                        onFinished = { showSplash = false }
                    )
                } else {
                    MainApp(repository)
                }
            }
        }
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    FUNNEL("Funnel", Icons.Default.FilterList),
    LEADS("Leads", Icons.Default.Science),
    LAB("Lab", Icons.Default.Tune),
    RESULTS("Results", Icons.Default.BarChart)
}

@Composable
fun MainApp(repository: MolFunnelRepository) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var bundle by remember { mutableStateOf(AppBundle()) }
    var isBackendOnline by remember { mutableStateOf(true) }
    var showServerDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        bundle = repository.getBundle()
        isBackendOnline = repository.isBackendReachable()
    }

    Scaffold(
        topBar = {
            Surface(
                color = CanvasBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.app_logo),
                            contentDescription = "Iris Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, HairlineBorder, RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "IRIS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = InkPrimary
                            )
                            Text(
                                text = "Quantum Drug Funnel",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = InkSecondary
                            )
                        }
                    }

                    // Live QpiAI Cloud Status Pill (Clickable for Server Settings)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isBackendOnline) Color(0xFFE8F8F5) else ChipBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isBackendOnline) Color(0xFF27AE60) else HairlineBorder),
                        modifier = Modifier.clickable { showServerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (isBackendOnline) Color(0xFF27AE60) else InkSecondary, CircleShape)
                            )
                            Text(
                                text = if (isBackendOnline) "QpiAI Online" else "Offline Local",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBackendOnline) Color(0xFF27AE60) else InkSecondary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AppleLiquidGlassBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                NavigationTab.HOME -> HomeScreen(bundle) { targetTabIdx ->
                    selectedTab = when (targetTabIdx) {
                        0 -> NavigationTab.FUNNEL
                        1 -> NavigationTab.LEADS
                        2 -> NavigationTab.LAB
                        3 -> NavigationTab.RESULTS
                        else -> NavigationTab.FUNNEL
                    }
                }
                NavigationTab.FUNNEL -> FunnelScreen(bundle)
                NavigationTab.LEADS -> LeadsScreen(bundle)
                NavigationTab.LAB -> LabScreen(bundle, repository)
                NavigationTab.RESULTS -> ResultsScreen(bundle)
            }
        }
    }
    // Backend Server Connection & Network Configuration Dialog
    if (showServerDialog) {
        var tempUrl by remember { mutableStateOf(repository.baseUrl) }
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = {
                Text(
                    text = "Server Connection Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Configure live backend endpoint (e.g. WiFi IP or ngrok public tunnel):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary
                    )
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        label = { Text("Base URL", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        text = "Default Emulator: http://10.0.2.2:8000/\nPhysical Phone: http://192.168.x.x:8000/ or https://tunnel.ngrok.app/",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSecondary,
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val formatted = if (tempUrl.endsWith("/")) tempUrl else "$tempUrl/"
                            repository.baseUrl = formatted
                            val online = repository.isBackendReachable()
                            isBackendOnline = online
                            if (online) {
                                bundle = repository.getBundle()
                                Toast.makeText(context, "Connected to backend successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Backend not reachable, operating in offline benchmark mode.", Toast.LENGTH_SHORT).show()
                            }
                            showServerDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InkPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CONNECT & TEST", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CardBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerDialog = false }) {
                    Text("CANCEL", style = MaterialTheme.typography.labelSmall, color = InkSecondary)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }
}


/**
 * Apple-style liquid glass bottom navigation bar with 100% opaque solid background.
 * Features an interactive draggable liquid slider capsule with specular glass gradients,
 * rubber-band squash-and-stretch physics, and fluid spring snapping.
 * Zero opacity or see-through blur so underlying scroll content never bleeds through.
 */
@Composable
fun AppleLiquidGlassBottomBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CanvasBg,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Apple Floating Capsule Dock (100% solid, opaque)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF222226), // Solid deep obsidian (100% opaque)
                                Color(0xFF141417),
                                Color(0xFF0F0F12)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3F3F48), // Top subtle rim highlight
                                Color(0xFF232328)  // Bottom edge rim
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                // Outer Top Specular Sheen line (Apple glass aesthetic, 100% solid)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF232328),
                                    Color(0xFF5A5A68),
                                    Color(0xFF232328)
                                )
                            )
                        )
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    val density = LocalDensity.current
                    val tabCount = NavigationTab.values().size
                    val tabWidth = maxWidth / tabCount
                    val tabWidthPx = with(density) { tabWidth.toPx() }
                    val maxWidthPx = with(density) { maxWidth.toPx() }

                    // Drag state for real interactive finger sliding across tabs
                    var dragOffsetPx by remember { mutableStateOf<Float?>(null) }
                    var isDragging by remember { mutableStateOf(false) }

                    val activeIndex = selectedTab.ordinal
                    val targetPx = dragOffsetPx ?: (activeIndex * tabWidthPx)

                    // Spring animation for smooth Apple liquid glide
                    val animatedPillPx by animateFloatAsState(
                        targetValue = targetPx,
                        animationSpec = if (isDragging) {
                            spring(dampingRatio = 1f, stiffness = 800f)
                        } else {
                            spring(dampingRatio = 0.78f, stiffness = 380f)
                        },
                        label = "pillOffset"
                    )

                    // Liquid stretch scale during active motion
                    val pillScaleX by animateFloatAsState(
                        targetValue = if (isDragging) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
                        label = "pillScaleX"
                    )

                    val pillOffsetDp = with(density) { animatedPillPx.toDp() }

                    // Liquid Glass Slider Capsule (Solid, 100% Opaque)
                    Box(
                        modifier = Modifier
                            .offset(x = pillOffsetDp)
                            .width(tabWidth)
                            .fillMaxHeight()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                            .scale(scaleX = pillScaleX, scaleY = 1.0f)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(26.dp),
                                clip = false
                            )
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF454552), // Top specular sheen (100% solid)
                                        Color(0xFF2C2C34), // Mid glass reflection
                                        Color(0xFF1E1E24)  // Base tone
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF6B6B7E), // Crisp glossy highlight rim
                                        Color(0xFF34343E)
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                    ) {
                        // Glossy specular highlight arc at the top of the pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.5.dp)
                                .padding(horizontal = 8.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF454552),
                                            Color(0xFF88889C),
                                            Color(0xFF454552)
                                        )
                                    )
                                )
                        )
                    }

                    // Foreground Tab Items Row with both tap & smooth horizontal drag gestures
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(tabWidthPx, maxWidthPx) {
                                detectHorizontalDragGestures(
                                    onDragStart = { offset ->
                                        isDragging = true
                                        dragOffsetPx = (offset.x - tabWidthPx / 2f).coerceIn(0f, maxWidthPx - tabWidthPx)
                                    },
                                    onDragEnd = {
                                        isDragging = false
                                        val finalPx = dragOffsetPx ?: (selectedTab.ordinal * tabWidthPx)
                                        val targetIdx = ((finalPx + tabWidthPx / 2f) / tabWidthPx).toInt().coerceIn(0, tabCount - 1)
                                        dragOffsetPx = null
                                        onTabSelected(NavigationTab.values()[targetIdx])
                                    },
                                    onDragCancel = {
                                        isDragging = false
                                        dragOffsetPx = null
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        val current = dragOffsetPx ?: (selectedTab.ordinal * tabWidthPx)
                                        val next = (current + dragAmount).coerceIn(0f, maxWidthPx - tabWidthPx)
                                        dragOffsetPx = next
                                        val liveIdx = ((next + tabWidthPx / 2f) / tabWidthPx).toInt().coerceIn(0, tabCount - 1)
                                        if (liveIdx != selectedTab.ordinal) {
                                            onTabSelected(NavigationTab.values()[liveIdx])
                                        }
                                    }
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavigationTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab

                            // Dynamic scale animation for active tab icon
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = 0.65f,
                                    stiffness = 400f
                                ),
                                label = "tabIconScale"
                            )

                            val iconColor = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF8E8E93)
                            val textColor = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF8E8E93)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        onTabSelected(tab)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = iconColor,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .scale(iconScale)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tab.title,
                                        color = textColor,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    // Apple active micro-pill dot
                                    Box(
                                        modifier = Modifier
                                            .width(if (isSelected) 12.dp else 0.dp)
                                            .height(2.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color(0xFF0A84FF) else Color(0xFF222226)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
