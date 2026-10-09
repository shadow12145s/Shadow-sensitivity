package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.shadowsensi.model.DeviceHardwareInfo
import com.example.shadowsensi.model.GameType
import com.example.shadowsensi.model.PlayerProfile
import com.example.shadowsensi.services.SensitivityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Shadow Sensi AI", appName)
    }

    @Test
    fun `test free fire sensitivity recommendation generation`() {
        val mockDevice = DeviceHardwareInfo(
            deviceModel = "Test Phone",
            manufacturer = "Samsung",
            androidVersion = "14",
            sdkInt = 34,
            totalRamGb = 8.0,
            availableRamGb = 4.0,
            refreshRateHz = 120,
            screenResolution = "1080x2400",
            densityDpi = 440,
            batteryPct = 85,
            batteryTempCelsius = 34f,
            isCharging = false,
            hasGyroscope = true,
            hasAccelerometer = true,
            cpuCores = 8
        )

        val rec = SensitivityEngine.generateRecommendation(
            gameType = GameType.FREE_FIRE,
            deviceInfo = mockDevice,
            playerProfile = PlayerProfile()
        )

        assertNotNull(rec)
        assertTrue(rec.ffSens.general in 100..200)
        assertTrue(rec.ffSens.redDot in 90..200)
        assertTrue(rec.reasoning.isNotEmpty())
    }

    @Test
    fun `test bgmi sensitivity recommendation generation`() {
        val mockDevice = DeviceHardwareInfo(
            deviceModel = "Test Phone",
            manufacturer = "OnePlus",
            androidVersion = "14",
            sdkInt = 34,
            totalRamGb = 12.0,
            availableRamGb = 6.0,
            refreshRateHz = 90,
            screenResolution = "1080x2400",
            densityDpi = 440,
            batteryPct = 90,
            batteryTempCelsius = 32f,
            isCharging = false,
            hasGyroscope = true,
            hasAccelerometer = true,
            cpuCores = 8
        )

        val rec = SensitivityEngine.generateRecommendation(
            gameType = GameType.BGMI,
            deviceInfo = mockDevice,
            playerProfile = PlayerProfile()
        )

        assertNotNull(rec)
        assertTrue(rec.bgmiSens.gyro.redDotHolo in 100..400)
        assertTrue(rec.bgmiSens.ads.scope3x in 10..100)
    }
}
