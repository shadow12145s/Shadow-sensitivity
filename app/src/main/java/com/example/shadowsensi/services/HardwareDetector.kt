package com.example.shadowsensi.services

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.view.WindowManager
import com.example.shadowsensi.model.CompetitiveTier
import com.example.shadowsensi.model.DeviceGamingScore
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.DpiAnalysis

object HardwareDetector {

    fun detectHardware(context: Context): DeviceHardwareInfo {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            wm?.defaultDisplay
        }

        val refreshRate = display?.refreshRate?.toInt() ?: 60

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val densityDpi = metrics.densityDpi
        val resolution = "${width}x${height}"

        // Memory info
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)

        val totalRamGb = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)
        val availRamGb = memInfo.availMem.toDouble() / (1024 * 1024 * 1024)

        // Battery Info via sticky broadcast
        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, batteryFilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 85

        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val rawTemp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val batteryTempCelsius = if (rawTemp > 0) rawTemp / 10.0f else null

        // Sensors
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val hasGyro = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val hasAccel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null

        // CPU cores
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return DeviceHardwareInfo(
            deviceModel = Build.MODEL ?: "Android Device",
            manufacturer = (Build.MANUFACTURER ?: "Generic").replaceFirstChar { it.uppercase() },
            androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
            sdkInt = Build.VERSION.SDK_INT,
            totalRamGb = String.format(java.util.Locale.US, "%.1f", totalRamGb).toDoubleOrNull() ?: 6.0,
            availableRamGb = String.format(java.util.Locale.US, "%.1f", availRamGb).toDoubleOrNull() ?: 3.0,
            refreshRateHz = if (refreshRate > 0) refreshRate else 60,
            screenResolution = resolution,
            densityDpi = densityDpi,
            batteryPct = batteryPct,
            batteryTempCelsius = batteryTempCelsius,
            isCharging = isCharging,
            hasGyroscope = hasGyro,
            hasAccelerometer = hasAccel,
            cpuCores = cpuCores
        )
    }

    fun calculateGamingScore(info: DeviceHardwareInfo): DeviceGamingScore {
        // FPS capability estimate
        val estimatedFps = when {
            info.refreshRateHz >= 120 && info.totalRamGb >= 6.0 -> 120
            info.refreshRateHz >= 90 && info.totalRamGb >= 4.0 -> 90
            info.totalRamGb >= 3.0 -> 60
            else -> 45
        }

        // Performance score calculation based on RAM, CPU, Display
        var perfScore = 50
        if (info.totalRamGb >= 12.0) perfScore += 25
        else if (info.totalRamGb >= 8.0) perfScore += 20
        else if (info.totalRamGb >= 6.0) perfScore += 14
        else if (info.totalRamGb >= 4.0) perfScore += 8

        if (info.cpuCores >= 8) perfScore += 15 else perfScore += 8
        if (info.refreshRateHz >= 120) perfScore += 10 else if (info.refreshRateHz >= 90) perfScore += 6
        perfScore = perfScore.coerceIn(40, 99)

        // Smoothness score
        val smoothness = when {
            info.refreshRateHz >= 120 -> 96
            info.refreshRateHz >= 90 -> 88
            info.refreshRateHz >= 60 && info.totalRamGb >= 6.0 -> 78
            else -> 65
        }

        // Stability score (factors battery temp if known)
        var stability = 88
        val temp = info.batteryTempCelsius
        if (temp != null) {
            when {
                temp > 43f -> stability = 58 // Thermal throttling alert
                temp > 39f -> stability = 72
                temp > 35f -> stability = 85
                else -> stability = 95
            }
        }
        if (info.availableRamGb < 1.5) stability -= 10
        stability = stability.coerceIn(45, 98)

        // Touch latency estimation based on display scan rate & refresh
        val touchLatency = when {
            info.refreshRateHz >= 120 -> 14
            info.refreshRateHz >= 90 -> 20
            else -> 32
        }

        val tier = when {
            perfScore >= 90 && info.hasGyroscope -> CompetitiveTier.TIER_S_PLUS
            perfScore >= 80 -> CompetitiveTier.TIER_S
            perfScore >= 70 -> CompetitiveTier.TIER_A
            perfScore >= 60 -> CompetitiveTier.TIER_B
            else -> CompetitiveTier.TIER_C
        }

        return DeviceGamingScore(
            estimatedFps = estimatedFps,
            performanceScore = perfScore,
            smoothnessScore = smoothness,
            stabilityScore = stability,
            touchLatencyEstimateMs = touchLatency,
            competitiveTier = tier
        )
    }

    fun calculateDpiRecommendations(info: DeviceHardwareInfo): DpiAnalysis {
        val current = info.densityDpi
        // Safe DPI is 15-20% higher than default without causing UI overlap
        val safe = (current * 1.15f).toInt()
        val smooth = (current * 1.28f).toInt()
        val comp = (current * 1.40f).toInt().coerceAtMost(600)

        val warning = if (comp > 580) {
            "CAUTION: Setting Smallest Width (DPI) above 580 on some Android devices may reset keyboard layouts. Safe DPI is strongly recommended."
        } else {
            "NOTICE: Test DPI adjustments in developer settings gradually by +30 steps to ensure system icon proportions remain comfortable."
        }

        return DpiAnalysis(
            currentDpi = current,
            safeDpi = safe,
            smoothDpi = smooth,
            competitiveDpi = comp,
            touchSpeedRecommendation = if (info.refreshRateHz >= 90) "Maximum / Fast" else "3/4 Pointer Speed",
            aimAccelerationGuide = "Disable pointer precision/acceleration in Android accessibility for 1:1 muscle memory swipe tracking.",
            stabilityWarning = warning
        )
    }
}
