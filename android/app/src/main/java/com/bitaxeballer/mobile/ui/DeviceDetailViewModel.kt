package com.bitaxeballer.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitaxeballer.mobile.data.DeviceDetail
import com.bitaxeballer.mobile.data.DeviceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DeviceDetailUiState(
    val detail: DeviceDetail? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val disclaimer: String = "Overclocking can permanently damage your Bitaxe. Tune at your own risk."
)

class DeviceDetailViewModel(
    private val repository: DeviceRepository,
    private val ip: String
) : ViewModel() {
    private val _ui = MutableStateFlow(DeviceDetailUiState())
    val ui: StateFlow<DeviceDetailUiState> = _ui.asStateFlow()

    private var pollJob: Job? = null
    private var pollDelayMs: Long = 5_000

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

    private suspend fun refreshInternal(): Boolean {
        _ui.value = _ui.value.copy(loading = true, error = null)
        return runCatching { repository.getDevice(repository.getBaseUrl(), ip) }
            .onSuccess { device ->
                _ui.value = _ui.value.copy(detail = device, loading = false)
                pollDelayMs = 5_000
            }
            .onFailure { err ->
                _ui.value = _ui.value.copy(loading = false, error = err.message ?: "Failed")
            }
            .isSuccess
    }
}
