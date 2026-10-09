package com.example.shadowsensi.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewQuilt
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shadowsensi.data.PresetData
import com.example.shadowsensi.model.ClawGrip
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.HudButton
import com.example.shadowsensi.model.ProPreset
import com.example.shadowsensi.model.SensitivityRecommendation
import com.example.shadowsensi.ui.components.CyberButton
import com.example.shadowsensi.ui.components.CyberCard
import com.example.shadowsensi.ui.components.CyberSlider
import com.example.shadowsensi.ui.components.NeonBadge
import com.example.shadowsensi.ui.components.SectionHeader
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
import kotlin.math.roundToInt

@Composable
fun HudAndPresetsScreen(
    selectedGame: GameType,
    onApplyPreset: (ProPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0=HUD Lab, 1=Pro Presets

    // HUD state
    var selectedClaw by remember { mutableStateOf(ClawGrip.FOUR_FINGER) }
    var currentHudLayout by remember(selectedClaw, selectedGame) {
        mutableStateOf(PresetData.getHudLayoutsForClaw(selectedClaw, selectedGame))
    }
    var selectedButtonId by remember { mutableStateOf<String?>("fire_left") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hud_presets_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "CLAW HUD & PRESETS MATRIX",
                    color = NeonPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "TACTICAL INTERFACE",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Top Switcher Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberCardElevated,
                contentColor = NeonPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonPurple
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("INTERACTIVE HUD CLAW", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("PRO PRESETS", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (selectedTab == 0) {
            // HUD BUILDER TAB
            item {
                SectionHeader(
                    title = "Finger Claw Selection",
                    subtitle = "Select grip architecture to load recommended positions",
                    accentColor = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(ClawGrip.TWO_FINGER, ClawGrip.THREE_FINGER, ClawGrip.FOUR_FINGER, ClawGrip.FIVE_FINGER)) { claw ->
                        val isSel = claw == selectedClaw
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else CyberCardElevated)
                                .border(1.dp, if (isSel) NeonCyan else CyberCardBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedClaw = claw
                                    currentHudLayout = PresetData.getHudLayoutsForClaw(claw, selectedGame)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = claw.title,
                                color = if (isSel) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Interactive Simulated Landscape Display Canvas
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SCREEN SIMULATION (DRAG & INSPECT)",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            NeonBadge(
                                text = "${selectedClaw.fingers}-FINGER",
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 16:9 Canvas Box
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9.5f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF040406))
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                        ) {
                            val canvasWidthPx = constraints.maxWidth.toFloat()
                            val canvasHeightPx = constraints.maxHeight.toFloat()
                            val density = LocalDensity.current

                            // Subtle center crosshair guide
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp)
                                    .border(1.dp, Color(0x33FFFFFF), CircleShape)
                            )

                            // Render each button
                            currentHudLayout.buttons.forEach { button ->
                                val isSelected = button.id == selectedButtonId
                                val buttonColor = Color(button.colorHex)

                                val xPx = button.xPercent * canvasWidthPx
                                val yPx = button.yPercent * canvasHeightPx
                                val sizeDp = button.sizeDp.dp

                                Box(
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(
                                                (xPx - (button.sizeDp * density.density / 2)).roundToInt(),
                                                (yPx - (button.sizeDp * density.density / 2)).roundToInt()
                                            )
                                        }
                                        .size(sizeDp)
                                        .clip(CircleShape)
                                        .background(buttonColor.copy(alpha = button.opacity))
                                        .border(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) Color.White else buttonColor,
                                            CircleShape
                                        )
                                        .clickable { selectedButtonId = button.id }
                                        .pointerInput(button.id) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                selectedButtonId = button.id
                                                val newXPct = ((xPx + dragAmount.x) / canvasWidthPx).coerceIn(0.08f, 0.92f)
                                                val newYPct = ((yPx + dragAmount.y) / canvasHeightPx).coerceIn(0.08f, 0.92f)

                                                currentHudLayout = currentHudLayout.copy(
                                                    buttons = currentHudLayout.buttons.map {
                                                        if (it.id == button.id) it.copy(xPercent = newXPct, yPercent = newYPct)
                                                        else it
                                                    }
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = button.name.take(4).uppercase(),
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Button Inspector
            item {
                val selectedButton = currentHudLayout.buttons.find { it.id == selectedButtonId }
                if (selectedButton != null) {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = Color(selectedButton.colorHex).copy(alpha = 0.6f),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SELECTED: ${selectedButton.name.uppercase()}",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Assigned to: ${selectedButton.fingerAssigned}",
                                        color = Color(selectedButton.colorHex),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                NeonBadge(
                                    text = "${selectedButton.sizeDp.toInt()} DP",
                                    color = Color(selectedButton.colorHex)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            CyberSlider(
                                name = "Button Scale / Size",
                                value = selectedButton.sizeDp.toInt(),
                                range = 32f..85f,
                                onValueChange = { newSize ->
                                    currentHudLayout = currentHudLayout.copy(
                                        buttons = currentHudLayout.buttons.map {
                                            if (it.id == selectedButton.id) it.copy(sizeDp = newSize.toFloat())
                                            else it
                                        }
                                    )
                                },
                                accentColor = Color(selectedButton.colorHex),
                                unit = "dp"
                            )

                            CyberSlider(
                                name = "Transparency / Opacity",
                                value = (selectedButton.opacity * 100).toInt(),
                                range = 40f..100f,
                                onValueChange = { newOp ->
                                    currentHudLayout = currentHudLayout.copy(
                                        buttons = currentHudLayout.buttons.map {
                                            if (it.id == selectedButton.id) it.copy(opacity = newOp / 100f)
                                            else it
                                        }
                                    )
                                },
                                accentColor = Color(selectedButton.colorHex),
                                unit = "%"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                CyberButton(
                    text = "RESET CLAW POSITIONS",
                    onClick = {
                        currentHudLayout = PresetData.getHudLayoutsForClaw(selectedClaw, selectedGame)
                        Toast.makeText(context, "HUD layout reset to recommended claw coordinates.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = Icons.Default.RestartAlt,
                    isPrimary = false,
                    accentColor = TextSecondary,
                    testTag = "reset_hud_btn"
                )
            }
        } else {
            // PRO PRESETS TAB
            item {
                SectionHeader(
                    title = "Esports Preset Vault",
                    subtitle = "Tournament tested configurations for ${selectedGame.displayName}",
                    accentColor = NeonPurple
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            val presetsForGame = PresetData.PRO_PRESETS.filter {
                if (selectedGame.isFreeFireFamily()) it.gameType.isFreeFireFamily()
                else it.gameType.isPubgFamily()
            }

            items(presetsForGame) { preset ->
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    borderColor = CyberCardBorder,
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
                                    text = preset.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Author: ${preset.author} • ${preset.clawGrip.title}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            NeonBadge(
                                text = preset.tag,
                                color = NeonPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = preset.description,
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recommended DPI: ${preset.recommendedDpi} | Stability: ${preset.stabilityRating}%",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            CyberButton(
                                text = "APPLY PRESET",
                                onClick = {
                                    onApplyPreset(preset)
                                    Toast.makeText(context, "${preset.name} applied to active Sensitivity!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.width(140.dp),
                                isPrimary = true,
                                accentColor = NeonCyan,
                                testTag = "apply_preset_${preset.id}"
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
