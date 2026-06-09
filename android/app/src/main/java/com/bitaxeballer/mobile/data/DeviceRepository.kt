package com.bitaxeballer.mobile.data

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

class DeviceRepository {
    @Volatile
    private var activeBaseUrl: String = "http://bitaxe-baller.local"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client = OkHttpClient.Builder().build()

    fun setBaseUrl(url: String) {
        activeBaseUrl = url.trim().trimEnd('/')
    }

    fun getBaseUrl(): String = activeBaseUrl

    private fun api(baseUrl: String): BitaxeApi {
        val normalized = if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(normalized)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BitaxeApi::class.java)
    }

    suspend fun listDevices(baseUrl: String = activeBaseUrl): List<DeviceSummary> = api(baseUrl).listDevices()

    suspend fun getDevice(baseUrl: String = activeBaseUrl, ip: String): DeviceDetail = api(baseUrl).getDevice(ip)

    suspend fun addDevice(baseUrl: String = activeBaseUrl, ip: String, label: String?) {
        api(baseUrl).addDevice(AddDeviceRequest(ip = ip, label = label?.ifBlank { null }))
    }

    suspend fun removeDevice(baseUrl: String = activeBaseUrl, ip: String) {
        api(baseUrl).removeDevice(RemoveDeviceRequest(ip = ip))
    }

    suspend fun scan(baseUrl: String = activeBaseUrl): ScanResponse = api(baseUrl).scanNetwork()
}
