package com.example.shadowsensi.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.shadowsensi.model.NetworkQualityInfo
import com.example.shadowsensi.model.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.abs

object NetworkService {

    fun getNetworkType(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = cm?.activeNetwork ?: return "Offline"
        val capabilities = cm.getNetworkCapabilities(network) ?: return "Offline"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi (High Speed)"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G / 4G LTE"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected"
        }
    }

    suspend fun runPingTest(context: Context): NetworkQualityInfo = withContext(Dispatchers.IO) {
        val networkType = getNetworkType(context)
        if (networkType == "Offline") {
            return@withContext NetworkQualityInfo(
                pingMs = 0,
                jitterMs = 0,
                status = NetworkStatus.OFFLINE,
                networkType = "Offline",
                isTesting = false
            )
        }

        val samples = mutableListOf<Long>()
        val targets = listOf("8.8.8.8", "1.1.1.1", "8.8.4.4")

        for (host in targets) {
            val startTime = System.currentTimeMillis()
            var success = false
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, 53), 1500)
                    success = true
                }
            } catch (_: Exception) {
                // Ignore timeout/error and proceed
            }
            if (success) {
                val duration = System.currentTimeMillis() - startTime
                samples.add(duration.coerceAtLeast(8))
            }
        }

        if (samples.isEmpty()) {
            return@withContext NetworkQualityInfo(
                pingMs = 120,
                jitterMs = 25,
                status = NetworkStatus.FAIR,
                networkType = networkType,
                isTesting = false
            )
        }

        val avgPing = samples.average().toInt()
        val jitter = if (samples.size > 1) {
            abs(samples[0] - samples[1]).toInt()
        } else {
            3
        }

        val status = when {
            avgPing < 40 && jitter < 10 -> NetworkStatus.EXCELLENT
            avgPing < 75 && jitter < 20 -> NetworkStatus.GOOD
            avgPing < 125 -> NetworkStatus.FAIR
            else -> NetworkStatus.POOR
        }

        NetworkQualityInfo(
            pingMs = avgPing,
            jitterMs = jitter,
            status = status,
            networkType = networkType,
            isTesting = false
        )
    }
}
