package com.bitaxeballer.mobile.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.ConcurrentHashMap

class DeviceRepository {
    @Volatile
    private var activeBaseUrl: String = DEFAULT_BASE_URL

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client = OkHttpClient.Builder().build()
    private val apiCache = ConcurrentHashMap<String, BitaxeApi>()

    fun setBaseUrl(url: String) {
        activeBaseUrl = normalizeBaseUrl(url)
    }

    fun getBaseUrl(): String = activeBaseUrl

    private fun normalizeBaseUrl(url: String): String = url.trim().trimEnd('/')

    private fun api(baseUrl: String): BitaxeApi {
        val normalized = normalizeBaseUrl(baseUrl)
        return apiCache.computeIfAbsent(normalized) { cacheKey ->
            Retrofit.Builder()
                .baseUrl("$cacheKey/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(BitaxeApi::class.java)
        }
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
