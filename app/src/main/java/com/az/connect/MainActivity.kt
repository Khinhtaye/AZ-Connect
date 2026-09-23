package com.az.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AZConnectApp()
        }
    }
}

@Composable
fun AZConnectApp() {
    var isConnected by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Title
            Text(
                text = "A&Z Connect",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Connection Status & Big Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            color = if (isConnected) Color(0xFF00E676) else Color(0xFFFF5252),
                            shape = CircleShape
                        )
                        .clickable { isConnected = !isConnected },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isConnected) "CONNECTED" else "CONNECT",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isConnected) "Status: Protected & Encrypted" else "Status: Disconnected",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            // Server Selector Card (Lantern Style)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Optimal Server", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(text = "Singapore (Fastest)", color = Color.Gray, fontSize = 12.sp)
                    }
                    Text(text = "42 ms", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
