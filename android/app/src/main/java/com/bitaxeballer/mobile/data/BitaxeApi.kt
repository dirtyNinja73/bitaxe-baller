package com.bitaxeballer.mobile.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BitaxeApi {
    @GET("/api/devices")
    suspend fun listDevices(): List<DeviceSummary>

    @GET("/api/device/{ip}")
    suspend fun getDevice(@Path("ip") ip: String): DeviceDetail

    @POST("/api/devices/add")
    suspend fun addDevice(@Body body: AddDeviceRequest)

    @POST("/api/devices/remove")
    suspend fun removeDevice(@Body body: RemoveDeviceRequest)

    @POST("/api/scan")
    suspend fun scanNetwork(): ScanResponse
}
