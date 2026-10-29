package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextPrimary
import com.example.ui.theme.PedalTextSecondary

@Composable
fun RenamePresetDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("rename_preset_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PedalCardBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PedalBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Ubah Nama Preset",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PedalTextPrimary
                )
                Text(
                    text = "Maksimal 16 karakter (kompatibel pedal GP-180)",
                    fontSize = 11.sp,
                    color = PedalTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 16) name = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_preset_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PedalOrange,
                        unfocusedBorderColor = PedalBorder,
                        focusedTextColor = PedalTextPrimary,
                        unfocusedTextColor = PedalTextPrimary,
                        focusedContainerColor = PedalCardElevated,
                        unfocusedContainerColor = PedalCardElevated
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_rename_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PedalTextSecondary)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            onConfirm(name)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_rename_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PedalOrange,
                            contentColor = Color(0xFF1B1109)
                        )
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
