package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData
import com.example.ui.components.ChainBlock
import com.example.ui.components.ReorderableChainGrid
import com.example.ui.components.RotaryKnob
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextPrimary
import com.example.ui.theme.PedalTextSecondary
import java.net.URLEncoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PresetEditorScreen(
    preset: PresetData,
    instrument: String,
    bandInput: String,
    songTitleInput: String,
    musicLinkInput: String,
    isGenerating: Boolean,
    statusMessage: String,
    onInstrumentChange: (String) -> Unit,
    onBandChange: (String) -> Unit,
    onSongTitleChange: (String) -> Unit,
    onMusicLinkChange: (String) -> Unit,
    onExampleClick: (String, String) -> Unit,
    onGenerateClick: () -> Unit,
    onModuleSelect: (Int) -> Unit,
    onToggleSelectedModule: () -> Unit,
    onCycleModel: (Int) -> Unit,
    onOpenModelSelector: () -> Unit,
    onKnobChange: (paramIndex: Int, value: Float) -> Unit,
    onOpenFile: () -> Unit,
    onSaveFile: () -> Unit,
    onShareFile: () -> Unit,
    onRenameClick: () -> Unit,
    onOpenChainOrderDialog: () -> Unit,
    onShiftSelectedModule: (direction: Int) -> Unit,
    onMoveModuleInChain: (fromPos: Int, toPos: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedModule = preset.selectedModule
    val selectedState = preset.selectedModuleState

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("preset_editor_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Generator Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_generator_panel"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PedalCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cari Preset Lagu dengan AI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PedalTextPrimary
                    )

                    // Instrument Selector Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Untuk:",
                            fontSize = 11.sp,
                            color = PedalTextMuted
                        )
                        PillButton(
                            text = "Gitar",
                            isSelected = instrument == "gitar",
                            onClick = { onInstrumentChange("gitar") },
                            testTag = "instrument_guitar_button"
                        )
                        PillButton(
                            text = "Bass",
                            isSelected = instrument == "bass",
                            onClick = { onInstrumentChange("bass") },
                            testTag = "instrument_bass_button"
                        )
                    }
                }

                Text(
                    text = "Isi nama band dan judul lagu, AI akan menyusun parameter pedal GP180.",
                    fontSize = 11.sp,
                    color = PedalTextMuted
                )

                // Band & Title inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bandInput,
                        onValueChange = onBandChange,
                        placeholder = { Text("Nama band / artis", fontSize = 12.sp, color = PedalTextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("band_input"),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = songTitleInput,
                        onValueChange = onSongTitleChange,
                        placeholder = { Text("Judul lagu", fontSize = 12.sp, color = PedalTextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("title_input"),
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Optional music link input
                OutlinedTextField(
                    value = musicLinkInput,
                    onValueChange = onMusicLinkChange,
                    placeholder = { Text("Tautan YouTube / Spotify / SoundCloud (opsional)", fontSize = 12.sp, color = PedalTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("music_link_input"),
                    singleLine = true,
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick Examples
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExampleChip("Metallica", "Master of Puppets", onExampleClick)
                    ExampleChip("Pink Floyd", "Comfortably Numb", onExampleClick)
                    ExampleChip("RHCP", "Under the Bridge", onExampleClick)
                    ExampleChip("Queen", "Another One Bites the Dust", onExampleClick)
                }

                // Quick Search Shortcuts
                val searchQuery = "${bandInput.trim()} ${songTitleInput.trim()}".trim()
                if (searchQuery.isNotEmpty()) {
                    val encoded = URLEncoder.encode(searchQuery, "UTF-8")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cari di:", fontSize = 11.sp, color = PedalTextMuted)
                        SearchLinkChip("YouTube") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded")))
                        }
                        SearchLinkChip("Spotify") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/$encoded")))
                        }
                        SearchLinkChip("SoundCloud") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://soundcloud.com/search?q=$encoded")))
                        }
                    }
                }

                // Generate Button
                Button(
                    onClick = onGenerateClick,
                    enabled = !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_preset_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PedalOrange,
                        contentColor = Color(0xFF1B1109)
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 6.dp),
                            color = Color(0xFF1B1109),
                            strokeWidth = 2.dp
                        )
                        Text("Menyusun Tone...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 6.dp)
                        )
                        Text("Generate Presets", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Signal Chain Section
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rantai Sinyal (12 Modul)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PedalTextSecondary
                    )
                    Text(
                        text = "IN ➔ OUT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PedalOrange
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PedalCardElevated)
                        .border(1.dp, PedalBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenChainOrderDialog)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("open_chain_order_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = PedalOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Ubah Urutan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PedalOrange
                        )
                    }
                }
            }

            // Touch Drag-and-Drop Reorderable Signal Chain Grid
            ReorderableChainGrid(
                preset = preset,
                onModuleSelect = onModuleSelect,
                onMoveModule = onMoveModuleInChain
            )

            Text(
                text = "💡 Tekan lama & geser modul efek untuk memindahkan urutannya langsung lewat layar sentuh",
                fontSize = 10.sp,
                color = PedalTextMuted,
                modifier = Modifier.padding(start = 2.dp)
            )
        }

        // Active Module Parameter Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_module_panel"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PedalCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header of module panel with reorder shift buttons & toggle
                val chainPos = preset.selectedModuleChainPosition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (selectedState.isOn) selectedModule.color else PedalBorder)
                        )
                        Text(
                            text = "${selectedModule.id} · ${selectedModule.name}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedModule.color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Shift Earlier (Left) in Chain
                        IconButton(
                            onClick = { onShiftSelectedModule(-1) },
                            enabled = chainPos > 0,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PedalCardElevated)
                                .testTag("shift_left_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geser ke depan di rantai",
                                tint = if (chainPos > 0) PedalOrange else PedalTextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Current chain position indicator
                        Text(
                            text = "#${chainPos + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PedalOrange
                        )

                        // Quick Shift Later (Right) in Chain
                        IconButton(
                            onClick = { onShiftSelectedModule(1) },
                            enabled = chainPos < preset.chainOrder.lastIndex,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PedalCardElevated)
                                .testTag("shift_right_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Geser ke belakang di rantai",
                                tint = if (chainPos < preset.chainOrder.lastIndex) PedalOrange else PedalTextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // On/Off Switch Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selectedState.isOn) PedalCardElevated else Color(0xFF1B1D22))
                                .border(1.dp, if (selectedState.isOn) selectedModule.color.copy(alpha = 0.6f) else PedalBorder, RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleSelectedModule)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .testTag("toggle_module_button")
                        ) {
                            Text(
                                text = if (selectedState.isOn) "ON" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedState.isOn) PedalOrange else PedalTextMuted
                            )
                        }
                    }
                }

                // Model Selector Carousel
                val currentModelName = selectedModule.models.getOrNull(selectedState.modelIndex) ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PedalCardElevated)
                        .border(1.dp, PedalBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onCycleModel(-1) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("previous_model_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Model Sebelumnya",
                            tint = PedalOrange
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenModelSelector)
                            .padding(vertical = 4.dp)
                            .testTag("select_model_dropdown_button"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$currentModelName  ▾",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PedalTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onCycleModel(1) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("next_model_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Model Selanjutnya",
                            tint = PedalOrange
                        )
                    }
                }

                // Model Description Badge (if available, e.g. Gate 1 vs Gate 2)
                val modelDesc = selectedModule.getDescriptionForModel(selectedState.modelIndex)
                if (modelDesc != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PedalCardElevated.copy(alpha = 0.7f))
                            .border(1.dp, selectedModule.color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = modelDesc,
                            fontSize = 11.sp,
                            color = PedalTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Rotary Knobs Grid
                val currentParameters = selectedModule.getParametersForModel(selectedState.modelIndex)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentParameters.forEachIndexed { pIdx, paramName ->
                        val value = selectedState.values.getOrNull(pIdx) ?: 50f
                        RotaryKnob(
                            label = paramName,
                            value = value,
                            onValueChange = { newVal ->
                                onKnobChange(pIdx, newVal)
                            },
                            accentColor = if (selectedState.isOn) selectedModule.color else PedalBorder
                        )
                    }
                }
            }
        }

        // Action Buttons: Open, Save, Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenFile,
                modifier = Modifier
                    .weight(1f)
                    .testTag("open_file_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = PedalCardElevated,
                    contentColor = PedalTextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileOpen,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text("Buka .prst", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onSaveFile,
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("save_file_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PedalOrange,
                    contentColor = Color(0xFF1B1109)
                ),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
                Text("Simpan (.prst)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = onShareFile,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PedalCardElevated)
                    .border(1.dp, PedalBorder, RoundedCornerShape(10.dp))
                    .size(44.dp)
                    .testTag("share_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Bagikan preset",
                    tint = PedalOrange
                )
            }
        }

        // Status Message Bar
        Text(
            text = statusMessage,
            fontSize = 12.sp,
            color = PedalTextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .testTag("status_message_text")
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun PillButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) PedalOrange else PedalCardElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color(0xFF1B1109) else PedalTextPrimary
        )
    }
}

@Composable
private fun ExampleChip(
    band: String,
    title: String,
    onClick: (String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PedalCardElevated)
            .border(1.dp, PedalBorder, RoundedCornerShape(14.dp))
            .clickable { onClick(band, title) }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("example_chip_${band.replace(" ", "_")}")
    ) {
        Text(
            text = "$band - $title",
            fontSize = 11.sp,
            color = PedalTextPrimary
        )
    }
}

@Composable
private fun SearchLinkChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PedalCardElevated)
            .border(1.dp, PedalBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = PedalOrange
        )
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PedalOrange,
    unfocusedBorderColor = PedalBorder,
    focusedTextColor = PedalTextPrimary,
    unfocusedTextColor = PedalTextPrimary,
    focusedContainerColor = PedalCardElevated,
    unfocusedContainerColor = PedalCardElevated
)
