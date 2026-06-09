package com.bitaxeballer.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitaxeballer.mobile.data.DeviceRepository
import com.bitaxeballer.mobile.data.HostPreferences
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: DeviceRepository,
    onOpenDevice: (String) -> Unit
) {
    val context = LocalContext.current
    val vm = remember { HomeViewModel(repository) }
    val hostPrefs = remember { HostPreferences(context) }
    val ui by vm.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var ipInput by remember { mutableStateOf("") }
    var labelInput by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        vm.startPolling()
        onDispose { vm.stopPolling() }
    }

    LaunchedEffect(Unit) {
        val savedBaseUrl = hostPrefs.baseUrl.first()
        vm.setBaseUrl(savedBaseUrl)
        vm.refresh()
    }

    LaunchedEffect(ui.baseUrl) {
        hostPrefs.setBaseUrl(ui.baseUrl)
    }

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Bitaxe Baller", style = MaterialTheme.typography.headlineSmall)
            Text(ui.disclaimer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.baseUrl,
                onValueChange = vm::setBaseUrl,
                label = { Text("Dashboard base URL") },
                supportingText = { Text("Example: http://bitaxe-baller.local or http://192.168.1.10:5050") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::refresh) { Text("Refresh") }
                Button(onClick = vm::scanNetwork, enabled = !ui.scanning) {
                    Text(if (ui.scanning) "Scanning..." else "Scan LAN")
                }
            }

            Text("Add device", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = ipInput,
                    onValueChange = { ipInput = it },
                    label = { Text("IP") }
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    label = { Text("Label (optional)") }
                )
            }
            Button(onClick = {
                vm.addDevice(ipInput, labelInput)
                ipInput = ""
                labelInput = ""
            }) { Text("Add") }

            Spacer(Modifier.height(8.dp))
            Text("Devices", fontWeight = FontWeight.Bold)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ui.devices, key = { it.ip }) { d ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDevice(d.ip) }) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(d.label?.ifBlank { d.ip } ?: d.ip, fontWeight = FontWeight.SemiBold)
                            Text("IP: ${d.ip}")
                            Text("Status: ${if (d.online) "Online" else "Offline"}")
                            Text("Hashrate: ${"%.2f".format(d.hashrate ?: 0.0)} GH/s")
                            Text(
                                "ASIC: ${"%.2f".format(d.asicTemp ?: 0.0)}°C · " +
                                    "VR: ${"%.2f".format(d.vrTemp ?: 0.0)}°C"
                            )
                            Text("Severity: ${d.severity ?: "none"}")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onOpenDevice(d.ip) }) { Text("Detail") }
                                Button(onClick = { vm.removeDevice(d.ip) }) { Text("Remove") }
                            }
                        }
                    }
                }
            }
        }
    }
}
