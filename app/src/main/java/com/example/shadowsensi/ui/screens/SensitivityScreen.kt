package com.example.shadowsensi.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.model.AimStyle
import com.example.shadowsensi.model.BgmiSensitivity
import com.example.shadowsensi.model.ClawGrip
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.FreeFireSensitivity
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.GyroMode
import com.example.shadowsensi.model.PlayStyle
import com.example.shadowsensi.model.PlayerProfile
import com.example.shadowsensi.model.PubgScopeSens
import com.example.shadowsensi.model.SensPref
import com.example.shadowsensi.model.SensitivityRecommendation
import com.example.shadowsensi.services.SensitivityEngine
import com.example.shadowsensi.ui.components.CyberButton
import com.example.shadowsensi.ui.components.CyberCard
import com.example.shadowsensi.ui.components.CyberSlider
import com.example.shadowsensi.ui.components.NeonBadge
import com.example.shadowsensi.ui.components.SectionHeader
import com.example.ui.theme.CyberBackground
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
fun SensitivityScreen(
    selectedGame: GameType,
    deviceInfo: DeviceHardwareInfo,
    currentRecommendation: SensitivityRecommendation,
    onSaveRecommendation: (SensitivityRecommendation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Player profile state
    var playerProfile by remember { mutableStateOf(PlayerProfile()) }
    var isQuestionnaireExpanded by remember { mutableStateOf(false) }

    // Active sensitivities for editing
    var ffSens by remember(currentRecommendation, selectedGame) {
        mutableStateOf(currentRecommendation.ffSens)
    }
    var bgmiSens by remember(currentRecommendation, selectedGame) {
        mutableStateOf(currentRecommendation.bgmiSens)
    }

    // BGMI active scope category tab: 0=Camera, 1=ADS, 2=Gyro
    var bgmiTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sensitivity_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI SENSITIVITY ENGINE",
                            color = selectedGame.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = selectedGame.displayName,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    NeonBadge(
                        text = selectedGame.badge,
                        color = selectedGame.accentColor
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Expandable Player Profile Questionnaire
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberCardBorder,
                contentPadding = PaddingValues(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isQuestionnaireExpanded = !isQuestionnaireExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "PLAYER COMBAT PREFERENCES",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${playerProfile.playStyle.title} • ${playerProfile.clawGrip.title}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = if (isQuestionnaireExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }

                    AnimatedVisibility(visible = isQuestionnaireExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            // Playstyle Selector
                            Text(
                                text = "PRIMARY PLAYSTYLE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(PlayStyle.values()) { style ->
                                    val sel = playerProfile.playStyle == style
                                    SelectChip(
                                        text = style.title,
                                        selected = sel,
                                        onClick = { playerProfile = playerProfile.copy(playStyle = style) },
                                        color = NeonCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Aim Style
                            Text(
                                text = "AIMING & DRAG TECHNIQUE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(AimStyle.values()) { aim ->
                                    val sel = playerProfile.aimStyle == aim
                                    SelectChip(
                                        text = aim.title,
                                        selected = sel,
                                        onClick = { playerProfile = playerProfile.copy(aimStyle = aim) },
                                        color = NeonPurple
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Claw Grip
                            Text(
                                text = "CONTROL LAYOUT (FINGERS)",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(ClawGrip.values()) { claw ->
                                    val sel = playerProfile.clawGrip == claw
                                    SelectChip(
                                        text = claw.title,
                                        selected = sel,
                                        onClick = { playerProfile = playerProfile.copy(clawGrip = claw) },
                                        color = NeonGreen
                                    )
                                }
                            }

                            if (selectedGame.isPubgFamily()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "GYROSCOPE MODE",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(GyroMode.values()) { gyro ->
                                        val sel = playerProfile.gyroMode == gyro
                                        SelectChip(
                                            text = gyro.title,
                                            selected = sel,
                                            onClick = { playerProfile = playerProfile.copy(gyroMode = gyro) },
                                            color = NeonCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Generate Button
        item {
            Spacer(modifier = Modifier.height(12.dp))
            CyberButton(
                text = "CALIBRATE & GENERATE AI SENSI",
                onClick = {
                    val newRec = SensitivityEngine.generateRecommendation(
                        gameType = selectedGame,
                        deviceInfo = deviceInfo,
                        playerProfile = playerProfile
                    )
                    ffSens = newRec.ffSens
                    bgmiSens = newRec.bgmiSens
                    onSaveRecommendation(newRec)
                    Toast.makeText(context, "AI Calibration Updated!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.AutoAwesome,
                accentColor = selectedGame.accentColor,
                testTag = "generate_sensi_btn"
            )
        }

        // AI Reasoning & Diagnostics Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = selectedGame.accentColor.copy(alpha = 0.4f),
                contentPadding = PaddingValues(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI REASONING LOG",
                            color = selectedGame.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        NeonBadge(
                            text = currentRecommendation.confidence.label,
                            color = currentRecommendation.confidence.color
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    currentRecommendation.reasoning.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = "▸",
                                color = selectedGame.accentColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = reason,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Touch Response: ${currentRecommendation.touchResponseAdvice}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Sliders Section
        item {
            Spacer(modifier = Modifier.height(18.dp))
            SectionHeader(
                title = "Sensitivity Sliders",
                subtitle = "Fine-tune and calibrate muscle memory values",
                accentColor = selectedGame.accentColor
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedGame.isFreeFireFamily()) {
                // Free Fire Sliders
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Column {
                        CyberSlider(
                            name = "General (Drag Speed)",
                            value = ffSens.general,
                            range = 50f..200f,
                            onValueChange = { ffSens = ffSens.copy(general = it) },
                            accentColor = NeonCyan,
                            testTag = "ff_slider_general"
                        )
                        CyberSlider(
                            name = "Red Dot",
                            value = ffSens.redDot,
                            range = 50f..200f,
                            onValueChange = { ffSens = ffSens.copy(redDot = it) },
                            accentColor = NeonRed,
                            testTag = "ff_slider_red_dot"
                        )
                        CyberSlider(
                            name = "2X Scope",
                            value = ffSens.scope2x,
                            range = 40f..200f,
                            onValueChange = { ffSens = ffSens.copy(scope2x = it) },
                            accentColor = NeonPurple,
                            testTag = "ff_slider_2x"
                        )
                        CyberSlider(
                            name = "4X Scope",
                            value = ffSens.scope4x,
                            range = 30f..200f,
                            onValueChange = { ffSens = ffSens.copy(scope4x = it) },
                            accentColor = NeonPurple,
                            testTag = "ff_slider_4x"
                        )
                        CyberSlider(
                            name = "Sniper Scope",
                            value = ffSens.sniperScope,
                            range = 20f..180f,
                            onValueChange = { ffSens = ffSens.copy(sniperScope = it) },
                            accentColor = NeonOrange,
                            testTag = "ff_slider_sniper"
                        )
                        CyberSlider(
                            name = "Free Look",
                            value = ffSens.freeLook,
                            range = 20f..180f,
                            onValueChange = { ffSens = ffSens.copy(freeLook = it) },
                            accentColor = TextSecondary,
                            testTag = "ff_slider_free_look"
                        )
                    }
                }
            } else {
                // BGMI / PUBG Mobile Sliders with 3 tabs
                Column {
                    TabRow(
                        selectedTabIndex = bgmiTab,
                        containerColor = CyberCardElevated,
                        contentColor = NeonCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[bgmiTab]),
                                color = NeonCyan
                            )
                        }
                    ) {
                        Tab(
                            selected = bgmiTab == 0,
                            onClick = { bgmiTab = 0 },
                            text = { Text("CAMERA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = bgmiTab == 1,
                            onClick = { bgmiTab = 1 },
                            text = { Text("ADS RECOIL", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = bgmiTab == 2,
                            onClick = { bgmiTab = 2 },
                            text = { Text("GYROSCOPE", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        when (bgmiTab) {
                            0 -> PubgSensGroup(
                                sens = bgmiSens.camera,
                                max = 200,
                                onUpdate = { bgmiSens = bgmiSens.copy(camera = it) }
                            )
                            1 -> PubgSensGroup(
                                sens = bgmiSens.ads,
                                max = 200,
                                onUpdate = { bgmiSens = bgmiSens.copy(ads = it) }
                            )
                            2 -> PubgSensGroup(
                                sens = bgmiSens.gyro,
                                max = 400,
                                onUpdate = { bgmiSens = bgmiSens.copy(gyro = it) }
                            )
                        }
                    }
                }
            }
        }

        // Actions: Copy, Save, Reset
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberButton(
                    text = "COPY SETTINGS",
                    onClick = {
                        val textToCopy = formatSettingsForClipboard(selectedGame, ffSens, bgmiSens)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("ShadowSensi", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Settings copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.ContentCopy,
                    accentColor = NeonCyan,
                    testTag = "copy_sensi_btn"
                )

                CyberButton(
                    text = "RESET BASE",
                    onClick = {
                        ffSens = currentRecommendation.ffSens
                        bgmiSens = currentRecommendation.bgmiSens
                        Toast.makeText(context, "Reset to AI recommended baseline.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Default.RestartAlt,
                    isPrimary = false,
                    accentColor = TextSecondary,
                    testTag = "reset_sensi_btn"
                )
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun PubgSensGroup(
    sens: PubgScopeSens,
    max: Int,
    onUpdate: (PubgScopeSens) -> Unit
) {
    Column {
        CyberSlider(
            name = "No Scope (TPP / FPP)",
            value = sens.noScope,
            range = 20f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(noScope = it)) },
            accentColor = NeonCyan
        )
        CyberSlider(
            name = "Red Dot / Holographic",
            value = sens.redDotHolo,
            range = 10f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(redDotHolo = it)) },
            accentColor = NeonRed
        )
        CyberSlider(
            name = "2X Scope",
            value = sens.scope2x,
            range = 10f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(scope2x = it)) },
            accentColor = NeonPurple
        )
        CyberSlider(
            name = "3X Scope",
            value = sens.scope3x,
            range = 5f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(scope3x = it)) },
            accentColor = NeonOrange
        )
        CyberSlider(
            name = "4X Scope",
            value = sens.scope4x,
            range = 5f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(scope4x = it)) },
            accentColor = NeonGreen
        )
        CyberSlider(
            name = "6X Scope",
            value = sens.scope6x,
            range = 5f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(scope6x = it)) },
            accentColor = NeonPurple
        )
        CyberSlider(
            name = "8X Scope",
            value = sens.scope8x,
            range = 2f..max.toFloat(),
            onValueChange = { onUpdate(sens.copy(scope8x = it)) },
            accentColor = TextSecondary
        )
    }
}

@Composable
private fun SelectChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) color.copy(alpha = 0.2f) else CyberCardElevated)
            .border(1.dp, if (selected) color else CyberCardBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private fun formatSettingsForClipboard(
    game: GameType,
    ff: FreeFireSensitivity,
    bgmi: BgmiSensitivity
): String {
    return if (game.isFreeFireFamily()) {
        """=== SHADOW SENSI AI : ${game.displayName} ===
General: ${ff.general}
Red Dot: ${ff.redDot}
2X Scope: ${ff.scope2x}
4X Scope: ${ff.scope4x}
Sniper Scope: ${ff.sniperScope}
Free Look: ${ff.freeLook}
Recommended Fire Button: 45%
Calibrated with Shadow Sensi AI Esports Assistant"""
    } else {
        """=== SHADOW SENSI AI : ${game.displayName} ===
[CAMERA]
No Scope: ${bgmi.camera.noScope} | Red Dot: ${bgmi.camera.redDotHolo} | 2X: ${bgmi.camera.scope2x}
3X: ${bgmi.camera.scope3x} | 4X: ${bgmi.camera.scope4x} | 6X: ${bgmi.camera.scope6x} | 8X: ${bgmi.camera.scope8x}

[ADS RECOIL]
No Scope: ${bgmi.ads.noScope} | Red Dot: ${bgmi.ads.redDotHolo} | 2X: ${bgmi.ads.scope2x}
3X: ${bgmi.ads.scope3x} | 4X: ${bgmi.ads.scope4x} | 6X: ${bgmi.ads.scope6x} | 8X: ${bgmi.ads.scope8x}

[GYROSCOPE]
No Scope: ${bgmi.gyro.noScope}% | Red Dot: ${bgmi.gyro.redDotHolo}% | 2X: ${bgmi.gyro.scope2x}%
3X: ${bgmi.gyro.scope3x}% | 4X: ${bgmi.gyro.scope4x}% | 6X: ${bgmi.gyro.scope6x}% | 8X: ${bgmi.gyro.scope8x}%
Calibrated with Shadow Sensi AI Esports Assistant"""
    }
}
