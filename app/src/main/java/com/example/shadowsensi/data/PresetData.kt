package com.example.shadowsensi.data

import com.example.shadowsensi.model.BgmiSensitivity
import com.example.shadowsensi.model.ClawGrip
import com.example.shadowsensi.model.FreeFireSensitivity
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.HudButton
import com.example.shadowsensi.model.HudLayout
import com.example.shadowsensi.model.ProPreset
import com.example.shadowsensi.model.PubgScopeSens

object PresetData {

    val PRO_PRESETS = listOf(
        // Free Fire Presets
        ProPreset(
            id = "ff_one_tap",
            name = "One-Tap Beast",
            gameType = GameType.FREE_FIRE,
            tag = "ONE-TAP",
            description = "High initial flick response for instant headshot snaps with Desert Eagle & M1887 shotgun.",
            author = "Shadow Esports Pro",
            recommendedDpi = 480,
            clawGrip = ClawGrip.FOUR_FINGER,
            ffSens = FreeFireSensitivity(
                general = 195,
                redDot = 182,
                scope2x = 165,
                scope4x = 148,
                sniperScope = 130,
                freeLook = 145
            ),
            stabilityRating = 94
        ),
        ProPreset(
            id = "ff_fast_drag",
            name = "Fast Drag Velocity",
            gameType = GameType.FREE_FIRE,
            tag = "FAST-DRAG",
            description = "Optimized vertical drag curve for SMG spray (MP40, UMP) with consistent red numbers.",
            author = "Apex Predator",
            recommendedDpi = 440,
            clawGrip = ClawGrip.THREE_FINGER,
            ffSens = FreeFireSensitivity(
                general = 188,
                redDot = 176,
                scope2x = 158,
                scope4x = 142,
                sniperScope = 120,
                freeLook = 138
            ),
            stabilityRating = 96
        ),
        ProPreset(
            id = "ff_max_ultra",
            name = "Max HD Smoothness",
            gameType = GameType.FREE_FIRE_MAX,
            tag = "MAX-SMOOTH",
            description = "Tailored for Free Fire MAX 90Hz/120Hz physics to prevent jitter while turning 180 degrees.",
            author = "Neon Phantom",
            recommendedDpi = 460,
            clawGrip = ClawGrip.FOUR_FINGER,
            ffSens = FreeFireSensitivity(
                general = 182,
                redDot = 170,
                scope2x = 154,
                scope4x = 138,
                sniperScope = 115,
                freeLook = 130
            ),
            stabilityRating = 98
        ),
        ProPreset(
            id = "ff_low_end",
            name = "Low-End Booster",
            gameType = GameType.FREE_FIRE,
            tag = "60HZ-BOOST",
            description = "Maximum sensitivity compensation for 3GB/4GB RAM phones to eliminate touch sampling lag.",
            author = "Vortex Engine",
            recommendedDpi = 411,
            clawGrip = ClawGrip.TWO_FINGER,
            ffSens = FreeFireSensitivity(
                general = 200,
                redDot = 192,
                scope2x = 172,
                scope4x = 158,
                sniperScope = 125,
                freeLook = 160
            ),
            stabilityRating = 89
        ),

        // BGMI Presets
        ProPreset(
            id = "bgmi_gyro_god",
            name = "400% Gyroscope God",
            gameType = GameType.BGMI,
            tag = "GYRO-PRO",
            description = "Esports tournament full-gyro setup. Zero camera swipe needed for recoil control.",
            author = "Titan India",
            recommendedDpi = 480,
            clawGrip = ClawGrip.FOUR_FINGER,
            bgmiSens = BgmiSensitivity(
                camera = PubgScopeSens(120, 60, 42, 30, 22, 16, 12),
                ads = PubgScopeSens(110, 50, 36, 25, 18, 14, 10),
                gyro = PubgScopeSens(380, 400, 360, 280, 220, 140, 90)
            ),
            stabilityRating = 97
        ),
        ProPreset(
            id = "bgmi_laser_spray",
            name = "Laser Spray Master",
            gameType = GameType.BGMI,
            tag = "LASER-ADS",
            description = "Precision ADS calibration for M416 + 3X scope sprays with pin-point accuracy.",
            author = "Glitch Esports",
            recommendedDpi = 440,
            clawGrip = ClawGrip.THREE_FINGER,
            bgmiSens = BgmiSensitivity(
                camera = PubgScopeSens(115, 55, 38, 28, 20, 15, 11),
                ads = PubgScopeSens(125, 68, 48, 36, 26, 20, 14),
                gyro = PubgScopeSens(300, 340, 290, 230, 180, 110, 75)
            ),
            stabilityRating = 93
        ),
        ProPreset(
            id = "pubg_global_pro",
            name = "Global PMGC Standard",
            gameType = GameType.PUBG_MOBILE,
            tag = "PMGC-META",
            description = "Official competitive circuit baseline tested across international mobile tournaments.",
            author = "Falcon PMGC",
            recommendedDpi = 520,
            clawGrip = ClawGrip.FOUR_FINGER,
            bgmiSens = BgmiSensitivity(
                camera = PubgScopeSens(125, 62, 44, 32, 24, 18, 13),
                ads = PubgScopeSens(118, 58, 40, 30, 22, 16, 12),
                gyro = PubgScopeSens(360, 380, 340, 270, 210, 130, 85)
            ),
            stabilityRating = 95
        )
    )

