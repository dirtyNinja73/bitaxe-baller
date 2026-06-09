package com.bitaxeballer.mobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitaxeballer.mobile.data.DeviceRepository

@Composable
fun DeviceDetailScreen(
    ip: String,
    repository: DeviceRepository,
    onBack: () -> Unit
) {
    val vm = remember(ip) { DeviceDetailViewModel(repository, ip) }
    val ui by vm.ui.collectAsState()

    DisposableEffect(Unit) {
        vm.startPolling()
        onDispose { vm.stopPolling() }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onBack) { Text("Back") }
            }

            Text("Device Detail", style = MaterialTheme.typography.headlineSmall)
            Text(ui.disclaimer, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

            ui.error?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }

            val d = ui.detail
            if (d == null) {
                Text("Loading...")
                return@Column
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(d.label?.ifBlank { d.ip } ?: d.ip, fontWeight = FontWeight.SemiBold)
                    Text("Online: ${d.online}")
                    Text("Hashrate: ${"%.2f".format(d.hashrate ?: 0.0)} GH/s")
                    Text("ASIC temp: ${"%.2f".format(d.asicTemp ?: 0.0)}°C")
                    Text("VR temp: ${"%.2f".format(d.vrTemp ?: 0.0)}°C")
                    Text("Efficiency: ${"%.2f".format(d.efficiency ?: 0.0)} J/TH")
                    Text("Frequency: ${"%.0f".format(d.frequency ?: 0.0)} MHz")
                    Text("Core voltage: ${"%.0f".format(d.coreVoltage ?: 0.0)} mV")
                }
            }

            Text("Recommendations", fontWeight = FontWeight.Bold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(d.recommendations) { rec ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(rec.title ?: "Recommendation", fontWeight = FontWeight.SemiBold)
                            Text("Severity: ${rec.severity ?: "info"}")
                            Text(rec.body ?: "")
                        }
                    }
                }
            }
        }
    }
}
