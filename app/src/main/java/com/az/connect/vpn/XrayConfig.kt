package com.az.connect.vpn

object XrayConfig {
    fun buildConfigJson(profile: VlessProfile): String {
        return """
        {
          "inbounds": [{
            "tag": "socks",
            "port": 10808,
            "protocol": "socks",
            "settings": { "auth": "noauth", "udp": true }
          }],
          "outbounds": [{
            "protocol": "vless",
            "settings": {
              "vnext": [{
                "address": "${profile.address}",
                "port": ${profile.port},
                "users": [{ "id": "${profile.uuid}", "encryption": "none" }]
              }]
            }
          }]
        }
        """.trimIndent()
    }
}
