package com.example.shadowsensi.model

import androidx.compose.ui.graphics.Color

data class DeviceHardwareInfo(
    val deviceModel: String,
    val manufacturer: String,
    val androidVersion: String,
    val sdkInt: Int,
    val totalRamGb: Double,
    val availableRamGb: Double,
    val refreshRateHz: Int,
    val screenResolution: String,
    val densityDpi: Int,
    val batteryPct: Int,
    val batteryTempCelsius: Float?,
    val isCharging: Boolean,
    val hasGyroscope: Boolean,
    val hasAccelerometer: Boolean,
    val cpuCores: Int
)

enum class CompetitiveTier(val rank: String, val title: String, val color: Color) {
    TIER_S_PLUS("S+", "Esports Elite", Color(0xFF00D9FF)),
    TIER_S("S", "Competitive Ready", Color(0xFF00E676)),
    TIER_A("A", "High Performance", Color(0xFF8B5CFF)),
    TIER_B("B", "Balanced Gaming", Color(0xFFFFD600)),
    TIER_C("C", "Casual Capable", Color(0xFFFF8A00))
}

data class DeviceGamingScore(
    val estimatedFps: Int, // e.g. 120, 90, 60, 45
    val performanceScore: Int, // 0 - 100
    val smoothnessScore: Int, // 0 - 100
    val stabilityScore: Int, // 0 - 100
    val touchLatencyEstimateMs: Int, // e.g. 12ms, 18ms, 28ms
    val competitiveTier: CompetitiveTier
)

enum class NetworkStatus(val label: String, val color: Color) {
    EXCELLENT("EXCELLENT (ESPORTS READY)", Color(0xFF00E676)),
    GOOD("GOOD (SMOOTH PLAY)", Color(0xFF00D9FF)),
    FAIR("FAIR (SLIGHT JITTER)", Color(0xFFFFD600)),
    POOR("POOR (HIGH LATENCY)", Color(0xFFFF304F)),
    OFFLINE("DISCONNECTED", Color(0xFF72728A))
}

data class NetworkQualityInfo(
    val pingMs: Int,
    val jitterMs: Int,
    val status: NetworkStatus,
    val networkType: String,
    val isTesting: Boolean = false
)

data class DpiAnalysis(
    val currentDpi: Int,
    val safeDpi: Int,
    val smoothDpi: Int,
    val competitiveDpi: Int,
    val touchSpeedRecommendation: String,
    val aimAccelerationGuide: String,
    val stabilityWarning: String
)
