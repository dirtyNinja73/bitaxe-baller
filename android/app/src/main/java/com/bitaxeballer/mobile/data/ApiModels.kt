package com.bitaxeballer.mobile.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class DeviceSummary(
    val ip: String,
    val label: String? = null,
    val online: Boolean = false,
    val severity: String? = null,
    val hashrate: Double? = null,
    val asicTemp: Double? = null,
    val vrTemp: Double? = null,
    val recommendations: List<Recommendation> = emptyList()
)

@Serializable
data class Recommendation(
    val id: String? = null,
    val severity: String? = null,
    val title: String? = null,
    val body: String? = null
)

@Serializable
data class DeviceDetail(
    val ip: String,
    val label: String? = null,
    val online: Boolean = false,
    val severity: String? = null,
    val hashrate: Double? = null,
    val asicTemp: Double? = null,
    val vrTemp: Double? = null,
    val efficiency: Double? = null,
    val frequency: Double? = null,
    val coreVoltage: Double? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val metrics: JsonElement? = null
)

@Serializable
data class AddDeviceRequest(val ip: String, val label: String? = null)

@Serializable
data class RemoveDeviceRequest(val ip: String)

@Serializable
data class ScanResponse(
    val found: List<String> = emptyList(),
    val scanned: Int = 0,
    val subnet: String? = null,
    val host: String? = null,
    val skipped_existing: Int = 0
)
