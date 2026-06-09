package com.bitaxeballer.mobile.data

import kotlinx.serialization.Serializable

@Serializable
data class DeviceMetrics(
    val hashRate: Double? = null,
    val temp: Double? = null,
    val vrTemp: Double? = null,
    val frequency: Double? = null,
    val coreVoltage: Double? = null
)

@Serializable
data class DeviceEfficiency(
    val jPerTh: Double? = null
)

@Serializable
data class DeviceSummary(
    val ip: String,
    val label: String? = null,
    val online: Boolean = false,
    val severity: String? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val metrics: DeviceMetrics? = null
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
    val recommendations: List<Recommendation> = emptyList(),
    val metrics: DeviceMetrics? = null,
    val efficiency: DeviceEfficiency? = null
)

@Serializable
data class AddDeviceRequest(val ip: String, val label: String? = null)

@Serializable
data class RemoveDeviceRequest(val ip: String)

@Serializable
data class ScannedDevice(
    val ip: String,
    val hostname: String? = null,
    val model: String? = null,
    val version: String? = null,
    val hashRate: Double? = null
)

@Serializable
data class ScanResponse(
    val found: List<ScannedDevice> = emptyList(),
    val scanned: Int = 0,
    val subnet: String? = null,
    val host: String? = null,
    val skipped_existing: Int = 0
)
