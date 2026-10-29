package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextPrimary
import com.example.ui.theme.PedalTextSecondary

@Composable
fun EffectsListScreen(
    preset: PresetData,
    onToggleModule: (Int) -> Unit,
    onSelectAndOpenModule: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("effects_list_screen")
    ) {
        Text(
            text = "Daftar Efek & Status Modul",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PedalTextPrimary,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(PEDAL_MODULES) { index, modDef ->
                val modState = preset.modules[index]
                val currentModelName = modDef.models.getOrNull(modState.modelIndex) ?: modDef.models.first()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("effect_item_${modDef.id}")
                        .clickable { onSelectAndOpenModule(index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PedalCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (modState.isOn) modDef.color else PedalBorder)
                            )

                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = modDef.id,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = modDef.color
                                    )
                                    Text(
                                        text = "· $currentModelName",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = PedalTextPrimary
                                    )
                                }

                                val activeParams = modDef.getParametersForModel(modState.modelIndex)
                                Text(
                                    text = "${modDef.models.size} model · ${activeParams.joinToString(", ")}",
                                    fontSize = 11.sp,
                                    color = PedalTextMuted,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        // Toggle switch button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (modState.isOn) PedalCardElevated else Color(0xFF1B1D22))
                                .border(
                                    width = 1.dp,
                                    color = if (modState.isOn) modDef.color.copy(alpha = 0.6f) else PedalBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onToggleModule(index) }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                                .testTag("toggle_module_${modDef.id}")
                        ) {
                            Text(
                                text = if (modState.isOn) "ON" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (modState.isOn) PedalOrange else PedalTextMuted
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
