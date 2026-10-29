package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData
import com.example.data.presets.BuiltInPresets
import com.example.data.presets.StorePresetItem
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextPrimary
import com.example.ui.theme.PedalTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PresetStoreScreen(
    onLoadPreset: (PresetData, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Gitar", "Bass")

    val allPresets = BuiltInPresets.items
    val filteredPresets = allPresets.filter { item ->
        when (selectedFilter) {
            "Gitar" -> !item.isBass
            "Bass" -> item.isBass
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("preset_store_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pustaka Tone GP-180",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PedalTextPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PedalOrange else PedalCardElevated)
                            .border(1.dp, PedalBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("filter_$filter")
                    ) {
                        Text(
                            text = filter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color(0xFF1B1109) else PedalTextPrimary
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPresets) { item ->
                PresetStoreCard(
                    item = item,
                    onLoadClick = {
                        onLoadPreset(item.preset, "Preset '${item.name}' diterapkan.")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PresetStoreCard(
    item: StorePresetItem,
    onLoadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("store_card_${item.name.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PedalTextPrimary
                    )
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = PedalTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.isBass) Color(0xFF1D2F3A) else Color(0xFF382317))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isBass) Color(0xFF4FC3F7) else PedalOrange
                    )
                }
            }

            // Active modules chips preview
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.preset.modules.forEachIndexed { idx, modState ->
                    if (modState.isOn) {
                        val modDef = PEDAL_MODULES[idx]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PedalCardElevated)
                                .border(1.dp, modDef.color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = modDef.id,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = modDef.color
                            )
                        }
                    }
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onLoadClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PedalOrange,
                        contentColor = Color(0xFF1B1109)
                    ),
                    modifier = Modifier.testTag("load_preset_${item.name.replace(" ", "_")}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "Gunakan Preset",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
