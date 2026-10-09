package com.example.shadowsensi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.model.DeviceGamingScore
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.SensitivityRecommendation
import com.example.shadowsensi.ui.components.CyberButton
import com.example.shadowsensi.ui.components.CyberCard
import com.example.shadowsensi.ui.components.NeonBadge
import com.example.shadowsensi.ui.components.SectionHeader
import com.example.shadowsensi.ui.components.StatusPulseDot
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    selectedGame: GameType,
    onSelectGame: (GameType) -> Unit,
    deviceInfo: DeviceHardwareInfo,
    gamingScore: DeviceGamingScore,
    currentRecommendation: SensitivityRecommendation,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp)
    ) {
        // Top Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusPulseDot(color = NeonCyan, size = 8.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHADOW SENSI AI",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "COMMAND MATRIX",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    NeonBadge(
                        text = "TIER ${gamingScore.competitiveTier.rank}",
                        color = gamingScore.competitiveTier.color
                    )
                }
            }
        }

        // Game Selector Carousel
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                SectionHeader(
                    title = "Target Game Engine",
                    subtitle = "Select game to calibrate algorithms",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    accentColor = selectedGame.accentColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(GameType.values()) { game ->
                        val isSelected = game == selectedGame
                        Box(
                            modifier = Modifier
                                .testTag("game_chip_${game.id}")
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) game.accentColor.copy(alpha = 0.18f) else CyberCardElevated
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) game.accentColor else CyberCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectGame(game) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(game.accentColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = game.shortName,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = game.badge,
                                        color = game.accentColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Device Performance Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DEVICE TELEMETRY",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${deviceInfo.manufacturer} ${deviceInfo.deviceModel}",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            NeonBadge(
                                text = "${deviceInfo.refreshRateHz}Hz DISPLAY",
                                color = if (deviceInfo.refreshRateHz >= 90) NeonGreen else NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Score bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Performance Rating",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${gamingScore.performanceScore}/100",
                                color = NeonCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { gamingScore.performanceScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonCyan,
                            trackColor = CyberCardElevated
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3 Quick Telemetry Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCardElevated)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "EST. FPS",
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${gamingScore.estimatedFps} FPS",
                                        color = NeonGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCardElevated)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "TOUCH LAG",
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "~${gamingScore.touchLatencyEstimateMs} ms",
                                        color = NeonCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCardElevated)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "THERMAL",
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val tempText = deviceInfo.batteryTempCelsius?.let { "${it.toInt()}°C" } ?: "32°C"
                                    Text(
                                        text = tempText,
                                        color = if ((deviceInfo.batteryTempCelsius ?: 32f) > 40f) NeonRed else NeonPurple,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active AI Sensi Highlight Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = selectedGame.accentColor.copy(alpha = 0.4f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = selectedGame.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI SENSI READY",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            NeonBadge(
                                text = currentRecommendation.confidence.label,
                                color = currentRecommendation.confidence.color
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Calibrated for ${selectedGame.displayName}. Tap below to fine-tune sliders, analyze claw positioning, or copy calibration.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        CyberButton(
                            text = "OPEN SENSITIVITY LAB",
                            onClick = { onNavigateToTab(2) }, // Tab 2 is Sensi
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Default.Tune,
                            accentColor = selectedGame.accentColor,
                            testTag = "open_sensi_lab_btn"
                        )
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "ESPORTS MODULES",
                    subtitle = "Tactical tools for competitive mastery",
                    accentColor = NeonPurple
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickModuleTile(
                        title = "Hardware Analyzer",
                        desc = "DPI, CPU, Network",
                        icon = Icons.Default.Memory,
                        color = NeonCyan,
                        onClick = { onNavigateToTab(1) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickModuleTile(
                        title = "HUD Claw Lab",
                        desc = "2-5 Finger Layouts",
                        icon = Icons.AutoMirrored.Filled.ViewQuilt,
                        color = NeonPurple,
                        onClick = { onNavigateToTab(3) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickModuleTile(
                        title = "Training Center",
                        desc = "Reflex & Drag Drills",
                        icon = Icons.Default.FitnessCenter,
                        color = NeonOrange,
                        onClick = { onNavigateToTab(4) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickModuleTile(
                        title = "Shadow AI Coach",
                        desc = "Gemini High Thinking",
                        icon = Icons.Default.Psychology,
                        color = NeonRed,
                        onClick = { onNavigateToTab(5) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Pro Tip Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberCardBorder,
                    backgroundColor = CyberCardElevated,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "💡",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DAILY PRO INSIGHT",
                                color = NeonOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Keep touch surface clean and dry. Finger sleeves decrease drag friction by ~40%, allowing lower General sensitivity without losing flick acceleration.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickModuleTile(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier.clickable { onClick() },
        borderColor = CyberCardBorder,
        contentPadding = PaddingValues(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
