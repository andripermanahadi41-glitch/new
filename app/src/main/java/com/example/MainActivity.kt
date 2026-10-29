package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.MainViewModel
import com.example.ui.components.AiConfigDialog
import com.example.ui.components.ChainOrderDialog
import com.example.ui.components.ModelSelectorDialog
import com.example.ui.components.RenamePresetDialog
import com.example.ui.screens.EffectsListScreen
import com.example.ui.screens.PresetEditorScreen
import com.example.ui.screens.PresetStoreScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalDarkBg
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextPrimary
import com.example.ui.theme.PedalTextSecondary
import com.example.ui.theme.StatusGreen
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val presetData by viewModel.presetData.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val instrument by viewModel.instrument.collectAsState()
    val bandInput by viewModel.bandInput.collectAsState()
    val songTitleInput by viewModel.songTitleInput.collectAsState()
    val musicLinkInput by viewModel.musicLinkInput.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val bluetoothConnected by viewModel.bluetoothConnected.collectAsState()
    val usbConnected by viewModel.usbConnected.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()
    val showAiConfigDialog by viewModel.showAiConfigDialog.collectAsState()
    val showModelSelectorDialog by viewModel.showModelSelectorDialog.collectAsState()
    val showRenameDialog by viewModel.showRenameDialog.collectAsState()
    val showChainOrderDialog by viewModel.showChainOrderDialog.collectAsState()

    // Handle back button when on sub-tabs
    BackHandler(enabled = currentTab != 0) {
        viewModel.selectTab(0)
    }

    // File Open Launcher
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    viewModel.importPresetBytes(bytes)
                    Toast.makeText(context, "Preset berhasil dibuka", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // File Save Launcher
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val buildResult = viewModel.exportPresetBytes()
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(buildResult.bytes)
                    Toast.makeText(context, "${buildResult.filename} disimpan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menyimpan file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PedalDarkBg),
        containerColor = PedalDarkBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PedalDarkBg)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PedalCardBg)
                            .clickable { viewModel.selectTab(0) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = PedalOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Preset Name with rename click
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.openRenameDialog(true) }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .testTag("preset_header_name"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = presetData.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PedalTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah nama",
                            tint = PedalTextMuted,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(start = 4.dp)
                        )
                    }

                    // Bluetooth Chip
                    StatusChip(
                        label = if (bluetoothConnected) "● Bluetooth" else "○ Bluetooth",
                        isConnected = bluetoothConnected,
                        onClick = { viewModel.toggleBluetooth() },
                        testTag = "bluetooth_status_chip"
                    )

                    // USB Chip
                    StatusChip(
                        label = if (usbConnected) "● USB" else "○ USB",
                        isConnected = usbConnected,
                        onClick = { viewModel.toggleUsb() },
                        testTag = "usb_status_chip"
                    )

                    // Settings Cog Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PedalCardBg)
                            .clickable { viewModel.openAiConfigDialog(true) }
                            .testTag("open_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan AI",
                            tint = PedalTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PedalCardBg)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(width = 1.dp, color = PedalBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavTab(
                        icon = Icons.Default.GridOn,
                        label = "Preset",
                        isSelected = currentTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        testTag = "nav_tab_preset"
                    )
                    BottomNavTab(
                        icon = Icons.Default.Tune,
                        label = "Effects",
                        isSelected = currentTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        testTag = "nav_tab_effects"
                    )
                    BottomNavTab(
                        icon = Icons.Default.ShoppingCart,
                        label = "Store",
                        isSelected = currentTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        testTag = "nav_tab_store"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> PresetEditorScreen(
                    preset = presetData,
                    instrument = instrument,
                    bandInput = bandInput,
                    songTitleInput = songTitleInput,
                    musicLinkInput = musicLinkInput,
                    isGenerating = isGenerating,
                    statusMessage = statusMessage,
                    onInstrumentChange = { viewModel.setInstrument(it) },
                    onBandChange = { viewModel.updateBand(it) },
                    onSongTitleChange = { viewModel.updateSongTitle(it) },
                    onMusicLinkChange = { viewModel.updateMusicLink(it) },
                    onExampleClick = { band, title -> viewModel.setExample(band, title) },
                    onGenerateClick = { viewModel.generatePreset() },
                    onModuleSelect = { viewModel.selectModule(it) },
                    onToggleSelectedModule = { viewModel.toggleSelectedModule() },
                    onCycleModel = { viewModel.cycleModel(it) },
                    onOpenModelSelector = { viewModel.openModelSelectorDialog(true) },
                    onKnobChange = { pIdx, v -> viewModel.setKnobValue(presetData.selectedModuleIndex, pIdx, v) },
                    onOpenFile = {
                        openDocumentLauncher.launch(arrayOf("*/*"))
                    },
                    onSaveFile = {
                        val buildResult = viewModel.exportPresetBytes()
                        createDocumentLauncher.launch(buildResult.filename)
                    },
                    onShareFile = {
                        try {
                            val buildResult = viewModel.exportPresetBytes()
                            val presetsDir = File(context.cacheDir, "presets").apply { mkdirs() }
                            val tempFile = File(presetsDir, buildResult.filename)
                            FileOutputStream(tempFile).use { it.write(buildResult.bytes) }

                            val contentUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempFile
                            )
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/octet-stream"
                                putExtra(Intent.EXTRA_STREAM, contentUri)
                                putExtra(Intent.EXTRA_SUBJECT, "Preset GP-180: ${presetData.name}")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Preset GP-180"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal membagikan preset: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onRenameClick = { viewModel.openRenameDialog(true) },
                    onOpenChainOrderDialog = { viewModel.openChainOrderDialog(true) },
                    onShiftSelectedModule = { direction -> viewModel.shiftSelectedModule(direction) },
                    onMoveModuleInChain = { fromPos, toPos -> viewModel.moveModuleInChain(fromPos, toPos) }
                )
                1 -> EffectsListScreen(
                    preset = presetData,
                    onToggleModule = { viewModel.toggleModule(it) },
                    onSelectAndOpenModule = {
                        viewModel.selectModule(it)
                        viewModel.selectTab(0)
                    }
                )
                2 -> PresetStoreScreen(
                    onLoadPreset = { newPreset, msg ->
                        viewModel.loadPreset(newPreset, msg)
                        viewModel.selectTab(0)
                    }
                )
            }
        }

        // Dialogs
        if (showChainOrderDialog) {
            ChainOrderDialog(
                preset = presetData,
                onMoveModule = { fromIdx, toIdx ->
                    viewModel.moveModuleInChain(fromIdx, toIdx)
                },
                onResetOrder = {
                    viewModel.resetChainOrder()
                },
                onDismiss = { viewModel.openChainOrderDialog(false) }
            )
        }

        if (showAiConfigDialog) {
            AiConfigDialog(
                initialApiKey = customApiKey,
                initialModel = selectedAiModel,
                onSave = { key, model ->
                    viewModel.saveAiConfig(key, model)
                },
                onUseBuiltIn = {
                    viewModel.saveAiConfig("", selectedAiModel)
                },
                onDismiss = { viewModel.openAiConfigDialog(false) }
            )
        }

        if (showModelSelectorDialog) {
            val selectedMod = presetData.selectedModule
            val selectedState = presetData.selectedModuleState
            ModelSelectorDialog(
                module = selectedMod,
                selectedModelIndex = selectedState.modelIndex,
                onModelSelected = { modelIdx ->
                    viewModel.setModel(presetData.selectedModuleIndex, modelIdx)
                },
                onDismiss = { viewModel.openModelSelectorDialog(false) }
            )
        }

        if (showRenameDialog) {
            RenamePresetDialog(
                currentName = presetData.name,
                onConfirm = { newName ->
                    viewModel.renamePreset(newName)
                },
                onDismiss = { viewModel.openRenameDialog(false) }
            )
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    isConnected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PedalCardBg)
            .border(1.dp, if (isConnected) StatusGreen.copy(alpha = 0.6f) else PedalBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isConnected) StatusGreen else PedalTextMuted
        )
    }
}

@Composable
private fun BottomNavTab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) PedalOrange else PedalTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PedalOrange else PedalTextMuted
        )
    }
}