    fun getHudLayoutsForClaw(claw: ClawGrip, game: GameType): HudLayout {
        val buttons = when (claw) {
            ClawGrip.TWO_FINGER -> listOf(
                HudButton("fire_right", "Fire Button", 0.82f, 0.68f, 54f, 0.95f, "Right Thumb", 0xFFFF304F),
                HudButton("scope", "Scope In", 0.78f, 0.42f, 50f, 0.9f, "Right Thumb", 0xFF00D9FF),
                HudButton("jump", "Jump", 0.88f, 0.50f, 48f, 0.85f, "Right Thumb", 0xFF8B5CFF),
                HudButton("crouch", "Crouch", 0.70f, 0.80f, 46f, 0.85f, "Right Thumb", 0xFF8B5CFF),
                HudButton("joystick", "Movement", 0.18f, 0.70f, 68f, 0.8f, "Left Thumb", 0xFF00D9FF),
                HudButton("sprint", "Quick Sprint", 0.22f, 0.42f, 42f, 0.75f, "Left Thumb", 0xFF00E676)
            )
            ClawGrip.THREE_FINGER -> listOf(
                HudButton("fire_left", "Primary Fire", 0.18f, 0.18f, 62f, 0.95f, "Left Index", 0xFFFF304F),
                HudButton("joystick", "Movement", 0.18f, 0.72f, 68f, 0.8f, "Left Thumb", 0xFF00D9FF),
                HudButton("scope", "Scope In", 0.82f, 0.45f, 54f, 0.9f, "Right Thumb", 0xFF00D9FF),
                HudButton("jump", "Jump", 0.88f, 0.60f, 48f, 0.85f, "Right Thumb", 0xFF8B5CFF),
                HudButton("crouch", "Crouch", 0.74f, 0.72f, 48f, 0.85f, "Right Thumb", 0xFF8B5CFF),
                HudButton("reload", "Reload", 0.64f, 0.82f, 44f, 0.8f, "Right Thumb", 0xFFFFD600)
            )
            ClawGrip.FOUR_FINGER -> listOf(
                HudButton("fire_left", "Primary Fire", 0.16f, 0.18f, 66f, 0.95f, "Left Index", 0xFFFF304F),
                HudButton("scope", "Scope In", 0.84f, 0.18f, 58f, 0.95f, "Right Index", 0xFF00D9FF),
                HudButton("peek_left", "Left Peek", 0.72f, 0.28f, 46f, 0.9f, "Right Index", 0xFF00D9FF),
                HudButton("crouch", "Crouch", 0.84f, 0.58f, 52f, 0.9f, "Right Thumb", 0xFF8B5CFF),
                HudButton("jump", "Jump", 0.88f, 0.42f, 52f, 0.9f, "Right Thumb", 0xFF8B5CFF),
                HudButton("prone", "Prone", 0.74f, 0.78f, 44f, 0.8f, "Right Thumb", 0xFF8B5CFF),
                HudButton("joystick", "Movement", 0.18f, 0.72f, 68f, 0.85f, "Left Thumb", 0xFF00D9FF),
                HudButton("sprint", "Quick Sprint", 0.22f, 0.44f, 42f, 0.75f, "Left Thumb", 0xFF00E676)
            )
            else -> listOf(
                HudButton("fire_left", "Primary Fire", 0.14f, 0.18f, 66f, 0.95f, "Left Index", 0xFFFF304F),
                HudButton("crouch", "Crouch", 0.26f, 0.18f, 50f, 0.9f, "Left Middle", 0xFF8B5CFF),
                HudButton("scope", "Scope In", 0.86f, 0.18f, 58f, 0.95f, "Right Index", 0xFF00D9FF),
                HudButton("jump", "Jump", 0.74f, 0.18f, 52f, 0.9f, "Right Middle", 0xFF8B5CFF),
                HudButton("peek_left", "Peek Left", 0.86f, 0.38f, 48f, 0.85f, "Right Thumb", 0xFF00D9FF),
                HudButton("peek_right", "Peek Right", 0.74f, 0.38f, 48f, 0.85f, "Right Thumb", 0xFF00D9FF),
                HudButton("joystick", "Movement", 0.18f, 0.72f, 68f, 0.85f, "Left Thumb", 0xFF00D9FF)
            )
        }

        return HudLayout(
            id = "hud_${claw.name.lowercase()}_${game.id}",
            title = "${claw.title} Esports HUD",
            clawGrip = claw,
            gameType = game,
            description = "Calibrated button clustering for optimal finger reach and accidental touch prevention.",
            buttons = buttons
        )
    }
}
