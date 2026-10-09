package com.example.shadowsensi.ui.screens

import android.os.SystemClock
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.ui.components.CyberButton
import com.example.shadowsensi.ui.components.CyberCard
import com.example.shadowsensi.ui.components.NeonBadge
import com.example.shadowsensi.ui.components.SectionHeader
import com.example.shadowsensi.ui.components.StatBox
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun TrainingScreen(
    selectedGame: GameType,
    modifier: Modifier = Modifier
) {
    var trainingTab by remember { mutableIntStateOf(0) } // 0=Target Reflex, 1=Drag Headshot Swipe

    // Reflex Game State
    var isReflexActive by remember { mutableStateOf(false) }
    var targetX by remember { mutableFloatStateOf(0.5f) }
    var targetY by remember { mutableFloatStateOf(0.5f) }
    var spawnTime by remember { mutableLongStateOf(0L) }
    var lastReactionMs by remember { mutableIntStateOf(0) }
    var bestReactionMs by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    val reactionHistory = remember { mutableStateListOf<Int>() }

    // Drag Swipe Calibration State
    var dragResult by remember { mutableStateOf("Swipe upward inside the zone to test drag velocity & headshot angle.") }
    var dragVelocityLabel by remember { mutableStateOf("0.0 px/ms") }
    var dragAngleLabel by remember { mutableStateOf("0°") }
    var dragStatusColor by remember { mutableStateOf(NeonCyan) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("training_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "TRAINING CENTER & SIMULATOR",
                    color = NeonOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "MUSCLE MEMORY LAB",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Mode Tab Selector
        item {
            TabRow(
                selectedTabIndex = trainingTab,
                containerColor = CyberCardElevated,
                contentColor = NeonOrange,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[trainingTab]),
                        color = NeonOrange
                    )
                }
            ) {
                Tab(
                    selected = trainingTab == 0,
                    onClick = { trainingTab = 0 },
                    text = { Text("TARGET REFLEX", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = trainingTab == 1,
                    onClick = { trainingTab = 1 },
                    text = { Text("DRAG SWIPE SIMULATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (trainingTab == 0) {
            // TARGET REFLEX TEST
            item {
                SectionHeader(
                    title = "Target Click Reaction Drill",
                    subtitle = "Tap target as quickly as it spawns. Esports pro benchmark: <220ms",
                    accentColor = NeonCyan
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "LAST PING",
                        value = if (lastReactionMs > 0) "$lastReactionMs" else "--",
                        unit = "ms",
                        modifier = Modifier.weight(1f),
                        accentColor = if (lastReactionMs in 1..220) NeonGreen else if (lastReactionMs > 320) NeonRed else NeonCyan
                    )
                    StatBox(
                        label = "BEST TIME",
                        value = if (bestReactionMs > 0) "$bestReactionMs" else "--",
                        unit = "ms",
                        modifier = Modifier.weight(1f),
                        accentColor = NeonGreen
                    )
                    StatBox(
                        label = "STREAK",
                        value = "$streak",
                        unit = "hits",
                        modifier = Modifier.weight(1f),
                        accentColor = NeonOrange
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Reflex Canvas
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isReflexActive) NeonCyan.copy(alpha = 0.8f) else CyberCardBorder,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                            .background(Color(0xFF06060A))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val canvasW = constraints.maxWidth.toFloat()
                        val canvasH = constraints.maxHeight.toFloat()

                        if (!isReflexActive) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DRILL PAUSED",
                                    color = TextMuted,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CyberButton(
                                    text = "START REFLEX TEST",
                                    onClick = {
                                        isReflexActive = true
                                        spawnTime = SystemClock.elapsedRealtime()
                                        targetX = Random.nextFloat().coerceIn(0.15f, 0.85f)
                                        targetY = Random.nextFloat().coerceIn(0.15f, 0.85f)
                                    },
                                    leadingIcon = Icons.Default.PlayArrow,
                                    accentColor = NeonCyan,
                                    testTag = "start_reflex_btn"
                                )
                            }
                        } else {
                            // Target circle
                            val posX = (targetX * canvasW) - 28.dp.value
                            val posY = (targetY * canvasH) - 28.dp.value

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(NeonRed)
                                    .border(3.dp, Color.White, CircleShape)
                                    .clickable {
                                        val now = SystemClock.elapsedRealtime()
                                        val reaction = (now - spawnTime).toInt()
                                        lastReactionMs = reaction
                                        if (bestReactionMs == 0 || reaction < bestReactionMs) {
                                            bestReactionMs = reaction
                                        }
                                        hits++
                                        streak++
                                        reactionHistory.add(reaction)

                                        // Respawn at new random spot
                                        spawnTime = SystemClock.elapsedRealtime()
                                        targetX = Random.nextFloat().coerceIn(0.15f, 0.85f)
                                        targetY = Random.nextFloat().coerceIn(0.15f, 0.85f)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }

                if (isReflexActive) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CyberButton(
                        text = "STOP DRILL",
                        onClick = { isReflexActive = false },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = false,
                        accentColor = TextSecondary,
                        testTag = "stop_reflex_btn"
                    )
                }
            }
        } else {
            // DRAG HEADSHOT SWIPE SIMULATOR
            item {
                SectionHeader(
                    title = "Drag Swipe Simulator",
                    subtitle = "Calibrate vertical swipe acceleration for Free Fire / FPS drag shots",
                    accentColor = NeonRed
                )
                Spacer(modifier = Modifier.height(10.dp))

                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = dragStatusColor.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "SWIPE TELEMETRY",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            NeonBadge(
                                text = dragVelocityLabel,
                                color = dragStatusColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dragResult,
                            color = dragStatusColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Swipe Canvas Area
                        var startX by remember { mutableFloatStateOf(0f) }
                        var startY by remember { mutableFloatStateOf(0f) }
                        var startTimeMs by remember { mutableLongStateOf(0L) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF040408))
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            startX = offset.x
                                            startY = offset.y
                                            startTimeMs = SystemClock.elapsedRealtime()
                                        },
                                        onDragEnd = {
                                            // Evaluated on drag release
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val deltaY = startY - change.position.y // Positive when dragging UP
                                        val deltaX = change.position.x - startX
                                        val timeTaken = (SystemClock.elapsedRealtime() - startTimeMs).coerceAtLeast(1)

                                        if (deltaY > 60f) {
                                            val velocity = deltaY / timeTaken.toFloat()
                                            dragVelocityLabel = String.format(java.util.Locale.US, "%.2f px/ms", velocity)

                                            val deviation = abs(deltaX)
                                            dragAngleLabel = "${deviation.toInt()} px drift"

                                            when {
                                                velocity in 1.4f..2.4f && deviation < 45f -> {
                                                    dragResult = "🎯 PERFECT HEADSHOT DRAG! Ideal vertical velocity with minimal lateral tilt."
                                                    dragStatusColor = NeonGreen
                                                }
                                                velocity > 2.4f -> {
                                                    dragResult = "⚠️ OVER-DRAGGED: Swipe velocity too fierce. Crosshair will shoot above enemy helmet."
                                                    dragStatusColor = NeonOrange
                                                }
                                                deviation >= 45f -> {
                                                    dragResult = "⚠️ SLANTED SWIPE: Diagonal drift detected (${deviation.toInt()}px). Drag straight up towards the 12 o'clock angle."
                                                    dragStatusColor = NeonRed
                                                }
                                                else -> {
                                                    dragResult = "⚠️ UNDER-DRAGGED: Velocity too sluggish. Aim will stick to enemy chest level."
                                                    dragStatusColor = NeonYellow
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = NeonCyan.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "SWIPE UPWARD HERE (DRAG HEADSHOT)",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Tests upward acceleration and flick angle",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Routine Checklist
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Esports Training Routine",
                subtitle = "15-minute daily warmup recommended by competitive coaches",
                accentColor = NeonPurple
            )
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DrillCheckItem("5 min: Target Reflex Practice (Aim for sub-220ms reaction)")
                    DrillCheckItem("5 min: Drag Swipe Straightness (Zero horizontal deviation)")
                    DrillCheckItem("5 min: In-game training ground 180-degree turn calibration")
                }
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun DrillCheckItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonGreen)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
