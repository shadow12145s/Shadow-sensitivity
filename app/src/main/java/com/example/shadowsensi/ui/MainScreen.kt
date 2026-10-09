package com.example.shadowsensi.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.PlayerProfile
import com.example.shadowsensi.services.HardwareDetector
import com.example.shadowsensi.services.SensitivityEngine
import com.example.shadowsensi.ui.components.NeonBadge
import com.example.shadowsensi.ui.components.StatusPulseDot
import com.example.shadowsensi.ui.screens.DashboardScreen
import com.example.shadowsensi.ui.screens.DeviceAnalyzerScreen
import com.example.shadowsensi.ui.screens.GeminiChatScreen
import com.example.shadowsensi.ui.screens.HudAndPresetsScreen
import com.example.shadowsensi.ui.screens.SensitivityScreen
import com.example.shadowsensi.ui.screens.SettingsScreen
import com.example.shadowsensi.ui.screens.TrainingScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Hardware Telemetry initialized once from system
    val deviceInfo = remember { HardwareDetector.detectHardware(context) }
    val gamingScore = remember(deviceInfo) { HardwareDetector.calculateGamingScore(deviceInfo) }

    // Active Game Selection
    var selectedGame by remember { mutableStateOf(GameType.FREE_FIRE) }

    // Current Recommendation
    var currentRecommendation by remember(selectedGame) {
        mutableStateOf(
            SensitivityEngine.generateRecommendation(
                gameType = selectedGame,
                deviceInfo = deviceInfo,
                playerProfile = PlayerProfile()
            )
        )
    }

    // Navigation Tab (0: Home, 1: Analyzer, 2: Sensi, 3: HUD, 4: Training, 5: Coach AI, 6: Settings)
    var currentTab by remember { mutableIntStateOf(0) }

    // Handle Android system back button
    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    val navItems = listOf(
        NavTabItem("Home", Icons.Default.Speed, "nav_home"),
        NavTabItem("Specs", Icons.Default.Memory, "nav_specs"),
        NavTabItem("Sensi", Icons.Default.Tune, "nav_sensi"),
        NavTabItem("HUD", Icons.AutoMirrored.Filled.ViewQuilt, "nav_hud"),
        NavTabItem("Drills", Icons.Default.FitnessCenter, "nav_drills"),
        NavTabItem("AI Coach", Icons.Default.Psychology, "nav_ai_coach")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberBackground,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface)
                    .border(1.dp, CyberCardBorder)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { currentTab = 0 }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedGame.accentColor.copy(alpha = 0.2f))
                                .border(1.dp, selectedGame.accentColor, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusPulseDot(color = NeonCyan, size = 6.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SHADOW SENSI",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "ESPORTS MATRIX",
                                color = selectedGame.accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Game quick badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(selectedGame.accentColor.copy(alpha = 0.15f))
                                .border(1.dp, selectedGame.accentColor, RoundedCornerShape(8.dp))
                                .clickable {
                                    // Cycle through games
                                    val all = GameType.values()
                                    val nextIndex = (all.indexOf(selectedGame) + 1) % all.size
                                    selectedGame = all[nextIndex]
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = selectedGame.badge,
                                color = selectedGame.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { currentTab = 6 },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (currentTab == 6) NeonCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .border(1.dp, CyberCardBorder),
                containerColor = CyberSurface
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag(item.testTag),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonCyan
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    0 -> DashboardScreen(
                        selectedGame = selectedGame,
                        onSelectGame = { selectedGame = it },
                        deviceInfo = deviceInfo,
                        gamingScore = gamingScore,
                        currentRecommendation = currentRecommendation,
                        onNavigateToTab = { currentTab = it }
                    )
                    1 -> DeviceAnalyzerScreen(
                        deviceInfo = deviceInfo,
                        gamingScore = gamingScore
                    )
                    2 -> SensitivityScreen(
                        selectedGame = selectedGame,
                        deviceInfo = deviceInfo,
                        currentRecommendation = currentRecommendation,
                        onSaveRecommendation = { currentRecommendation = it }
                    )
                    3 -> HudAndPresetsScreen(
                        selectedGame = selectedGame,
                        onApplyPreset = { preset ->
                            if (preset.ffSens != null) {
                                currentRecommendation = currentRecommendation.copy(ffSens = preset.ffSens)
                            }
                            if (preset.bgmiSens != null) {
                                currentRecommendation = currentRecommendation.copy(bgmiSens = preset.bgmiSens)
                            }
                            currentTab = 2 // Jump to sensitivity screen to see values applied
                        }
                    )
                    4 -> TrainingScreen(
                        selectedGame = selectedGame
                    )
                    5 -> GeminiChatScreen(
                        selectedGame = selectedGame
                    )
                    6 -> SettingsScreen(
                        onResetAllData = {
                            selectedGame = GameType.FREE_FIRE
                            currentRecommendation = SensitivityEngine.generateRecommendation(
                                gameType = GameType.FREE_FIRE,
                                deviceInfo = deviceInfo,
                                playerProfile = PlayerProfile()
                            )
                        }
                    )
                }
            }
        }
    }
}
