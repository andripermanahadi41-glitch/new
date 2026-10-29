package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModuleDefinition
import com.example.data.model.ModuleState
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalCardBg
import com.example.ui.theme.PedalCardElevated
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted
import com.example.ui.theme.PedalTextSecondary

@Composable
fun ChainBlock(
    module: ModuleDefinition,
    state: ModuleState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    positionIndex: Int? = null
) {
    val modelName = module.models.getOrNull(state.modelIndex) ?: module.models.firstOrNull() ?: ""
    val shape = RoundedCornerShape(10.dp)
    val bgColor = if (state.isOn) PedalCardElevated else PedalCardBg
    val borderColor = if (isSelected) PedalOrange else Color.Transparent

    Box(
        modifier = modifier
            .testTag("chain_block_${module.id}")
            .clip(shape)
            .background(bgColor)
            .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) borderColor else PedalBorder.copy(alpha = 0.5f), shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .height(58.dp)
    ) {
        // Chain position index badge at top-left
        if (positionIndex != null) {
            Text(
                text = "$positionIndex",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PedalOrange else PedalTextMuted.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.TopStart).padding(start = 2.dp)
            )
        }

        // Status LED dot at top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(7.dp)
                .clip(CircleShape)
                .background(if (state.isOn) module.color else PedalBorder)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = module.id,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.isOn) module.color else PedalTextMuted,
                textAlign = TextAlign.Center
            )

            Text(
                text = modelName.take(8),
                fontSize = 9.sp,
                color = if (state.isOn) PedalTextSecondary else PedalTextMuted.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
