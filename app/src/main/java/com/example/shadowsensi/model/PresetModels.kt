package com.example.shadowsensi.model

data class ProPreset(
    val id: String,
    val name: String,
    val gameType: GameType,
    val tag: String,
    val description: String,
    val author: String,
    val recommendedDpi: Int,
    val clawGrip: ClawGrip,
    val ffSens: FreeFireSensitivity? = null,
    val bgmiSens: BgmiSensitivity? = null,
    val stabilityRating: Int = 92
)

data class HudButton(
    val id: String,
    val name: String,
    val xPercent: Float, // 0.0 to 1.0 (relative to canvas width)
    val yPercent: Float, // 0.0 to 1.0 (relative to canvas height)
    val sizeDp: Float,   // 32 to 90 dp
    val opacity: Float,  // 0.4 to 1.0
    val fingerAssigned: String, // "Left Thumb", "Left Index", "Right Thumb", "Right Index"
    val colorHex: Long = 0xFF00D9FF
)

data class HudLayout(
    val id: String,
    val title: String,
    val clawGrip: ClawGrip,
    val gameType: GameType,
    val description: String,
    val buttons: List<HudButton>
)
