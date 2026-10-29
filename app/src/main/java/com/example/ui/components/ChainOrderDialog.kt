package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun ChainOrderDialog(
    preset: PresetData,
    onMoveModule: (fromIndex: Int, toIndex: Int) -> Unit,
    onResetOrder: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("chain_order_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PedalCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Urutan Rantai Sinyal Efek",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PedalTextPrimary
                        )
                        Text(
                            text = "Alur sinyal: IN (Gitar) ➔ OUT (Output)",
                            fontSize = 11.sp,
                            color = PedalOrange
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_chain_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = PedalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(preset.chainOrder) { chainPos, moduleIndex ->
                        val modDef = PEDAL_MODULES[moduleIndex]
                        val modState = preset.modules[moduleIndex]
                        val modelName = modDef.models.getOrNull(modState.modelIndex) ?: modDef.models.first()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PedalCardElevated)
                                .border(1.dp, PedalBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("chain_order_item_$chainPos"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Order number chip
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1B1D22)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${chainPos + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PedalOrange
                                    )
                                }

                                // LED dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (modState.isOn) modDef.color else PedalBorder)
                                )

                                Column {
                                    Text(
                                        text = modDef.id,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = modDef.color
                                    )
                                    Text(
                                        text = modelName,
                                        fontSize = 10.sp,
                                        color = PedalTextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Reorder Arrows
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onMoveModule(chainPos, chainPos - 1) },
                                    enabled = chainPos > 0,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("move_up_chain_$chainPos")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "Pindah ke atas / lebih awal",
                                        tint = if (chainPos > 0) PedalOrange else PedalTextMuted.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onMoveModule(chainPos, chainPos + 1) },
                                    enabled = chainPos < preset.chainOrder.lastIndex,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("move_down_chain_$chainPos")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Pindah ke bawah / lebih akhir",
                                        tint = if (chainPos < preset.chainOrder.lastIndex) PedalOrange else PedalTextMuted.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetOrder,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_chain_order_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PedalTextSecondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                        Text("Reset Urutan", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("done_chain_order_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PedalOrange,
                            contentColor = Color(0xFF1B1109)
                        )
                    ) {
                        Text("Selesai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
