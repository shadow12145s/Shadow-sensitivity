package com.example.shadowsensi.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed

enum class GameType(
    val id: String,
    val displayName: String,
    val shortName: String,
    val accentColor: Color,
    val badge: String,
    val description: String
) {
    FREE_FIRE(
        id = "free_fire",
        displayName = "Garena Free Fire",
        shortName = "Free Fire",
        accentColor = NeonOrange,
        badge = "FF",
        description = "Fast-paced battle royale with drag headshot mechanics"
    ),
    FREE_FIRE_MAX(
        id = "free_fire_max",
        displayName = "Free Fire MAX",
        shortName = "FF MAX",
        accentColor = NeonRed,
        badge = "FF MAX",
        description = "Enhanced HD graphics, high-refresh rate physics"
    ),
    BGMI(
        id = "bgmi",
        displayName = "Battlegrounds Mobile India",
        shortName = "BGMI",
        accentColor = NeonCyan,
        badge = "BGMI",
        description = "Tactical shooter with ADS and full 400% gyroscope support"
    ),
    PUBG_MOBILE(
        id = "pubg_mobile",
        displayName = "PUBG Mobile Global",
        shortName = "PUBG",
        accentColor = NeonPurple,
        badge = "PUBG",
        description = "Global competitive esports with advanced recoil curves"
    );

    fun isFreeFireFamily(): Boolean = this == FREE_FIRE || this == FREE_FIRE_MAX
    fun isPubgFamily(): Boolean = this == BGMI || this == PUBG_MOBILE
}

enum class PlayStyle(val title: String, val desc: String) {
    RUSH("Rush Aggressive", "Close-range entry fragger, high mobility and rapid turns"),
    BALANCED("Balanced Flex", "Adapts between mid-range spraying and zone control"),
    PASSIVE("Zone & Anchor", "Patient long-range angles, high stability over speed"),
    SNIPER("Sniper Specialist", "Precision single-shot flicking, stable scope sensitivity")
}

enum class AimStyle(val title: String, val desc: String) {
    DRAG_HEADSHOT("Drag Headshot", "Vertical swipe up for instant red numbers (FF meta)"),
    ONE_TAP("One Tap Flick", "Precision snap and single-bullet headshot flicks"),
    SPRAY_CONTROL("Spray Master", "Recoil pull-down mastery for 5.56mm and 7.62mm weapons"),
    TRACKING("Smooth Tracking", "Fluid crosshair tracking on moving vehicles/targets"),
    MIXED("Hybrid Mastery", "Adaptive flicking and spray recoil compensation")
}

enum class ClawGrip(val title: String, val fingers: Int, val description: String) {
    TWO_FINGER("2 Finger (Thumbs)", 2, "Standard mobile layout, easy to learn"),
    THREE_FINGER("3 Finger Claw", 3, "Index finger handles fire/jump while thumbs aim"),
    FOUR_FINGER("4 Finger Claw", 4, "Esports standard for simultaneous jump, crouch & shoot"),
    FIVE_FINGER("5 Finger Claw", 5, "Advanced competitive layout with peak peek control"),
    SIX_FINGER("6 Finger Claw (Tablet)", 6, "Maximum simultaneous actions for large devices")
}

enum class GyroMode(val title: String) {
    OFF("Gyroscope Off"),
    SCOPE_ONLY("Scope On Only"),
    ALWAYS_ON("Always On (Full Gyro)")
}

enum class SensPref(val title: String, val multiplier: Float) {
    LOW("Low (Precision)", 0.85f),
    BALANCED("Medium (Default)", 1.0f),
    HIGH("High (Speed)", 1.18f),
    ULTRA("Ultra (Flick Pro)", 1.32f)
}

data class PlayerProfile(
    val playStyle: PlayStyle = PlayStyle.RUSH,
    val aimStyle: AimStyle = AimStyle.DRAG_HEADSHOT,
    val clawGrip: ClawGrip = ClawGrip.FOUR_FINGER,
    val gyroMode: GyroMode = GyroMode.ALWAYS_ON,
    val sensPref: SensPref = SensPref.HIGH
)

data class FreeFireSensitivity(
    val general: Int = 185,
    val redDot: Int = 175,
    val scope2x: Int = 160,
    val scope4x: Int = 145,
    val sniperScope: Int = 120,
    val freeLook: Int = 140
)

data class PubgScopeSens(
    val noScope: Int,
    val redDotHolo: Int,
    val scope2x: Int,
    val scope3x: Int,
    val scope4x: Int,
    val scope6x: Int,
    val scope8x: Int
)

data class BgmiSensitivity(
    val camera: PubgScopeSens = PubgScopeSens(
        noScope = 120,
        redDotHolo = 60,
        scope2x = 42,
        scope3x = 30,
        scope4x = 22,
        scope6x = 16,
        scope8x = 12
    ),
    val ads: PubgScopeSens = PubgScopeSens(
        noScope = 115,
        redDotHolo = 58,
        scope2x = 40,
        scope3x = 28,
        scope4x = 20,
        scope6x = 15,
        scope8x = 11
    ),
    val gyro: PubgScopeSens = PubgScopeSens(
        noScope = 360,
        redDotHolo = 380,
        scope2x = 340,
        scope3x = 270,
        scope4x = 210,
        scope6x = 130,
        scope8x = 85
    )
)

enum class ConfidenceLevel(val label: String, val color: Color) {
    HIGH("HIGH CONFIDENCE", Color(0xFF00E676)),
    MEDIUM("MODERATE CONFIDENCE", Color(0xFFFFD600)),
    LOW("ESTIMATED BASELINE", Color(0xFFFF8A00))
}

data class SensitivityRecommendation(
    val gameType: GameType,
    val ffSens: FreeFireSensitivity = FreeFireSensitivity(),
    val bgmiSens: BgmiSensitivity = BgmiSensitivity(),
    val safeDpi: Int = 411,
    val smoothDpi: Int = 480,
    val competitiveDpi: Int = 540,
    val touchResponseAdvice: String,
    val reasoning: List<String>,
    val confidence: ConfidenceLevel,
    val stabilityRating: Int, // 1 to 100
    val presetTag: String
)
