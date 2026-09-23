package com.az.connect

import android.content.Intent
import android.net.VpnService

class AZVpnService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
