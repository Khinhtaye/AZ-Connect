package com.az.connect.vpn

import android.os.ParcelFileDescriptor
import android.util.Log
import libv2ray.CoreCallbackHandler
import libv2ray.CoreController
import libv2ray.Libv2ray

object XrayCoreManager {

    private const val TAG = "XrayCoreManager"

    private val callback = object : CoreCallbackHandler {
        override fun startup(): Long {
            Log.i(TAG, "Xray core startup")
            return 0
        }

        override fun shutdown(): Long {
            Log.i(TAG, "Xray core shutdown")
            return 0
        }

        override fun onEmitStatus(l: Long, s: String?): Long {
            Log.i(TAG, "Xray status: $s")
            return 0
        }
    }

    private var controller: CoreController? = null

    fun start(configJson: String, vpnInterface: ParcelFileDescriptor?): Boolean {
        return try {
            if (controller == null) {
                controller = Libv2ray.newCoreController(callback)
            }
            val fd = vpnInterface?.fd ?: 0
            controller?.startLoop(configJson, fd)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Xray core", e)
            false
        }
    }

    fun stop() {
        try {
            controller?.stopLoop()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop Xray core", e)
        }
    }

    fun isRunning(): Boolean {
        return controller?.isRunning ?: false
    }
}
