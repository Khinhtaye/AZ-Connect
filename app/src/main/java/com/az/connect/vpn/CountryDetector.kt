package com.az.connect.vpn

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object CountryDetector {

    private val client = OkHttpClient()

    private val keywordMap = mapOf(
        "singapore" to "Singapore", "sg" to "Singapore", "🇸🇬" to "Singapore",
        "japan" to "Japan", "jp" to "Japan", "🇯🇵" to "Japan", "tokyo" to "Japan",
        "united states" to "United States", "usa" to "United States", "us" to "United States", "🇺🇸" to "United States",
        "germany" to "Germany", "de" to "Germany", "🇩🇪" to "Germany",
        "hong kong" to "Hong Kong", "hk" to "Hong Kong", "🇭🇰" to "Hong Kong",
        "united kingdom" to "United Kingdom", "uk" to "United Kingdom", "gb" to "United Kingdom", "🇬🇧" to "United Kingdom",
        "south korea" to "South Korea", "kr" to "South Korea", "korea" to "South Korea", "🇰🇷" to "South Korea",
        "taiwan" to "Taiwan", "tw" to "Taiwan", "🇹🇼" to "Taiwan",
        "india" to "India", "in" to "India", "🇮🇳" to "India",
        "canada" to "Canada", "ca" to "Canada", "🇨🇦" to "Canada"
    )

    fun detectFromRemark(remark: String): String? {
        val lower = remark.lowercase()
        for ((key, country) in keywordMap) {
            if (lower.contains(key)) return country
        }
        return null
    }

    suspend fun detectFromIp(address: String): String = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("http://ip-api.com/json/$address?fields=country")
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext "Unknown"
            JSONObject(body).optString("country", "Unknown")
        } catch (e: Exception) {
            "Unknown"
        }
    }

    suspend fun tagCountry(profile: VlessProfile): VlessProfile {
        val fromRemark = detectFromRemark(profile.remark)
        if (fromRemark != null) return profile.copy(country = fromRemark)
        val fromIp = detectFromIp(profile.address)
        return profile.copy(country = fromIp)
    }
}
