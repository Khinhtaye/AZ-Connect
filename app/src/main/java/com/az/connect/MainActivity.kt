package com.az.connect

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.az.connect.ui.theme.AZConnectTheme
import com.az.connect.vpn.AZVpnService
import com.az.connect.vpn.SubscriptionFetcher

class MainActivity : ComponentActivity() {

    private var pendingCountry: String? = null

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnService(pendingCountry ?: "Singapore")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AZConnectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        onConnect = { country -> requestVpnPermission(country) },
                        onDisconnect = { stopVpnService() }
                    )
                }
            }
        }
    }

    private fun requestVpnPermission(country: String) {
        pendingCountry = country
        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            startVpnService(country)
        }
    }

    private fun startVpnService(country: String) {
        val intent = Intent(this, AZVpnService::class.java).apply {
            action = AZVpnService.ACTION_CONNECT
            putExtra(AZVpnService.EXTRA_COUNTRY, country)
        }
        startService(intent)
    }

    private fun stopVpnService() {
        val intent = Intent(this, AZVpnService::class.java).apply {
            action = AZVpnService.ACTION_DISCONNECT
        }
        startService(intent)
    }
}

@Composable
fun MainScreen(
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit
) {
    var selectedCountry by remember { mutableStateOf("Singapore") }
    var isConnected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "A&Z Connect",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isConnected) "Connected: $selectedCountry" else "Disconnected",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(SubscriptionFetcher.TARGET_COUNTRIES) { country ->
                CountryRow(
                    country = country,
                    selected = country == selectedCountry,
                    onClick = {
                        if (!isConnected) selectedCountry = country
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isConnected) {
                    onDisconnect()
                    isConnected = false
                } else {
                    onConnect(selectedCountry)
                    isConnected = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(if (isConnected) "Disconnect" else "Connect")
        }
    }
}

@Composable
fun CountryRow(
    country: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = country, style = MaterialTheme.typography.bodyLarge)
    }
}
