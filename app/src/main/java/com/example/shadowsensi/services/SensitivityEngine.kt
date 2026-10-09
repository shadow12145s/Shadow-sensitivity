package com.example.shadowsensi.services

import com.example.shadowsensi.model.AimStyle
import com.example.shadowsensi.model.BgmiSensitivity
import com.example.shadowsensi.model.ClawGrip
import com.example.shadowsensi.model.ConfidenceLevel
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.FreeFireSensitivity
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.GyroMode
import com.example.shadowsensi.model.PlayStyle
import com.example.shadowsensi.model.PlayerProfile
import com.example.shadowsensi.model.PubgScopeSens
import com.example.shadowsensi.model.SensPref
import com.example.shadowsensi.model.SensitivityRecommendation

object SensitivityEngine {

    fun generateRecommendation(
        gameType: GameType,
        deviceInfo: DeviceHardwareInfo,
        playerProfile: PlayerProfile
    ): SensitivityRecommendation {
        val multiplier = playerProfile.sensPref.multiplier

        // Screen refresh calibration factor:
        // On 60Hz, users experience higher display lag, needing slightly higher sensi for drag.
        // On 120Hz, touch response is high, so slightly lower avoids over-flicking over the target.
        val hzFactor = when {
            deviceInfo.refreshRateHz >= 120 -> 0.94f
            deviceInfo.refreshRateHz >= 90 -> 0.98f
            else -> 1.05f
        }

        val dpiAnalysis = HardwareDetector.calculateDpiRecommendations(deviceInfo)

        val reasonings = mutableListOf<String>()

        if (deviceInfo.refreshRateHz >= 90) {
            reasonings.add("High Refresh (${deviceInfo.refreshRateHz}Hz) detected: Scope drift stabilized to stop over-flicking past enemy head hitboxes.")
        } else {
            reasonings.add("60Hz standard screen: Elevated General drag sensitivity by +6% to compensate for touch sampling latency.")
        }

        when (playerProfile.aimStyle) {
            AimStyle.DRAG_HEADSHOT -> reasonings.add("Drag Headshot profile active: Red Dot tuned for upward vertical acceleration snapping.")
            AimStyle.ONE_TAP -> reasonings.add("One Tap focus: High initial velocity with tight deceleration curve for crisp desert eagle/shotgun flicks.")
            AimStyle.SPRAY_CONTROL -> reasonings.add("Spray Master profile: ADS tuned specifically for 5.56mm laser stability.")
            AimStyle.TRACKING -> reasonings.add("Smooth Tracking profile: Lower jitter dampening on mid-range 3X/4X scopes.")
            AimStyle.MIXED -> reasonings.add("Hybrid setup: Balanced recoil pull-down and hipfire reaction speed.")
        }

        if (playerProfile.clawGrip == ClawGrip.TWO_FINGER) {
            reasonings.add("2-Finger layout: Increased camera turning radius because thumb screen travel is constrained.")
        } else {
            reasonings.add("${playerProfile.clawGrip.title}: Micro-aim fine-tuning enabled for dedicated aiming finger.")
        }

        val confidence = if (deviceInfo.hasGyroscope && deviceInfo.totalRamGb >= 4.0) {
            ConfidenceLevel.HIGH
        } else if (deviceInfo.totalRamGb >= 3.0) {
            ConfidenceLevel.MEDIUM
        } else {
            ConfidenceLevel.LOW
        }

        val touchAdvice = if (deviceInfo.refreshRateHz >= 120) {
            "Ultra-low latency detected (~14ms). Keep Android touch & hold delay on Short (0.5s) with Pointer Speed centered."
        } else {
            "Standard latency (~30ms). Set Android Pointer Speed to 80% and clean screen surface for friction-free drag flicks."
        }

        return if (gameType.isFreeFireFamily()) {
            val ff = calculateFreeFireSens(playerProfile, hzFactor, multiplier)
            SensitivityRecommendation(
                gameType = gameType,
                ffSens = ff,
                safeDpi = dpiAnalysis.safeDpi,
                smoothDpi = dpiAnalysis.smoothDpi,
                competitiveDpi = dpiAnalysis.competitiveDpi,
                touchResponseAdvice = touchAdvice,
                reasoning = reasonings,
                confidence = confidence,
                stabilityRating = if (playerProfile.sensPref == SensPref.LOW) 96 else if (playerProfile.sensPref == SensPref.ULTRA) 82 else 91,
                presetTag = if (gameType == GameType.FREE_FIRE_MAX) "SHADOW-MAX-RED" else "SHADOW-FF-DRAG"
            )
        } else {
            val bgmi = calculateBgmiSens(playerProfile, hzFactor, multiplier, deviceInfo.hasGyroscope)
            if (playerProfile.gyroMode == GyroMode.ALWAYS_ON) {
                reasonings.add("Full Gyroscope enabled: ADS camera down-scaled to prioritize wrist-tilt recoil control.")
            }
            SensitivityRecommendation(
                gameType = gameType,
                bgmiSens = bgmi,
                safeDpi = dpiAnalysis.safeDpi,
                smoothDpi = dpiAnalysis.smoothDpi,
                competitiveDpi = dpiAnalysis.competitiveDpi,
                touchResponseAdvice = touchAdvice,
                reasoning = reasonings,
                confidence = confidence,
                stabilityRating = if (playerProfile.gyroMode == GyroMode.ALWAYS_ON) 94 else 88,
                presetTag = if (gameType == GameType.BGMI) "BGMI-ESPORTS-400" else "PUBG-GLOBAL-PRO"
            )
        }
    }

