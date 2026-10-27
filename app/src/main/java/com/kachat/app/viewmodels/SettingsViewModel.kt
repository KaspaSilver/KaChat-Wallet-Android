package com.kachat.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kachat.app.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.net.Uri
import android.util.Log
import com.kachat.app.util.UserFacingError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 * Reads/writes app settings via AppSettingsRepository.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: AppSettingsRepository,
    pushState: com.kachat.app.services.PushState,
    private val diagnosticsExportService: com.kachat.app.services.DiagnosticsExportService,
) : ViewModel() {

    /** Read-only push diagnostics for Settings > Notifications (see PushState.PushDiagnostics). */
    val pushActive = pushState.pushActive
    val pushDiagnostics = pushState.diagnostics

    val network = settings.network
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettingsRepository.DEFAULT_NETWORK)

    val indexerUrl = settings.indexerUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettingsRepository.DEFAULT_INDEXER_URL)

    val knsApiUrl = settings.knsApiUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettingsRepository.DEFAULT_KNS_API_URL)

    val kaspaRestUrl = settings.kaspaRestUrl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettingsRepository.DEFAULT_KASPA_REST_URL)

    val notificationsEnabled = settings.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val notificationSoundEnabled = settings.notificationSoundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val notificationVibrationEnabled = settings.notificationVibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val showFeeEstimate = settings.showFeeEstimate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun saveIndexerUrl(value: String) = viewModelScope.launch { settings.setIndexerUrl(value) }
    fun saveKaspaRestUrl(value: String) = viewModelScope.launch { settings.setKaspaRestUrl(value) }
    fun setNotificationsEnabled(value: Boolean) = viewModelScope.launch { settings.setNotificationsEnabled(value) }
    fun setNotificationSoundEnabled(value: Boolean) = viewModelScope.launch { settings.setNotificationSoundEnabled(value) }
    fun setNotificationVibrationEnabled(value: Boolean) = viewModelScope.launch { settings.setNotificationVibrationEnabled(value) }
    fun setShowFeeEstimate(value: Boolean) = viewModelScope.launch { settings.setShowFeeEstimate(value) }

    // ------------------------------------------------------------------
    // Notifications hub subpages (Settings > Notifications)
    // ------------------------------------------------------------------

    val addressActivityNotificationsEnabled = settings.addressActivityNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    /** Receive notifications for the wallet's own spending addresses - a cold-storage account
     *  has its own switch in its menu (iOS ba352a2). */
    val spendingReceiveNotifications = settings.spendingReceiveNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setSpendingReceiveNotifications(enabled: Boolean) {
        viewModelScope.launch { settings.setSpendingReceiveNotifications(enabled) }
    }


    fun setAddressActivityNotificationsEnabled(value: Boolean) = viewModelScope.launch { settings.setAddressActivityNotificationsEnabled(value) }

    // ------------------------------------------------------------------
    // Kaspa Explorer (Settings > Connection > Kaspa Explorer, also opened from the account-less
    // App Settings) - was ChatViewModel's (iOS wallet 301ebfb).
    // ------------------------------------------------------------------

    val kaspaExplorer: StateFlow<com.kachat.app.models.KaspaExplorer> = settings.kaspaExplorer
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), com.kachat.app.models.KaspaExplorer.default)

    fun updateKaspaExplorer(explorer: com.kachat.app.models.KaspaExplorer) {
        viewModelScope.launch { settings.setKaspaExplorer(explorer) }
    }

    // ------------------------------------------------------------------
    // Diagnostics export (Settings > Diagnostics) - was ChatViewModel's (iOS wallet 301ebfb).
    // ------------------------------------------------------------------

    enum class ExportStatus { IDLE, IN_PROGRESS, SUCCESS, FAILED }
    data class ExportState(val status: ExportStatus = ExportStatus.IDLE, val message: String? = null)

    private val _diagnosticsExportState = MutableStateFlow(ExportState())
    val diagnosticsExportState: StateFlow<ExportState> = _diagnosticsExportState.asStateFlow()

    /** Builds the diagnostics zip, then hands its content:// URI to [onReady] - see DiagnosticsExportService. */
    fun exportDiagnostics(onReady: (Uri) -> Unit) {
        if (_diagnosticsExportState.value.status == ExportStatus.IN_PROGRESS) return
        viewModelScope.launch {
            _diagnosticsExportState.value = ExportState(status = ExportStatus.IN_PROGRESS)
            try {
                // Zip assembly is file I/O - keep it off Main.
                val uri = withContext(Dispatchers.IO) { diagnosticsExportService.exportDiagnostics() }
                _diagnosticsExportState.value = ExportState(status = ExportStatus.SUCCESS)
                onReady(uri)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Diagnostics export failed", e)
                _diagnosticsExportState.value = ExportState(status = ExportStatus.FAILED, message = UserFacingError.message(e, "Export failed"))
            }
        }
    }
}
