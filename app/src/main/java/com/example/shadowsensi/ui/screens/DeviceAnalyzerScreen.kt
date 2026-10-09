package com.example.shadowsensi.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.model.DeviceGamingScore
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.DpiAnalysis
import com.example.shadowsensi.model.NetworkQualityInfo
import com.example.shadowsensi.model.NetworkStatus
import com.example.shadowsensi.services.HardwareDetector
import com.example.shadowsensi.services.NetworkService
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
import kotlinx.coroutines.launch

@Composable
fun DeviceAnalyzerScreen(
    deviceInfo: DeviceHardwareInfo,
    gamingScore: DeviceGamingScore,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dpiAnalysis = remember(deviceInfo) {
        HardwareDetector.calculateDpiRecommendations(deviceInfo)
    }

    var networkInfo by remember {
        mutableStateOf(
            NetworkQualityInfo(
                pingMs = 42,
                jitterMs = 6,
                status = NetworkStatus.GOOD,
                networkType = NetworkService.getNetworkType(context)
            )
        )
    }
    var isTestingNetwork by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("device_analyzer_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Screen Title
        item {
            Column {
                Text(
                    text = "HARDWARE TELEMETRY",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "DEVICE DIAGNOSTICS",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Hardware Verified Specs Grid
        item {
            SectionHeader(
                title = "Hardware Profile",
                subtitle = "Real readings from Android kernel",
                badge = "VERIFIED",
                accentColor = NeonCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "Device Model",
                        value = deviceInfo.deviceModel,
                        modifier = Modifier.weight(1f),
                        accentColor = TextPrimary
                    )
                    StatBox(
                        label = "RAM Capacity",
                        value = "${deviceInfo.totalRamGb}",
                        unit = "GB Total",
                        modifier = Modifier.weight(1f),
                        accentColor = NeonCyan
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "Refresh Rate",
                        value = "${deviceInfo.refreshRateHz}",
                        unit = "Hz",
                        modifier = Modifier.weight(1f),
                        accentColor = if (deviceInfo.refreshRateHz >= 90) NeonGreen else NeonCyan
                    )
                    StatBox(
                        label = "Display Res",
                        value = deviceInfo.screenResolution,
                        modifier = Modifier.weight(1f),
                        accentColor = TextSecondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "CPU Cores",
                        value = "${deviceInfo.cpuCores}",
                        unit = "Cores",
                        modifier = Modifier.weight(1f),
                        accentColor = NeonPurple
                    )
                    StatBox(
                        label = "Gyroscope",
                        value = if (deviceInfo.hasGyroscope) "AVAILABLE" else "ABSENT",
                        modifier = Modifier.weight(1f),
                        accentColor = if (deviceInfo.hasGyroscope) NeonGreen else NeonOrange
                    )
                }
            }
        }

        // Competitive Ratings (Clear separation of real vs estimated)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Competitive Readiness",
                subtitle = "Algorithmic estimates for battle royale fps",
                badge = "ESTIMATED",
                accentColor = NeonPurple
            )
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonPurple.copy(alpha = 0.5f),
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
                                text = "ESPORTS CAPABILITY TIER",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = gamingScore.competitiveTier.title,
                                color = gamingScore.competitiveTier.color,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        NeonBadge(
                            text = "TIER ${gamingScore.competitiveTier.rank}",
                            color = gamingScore.competitiveTier.color
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScoreTile(
                            title = "EST. FPS",
                            value = "${gamingScore.estimatedFps}",
                            color = NeonGreen,
                            modifier = Modifier.weight(1f)
                        )
                        ScoreTile(
                            title = "SMOOTHNESS",
                            value = "${gamingScore.smoothnessScore}%",
                            color = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        ScoreTile(
                            title = "STABILITY",
                            value = "${gamingScore.stabilityScore}%",
                            color = NeonPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Network Quality Monitor
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Esports Network Monitor",
                subtitle = "Live socket latency & jitter test",
                accentColor = NeonCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.CellTower,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = networkInfo.networkType,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        NeonBadge(
                            text = networkInfo.status.label,
                            color = networkInfo.status.color
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCardElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "LIVE PING",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${networkInfo.pingMs} ms",
                                    color = networkInfo.status.color,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCardElevated)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "JITTER",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "±${networkInfo.jitterMs} ms",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CyberButton(
                        text = if (isTestingNetwork) "MEASURING..." else "TEST LIVE PING",
                        onClick = {
                            if (!isTestingNetwork) {
                                isTestingNetwork = true
                                scope.launch {
                                    val result = NetworkService.runPingTest(context)
                                    networkInfo = result
                                    isTestingNetwork = false
                                }
                            }
                        },
                        enabled = !isTestingNetwork,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = Icons.Default.Refresh,
                        isPrimary = false,
                        accentColor = NeonCyan,
                        testTag = "test_ping_btn"
                    )
                }
            }
        }

        // Smart DPI Analyzer
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Smart DPI Analyzer",
                subtitle = "Screen density scaling for faster swipe velocity",
                accentColor = NeonOrange
            )
            Spacer(modifier = Modifier.height(10.dp))

            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Current Display DPI",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${dpiAnalysis.currentDpi} DPI",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DpiCard(
                            label = "SAFE DPI",
                            dpi = dpiAnalysis.safeDpi,
                            color = NeonGreen,
                            modifier = Modifier.weight(1f)
                        )
                        DpiCard(
                            label = "SMOOTH DPI",
                            dpi = dpiAnalysis.smoothDpi,
                            color = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        DpiCard(
                            label = "COMPETITIVE",
                            dpi = dpiAnalysis.competitiveDpi,
                            color = NeonPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonOrange.copy(alpha = 0.12f))
                            .border(1.dp, NeonOrange.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = NeonOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dpiAnalysis.stabilityWarning,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Touch Response Guide: ${dpiAnalysis.touchSpeedRecommendation}. ${dpiAnalysis.aimAccelerationGuide}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun ScoreTile(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCardElevated)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun DpiCard(
    label: String,
    dpi: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberCardElevated)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$dpi",
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
