package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiToneService
import com.example.data.format.GP180PresetFormat
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("gp180_prefs", Context.MODE_PRIVATE)
    private val aiService = AiToneService()

    private val _presetData = MutableStateFlow(PresetData())
    val presetData: StateFlow<PresetData> = _presetData.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0: Preset, 1: Effects, 2: Store
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _instrument = MutableStateFlow("gitar") // "gitar" or "bass"
    val instrument: StateFlow<String> = _instrument.asStateFlow()

    private val _bandInput = MutableStateFlow("")
    val bandInput: StateFlow<String> = _bandInput.asStateFlow()

    private val _songTitleInput = MutableStateFlow("")
    val songTitleInput: StateFlow<String> = _songTitleInput.asStateFlow()

    private val _musicLinkInput = MutableStateFlow("")
    val musicLinkInput: StateFlow<String> = _musicLinkInput.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _statusMessage = MutableStateFlow("Valeton GP-180 siap.")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _bluetoothConnected = MutableStateFlow(false)
    val bluetoothConnected: StateFlow<Boolean> = _bluetoothConnected.asStateFlow()

    private val _usbConnected = MutableStateFlow(false)
    val usbConnected: StateFlow<Boolean> = _usbConnected.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("gemini_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedAiModel = MutableStateFlow(prefs.getString("gemini_model", "gemini-3.8-flash") ?: "gemini-3.8-flash")
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    private val _showAiConfigDialog = MutableStateFlow(false)
    val showAiConfigDialog: StateFlow<Boolean> = _showAiConfigDialog.asStateFlow()

    private val _showModelSelectorDialog = MutableStateFlow(false)
    val showModelSelectorDialog: StateFlow<Boolean> = _showModelSelectorDialog.asStateFlow()

    private val _showRenameDialog = MutableStateFlow(false)
    val showRenameDialog: StateFlow<Boolean> = _showRenameDialog.asStateFlow()

    private val _showChainOrderDialog = MutableStateFlow(false)
    val showChainOrderDialog: StateFlow<Boolean> = _showChainOrderDialog.asStateFlow()

    private var customBinaryTemplate: ByteArray? = null

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun setInstrument(inst: String) {
        _instrument.value = inst
    }

    fun updateBand(band: String) {
        _bandInput.value = band
    }

    fun updateSongTitle(title: String) {
        _songTitleInput.value = title
    }

    fun updateMusicLink(link: String) {
        _musicLinkInput.value = link
    }

    fun setExample(band: String, title: String) {
        _bandInput.value = band
        _songTitleInput.value = title
    }

    fun selectModule(index: Int) {
        _presetData.value = _presetData.value.copy(selectedModuleIndex = index)
    }

    fun toggleModule(index: Int) {
        _presetData.value = _presetData.value.updateModuleState(index) {
            it.copy(isOn = !it.isOn)
        }
    }

    fun toggleSelectedModule() {
        val current = _presetData.value
        toggleModule(current.selectedModuleIndex)
    }

    fun cycleModel(direction: Int) {
        val current = _presetData.value
        val modDef = current.selectedModule
        val modState = current.selectedModuleState
        val totalModels = modDef.models.size
        val newIndex = (modState.modelIndex + direction + totalModels) % totalModels
        setModel(current.selectedModuleIndex, newIndex)
    }

    fun setModel(moduleIndex: Int, modelIndex: Int) {
        _presetData.value = _presetData.value.updateModuleState(moduleIndex) {
            it.copy(modelIndex = modelIndex)
        }
    }

    fun setKnobValue(moduleIndex: Int, paramIndex: Int, value: Float) {
        _presetData.value = _presetData.value.updateModuleState(moduleIndex) { currentMod ->
            val newVals = currentMod.values.toMutableList()
            if (paramIndex in newVals.indices) {
                newVals[paramIndex] = value.coerceIn(0f, 100f)
            }
            currentMod.copy(values = newVals)
        }
    }

    fun renamePreset(newName: String) {
        val clean = newName.trim().take(16).ifBlank { "Preset" }
        _presetData.value = _presetData.value.copy(name = clean)
    }

    fun loadPreset(newPreset: PresetData, message: String = "Preset dimuat.") {
        _presetData.value = newPreset
        _statusMessage.value = message
    }

    fun generatePreset() {
        val band = _bandInput.value.trim()
        val title = _songTitleInput.value.trim()
        val link = _musicLinkInput.value.trim()

        if (band.isBlank() && title.isBlank() && link.isBlank()) {
            _statusMessage.value = "Silakan isi nama band dan judul lagu terlebih dahulu."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = "Menghubungi AI Tone Builder..."

            val result = aiService.generatePreset(
                band = band,
                title = title,
                instrument = _instrument.value,
                customApiKey = _customApiKey.value,
                selectedModel = _selectedAiModel.value
            )

            _isGenerating.value = false
            result.onSuccess { toneResult ->
                _presetData.value = toneResult.preset
                _statusMessage.value = toneResult.message
            }.onFailure { err ->
                _statusMessage.value = "Gagal membuat preset: ${err.localizedMessage}"
            }
        }
    }

    fun exportPresetBytes(): GP180PresetFormat.BuildResult {
        return GP180PresetFormat.build(_presetData.value, customBinaryTemplate)
    }

    fun importPresetBytes(bytes: ByteArray) {
        try {
            val (parsed, raw) = GP180PresetFormat.parse(bytes, _presetData.value)
            _presetData.value = parsed
            customBinaryTemplate = raw
            _statusMessage.value = "Preset asli '${parsed.name}' berhasil dibuka."
        } catch (e: Exception) {
            _statusMessage.value = "Gagal membaca file: ${e.message ?: "Format tidak valid"}"
        }
    }

    fun toggleBluetooth() {
        val next = !_bluetoothConnected.value
        _bluetoothConnected.value = next
        _statusMessage.value = if (next) {
            "Terhubung ke GP-180 via Bluetooth BLE MIDI."
        } else {
            "Bluetooth terputus."
        }
    }

    fun toggleUsb() {
        val next = !_usbConnected.value
        _usbConnected.value = next
        _statusMessage.value = if (next) {
            "Terhubung ke GP-180 via USB OTG MIDI."
        } else {
            "Kabel USB terputus."
        }
    }

    fun openAiConfigDialog(show: Boolean) {
        _showAiConfigDialog.value = show
    }

    fun openModelSelectorDialog(show: Boolean) {
        _showModelSelectorDialog.value = show
    }

    fun openRenameDialog(show: Boolean) {
        _showRenameDialog.value = show
    }

    fun openChainOrderDialog(show: Boolean) {
        _showChainOrderDialog.value = show
    }

    fun moveModuleInChain(fromIndex: Int, toIndex: Int) {
        _presetData.value = _presetData.value.moveModuleInChain(fromIndex, toIndex)
        _statusMessage.value = "Urutan rantai sinyal diperbarui."
    }

    fun shiftSelectedModule(direction: Int) {
        val currentMod = _presetData.value.selectedModule.id
        _presetData.value = _presetData.value.shiftSelectedModule(direction)
        val newPos = _presetData.value.selectedModuleChainPosition + 1
        _statusMessage.value = "Modul $currentMod digeser ke posisi $newPos dari 12."
    }

    fun resetChainOrder() {
        _presetData.value = _presetData.value.resetChainOrder()
        _statusMessage.value = "Urutan rantai sinyal dikembalikan ke standar."
    }

    fun saveAiConfig(apiKey: String, model: String) {
        _customApiKey.value = apiKey
        _selectedAiModel.value = model
        prefs.edit()
            .putString("gemini_api_key", apiKey)
            .putString("gemini_model", model)
            .apply()
        _statusMessage.value = if (apiKey.isNotBlank()) "Gemini API ($model) terkonfigurasi." else "Mode AI bawaan aktif."
    }
}
