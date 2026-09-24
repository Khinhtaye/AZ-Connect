package com.az.connect.vpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AZVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        const val ACTION_CONNECT = "com.az.connect.CONNECT"
        const val ACTION_DISCONNECT = "com.az.connect.DISCONNECT"
        const val EXTRA_COUNTRY = "country"
        private const val TAG = "AZVpnService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val country = intent.getStringExtra(EXTRA_COUNTRY) ?: "Singapore"
                connectToCountry(country)
            }
            ACTION_DISCONNECT -> stopVpn()
        }
        return START_STICKY
    }

    private fun connectToCountry(country: String) {
        scope.launch {
            val profile = SubscriptionFetcher.fetchBestProfileForCountry(country)
            if (profile == null) {
                Log.e(TAG, "No profile found for $country")
                return@launch
            }
            Log.i(TAG, "Connecting via ${profile.country}: ${profile.remark} (${profile.address}:${profile.port})")
            val configJson = XrayConfig.buildConfigJson(profile)
            startVpn(configJson)
        }
    }

    private fun startVpn(configJson: String) {
        try {
            val builder = Builder()
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .setSession("AZ-Connect")
                .setMtu(1500)

            vpnInterface = builder.establish()
            Log.i(TAG, "VPN interface established")

            // TODO: Xray core native binding
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            Log.i(TAG, "VPN stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop VPN", e)
        }
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
