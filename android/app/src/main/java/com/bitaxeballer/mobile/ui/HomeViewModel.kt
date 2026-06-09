package com.bitaxeballer.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bitaxeballer.mobile.data.DEFAULT_BASE_URL
import com.bitaxeballer.mobile.data.DeviceRepository
import com.bitaxeballer.mobile.data.DeviceSummary
import com.bitaxeballer.mobile.data.RISK_DISCLAIMER
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val baseUrl: String = DEFAULT_BASE_URL,
    val devices: List<DeviceSummary> = emptyList(),
    val loading: Boolean = false,
    val scanning: Boolean = false,
    val error: String? = null,
    val disclaimer: String = RISK_DISCLAIMER
)

class HomeViewModel(private val repository: DeviceRepository) : ViewModel() {
    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui.asStateFlow()

    private var pollJob: Job? = null
    private var pollDelayMs: Long = 5_000

    fun setBaseUrl(url: String) {
        val normalized = url.trim().trimEnd('/')
        repository.setBaseUrl(normalized)
        _ui.value = _ui.value.copy(baseUrl = normalized)
    }

    fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            while (true) {
                val success = refreshInternal()
                pollDelayMs = if (success) 5_000 else (pollDelayMs * 2).coerceAtMost(30_000)
                delay(pollDelayMs)
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }

    fun refresh() {
        viewModelScope.launch { refreshInternal() }
    }

    suspend fun refreshNow(): Boolean = refreshInternal()

    private suspend fun refreshInternal(): Boolean {
        _ui.value = _ui.value.copy(loading = true, error = null)
        return runCatching { repository.listDevices(_ui.value.baseUrl) }
            .onSuccess { devices ->
                _ui.value = _ui.value.copy(devices = devices, loading = false)
                pollDelayMs = 5_000
            }
            .onFailure { err ->
                _ui.value = _ui.value.copy(loading = false, error = err.message ?: "Failed")
            }
            .isSuccess
    }

    fun addDevice(ip: String, label: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.addDevice(_ui.value.baseUrl, ip.trim(), label)
                refreshInternal()
                onSuccess()
            } catch (err: Exception) {
                _ui.value = _ui.value.copy(error = err.message ?: "Add failed")
            }
        }
    }

    fun removeDevice(ip: String) {
        viewModelScope.launch {
            try {
                repository.removeDevice(_ui.value.baseUrl, ip)
                refreshInternal()
            } catch (err: Exception) {
                _ui.value = _ui.value.copy(error = err.message ?: "Remove failed")
            }
        }
    }

    fun scanNetwork() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(scanning = true, error = null)
            try {
                repository.scan(_ui.value.baseUrl)
                refreshInternal()
                _ui.value = _ui.value.copy(scanning = false)
            } catch (err: Exception) {
                _ui.value = _ui.value.copy(scanning = false, error = err.message ?: "Scan failed")
            }
        }
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }

companion object {
    fun provideFactory(repository: DeviceRepository): ViewModelProvider.Factory =
    viewModelFactory {
            initializer {
                HomeViewModel(repository)
            }
    }
}