    private fun calculateFreeFireSens(
        profile: PlayerProfile,
        hzFactor: Float,
        multiplier: Float
    ): FreeFireSensitivity {
        var baseGen = 180f
        var baseRed = 170f
        var base2x = 155f
        var base4x = 140f
        var baseSniper = 115f
        var baseLook = 130f

        when (profile.playStyle) {
            PlayStyle.RUSH -> {
                baseGen += 12f
                baseRed += 10f
                base2x += 8f
            }
            PlayStyle.BALANCED -> {
                baseGen += 4f
                baseRed += 4f
            }
            PlayStyle.PASSIVE -> {
                baseGen -= 8f
                base4x -= 8f
                baseSniper -= 5f
            }
            PlayStyle.SNIPER -> {
                baseGen -= 10f
                baseSniper += 15f
                base4x += 5f
            }
        }

        if (profile.aimStyle == AimStyle.DRAG_HEADSHOT || profile.aimStyle == AimStyle.ONE_TAP) {
            baseGen += 8f
            baseRed += 10f
        }

        val finalGen = (baseGen * hzFactor * multiplier).toInt().coerceIn(100, 200)
        val finalRed = (baseRed * hzFactor * multiplier).toInt().coerceIn(90, 200)
        val final2x = (base2x * hzFactor * multiplier).toInt().coerceIn(80, 200)
        val final4x = (base4x * hzFactor * multiplier).toInt().coerceIn(70, 200)
        val finalSniper = (baseSniper * multiplier).toInt().coerceIn(50, 180)
        val finalLook = (baseLook * multiplier).toInt().coerceIn(60, 190)

        return FreeFireSensitivity(
            general = finalGen,
            redDot = finalRed,
            scope2x = final2x,
            scope4x = final4x,
            sniperScope = finalSniper,
            freeLook = finalLook
        )
    }

    private fun calculateBgmiSens(
        profile: PlayerProfile,
        hzFactor: Float,
        multiplier: Float,
        hasGyro: Boolean
    ): BgmiSensitivity {
        val gyroBoost = if (profile.gyroMode == GyroMode.ALWAYS_ON && hasGyro) 1.25f else 1.0f

        val cam = PubgScopeSens(
            noScope = (120f * hzFactor * multiplier).toInt().coerceIn(70, 160),
            redDotHolo = (58f * hzFactor * multiplier).toInt().coerceIn(35, 90),
            scope2x = (40f * hzFactor * multiplier).toInt().coerceIn(25, 60),
            scope3x = (28f * multiplier).toInt().coerceIn(18, 45),
            scope4x = (20f * multiplier).toInt().coerceIn(12, 35),
            scope6x = (15f * multiplier).toInt().coerceIn(8, 25),
            scope8x = (11f * multiplier).toInt().coerceIn(6, 18)
        )

        // ADS recoil values
        val ads = PubgScopeSens(
            noScope = (115f * hzFactor * multiplier).toInt().coerceIn(65, 150),
            redDotHolo = (62f * multiplier).toInt().coerceIn(40, 95),
            scope2x = (42f * multiplier).toInt().coerceIn(28, 65),
            scope3x = (32f * multiplier).toInt().coerceIn(20, 50),
            scope4x = (24f * multiplier).toInt().coerceIn(14, 40),
            scope6x = (18f * multiplier).toInt().coerceIn(10, 30),
            scope8x = (12f * multiplier).toInt().coerceIn(6, 20)
        )

        // Gyroscope up to 400%
        val gyro = PubgScopeSens(
            noScope = (340f * gyroBoost * multiplier).toInt().coerceIn(200, 400),
            redDotHolo = (380f * gyroBoost * multiplier).toInt().coerceIn(250, 400),
            scope2x = (330f * gyroBoost * multiplier).toInt().coerceIn(200, 400),
            scope3x = (265f * gyroBoost * multiplier).toInt().coerceIn(160, 350),
            scope4x = (210f * gyroBoost * multiplier).toInt().coerceIn(130, 290),
            scope6x = (130f * gyroBoost * multiplier).toInt().coerceIn(80, 200),
            scope8x = (85f * gyroBoost * multiplier).toInt().coerceIn(50, 140)
        )

        return BgmiSensitivity(camera = cam, ads = ads, gyro = gyro)
    }
}
