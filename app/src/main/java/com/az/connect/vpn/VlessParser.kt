package com.az.connect.vpn

import android.net.Uri

data class VlessProfile(
    val uuid: String,
    val address: String,
    val port: Int,
    val remark: String,
    val country: String = "Unknown"
)

object VlessParser {
    fun parse(link: String): VlessProfile? {
        return try {
            val uri = Uri.parse(link)
            val uuid = uri.userInfo ?: return null
            val address = uri.host ?: return null
            val port = uri.port
            val remark = uri.fragment ?: "Server"
            VlessProfile(uuid, address, port, remark)
        } catch (e: Exception) {
            null
        }
    }
}
