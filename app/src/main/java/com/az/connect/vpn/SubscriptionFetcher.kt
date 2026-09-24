package com.az.connect.vpn

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object SubscriptionFetcher {

    private val client = OkHttpClient()

    private val SUB_URLS = listOf(
        "https://raw.githubusercontent.com/Pawdroid/Free-servers/main/sub",
        "https://raw.githubusercontent.com/freefq/free/master/v2"
    )

    val TARGET_COUNTRIES = listOf(
        "Singapore", "Japan", "United States", "Hong Kong", "South Korea", "Taiwan"
    )

    private suspend fun fetchRaw(url: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()
            val decoded = try {
                String(Base64.decode(body.trim(), Base64.DEFAULT))
            } catch (e: Exception) {
                body
            }
            decoded.lines().filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchAllProfiles(): List<VlessProfile> = withContext(Dispatchers.IO) {
        val allLines = SUB_URLS.map { async { fetchRaw(it) } }.awaitAll().flatten()

        val parsed = allLines.mapNotNull { line ->
            when {
                line.startsWith("vless://") -> VlessParser.parse(line)
                line.startsWith("vmess://") -> VmessParser.parse(line)
                else -> null
            }
        }

        parsed.map { async { CountryDetector.tagCountry(it) } }.awaitAll()
    }

    suspend fun fetchGroupedByCountry(): Map<String, List<VlessProfile>> {
        val all = fetchAllProfiles()
        return TARGET_COUNTRIES.associateWith { country ->
            all.filter { it.country == country }
        }.filterValues { it.isNotEmpty() }
    }

    suspend fun fetchBestProfileForCountry(country: String): VlessProfile? {
        val grouped = fetchGroupedByCountry()
        return grouped[country]?.firstOrNull()
    }
}
