package com.az.connect.vpn

import android.util.Base64
import org.json.JSONObject

object VmessParser {
    fun parse(link: String): VlessProfile? {
        return try {
            val raw = link.removePrefix("vmess://")
            val decoded = String(Base64.decode(raw, Base64.DEFAULT))
            val json = JSONObject(decoded)
            VlessProfile(
                uuid = json.getString("id"),
                address = json.getString("add"),
                port = json.getString("port").toInt(),
                remark = json.optString("ps", "Server")
            )
        } catch (e: Exception) {
            null
        }
    }
}
