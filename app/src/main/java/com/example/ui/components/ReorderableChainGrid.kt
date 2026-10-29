package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.PEDAL_MODULES
import com.example.data.model.PresetData
import com.example.ui.theme.PedalBorder
import com.example.ui.theme.PedalOrange
import com.example.ui.theme.PedalTextMuted

@Composable
fun ReorderableChainGrid(
    preset: PresetData,
    onModuleSelect: (Int) -> Unit,
    onMoveModule: (fromPos: Int, toPos: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var draggingChainPos by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartPos by remember { mutableStateOf(Offset.Zero) }

    // Store position bounds of each of the 12 cells in parent coordinates
    val cellBounds = remember { mutableStateMapOf<Int, Rect>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reorderable_chain_grid")
            .pointerInput(preset.chainOrder) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { startOffset ->
                        val hit = cellBounds.entries.firstOrNull { it.value.contains(startOffset) }
                        if (hit != null) {
                            draggingChainPos = hit.key
                            dragStartPos = startOffset
                            dragOffset = Offset.Zero
                            val modIndex = preset.chainOrder[hit.key]
                            onModuleSelect(modIndex)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        val currentPointer = dragStartPos + dragOffset

                        val targetHit = cellBounds.entries.firstOrNull { it.value.contains(currentPointer) }
                        val currentDragging = draggingChainPos

                        if (targetHit != null && currentDragging != null && targetHit.key != currentDragging) {
                            val targetPos = targetHit.key
                            onMoveModule(currentDragging, targetPos)
                            draggingChainPos = targetPos
                        }
                    },
                    onDragEnd = {
                        draggingChainPos = null
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        draggingChainPos = null
                        dragOffset = Offset.Zero
                    }
                )
            },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Grid Row 1 (Positions 0 .. 5)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (pos in 0 until 6) {
                val modIndex = preset.chainOrder.getOrNull(pos) ?: pos
                val isBeingDragged = draggingChainPos == pos

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            cellBounds[pos] = coordinates.boundsInParent()
                        }
                        .zIndex(if (isBeingDragged) 10f else 1f)
                        .graphicsLayer {
                            if (isBeingDragged) {
                                translationX = dragOffset.x
                                translationY = dragOffset.y
                                scaleX = 1.1f
                                scaleY = 1.1f
                            }
                        }
                ) {
                    ChainBlock(
                        module = PEDAL_MODULES[modIndex],
                        state = preset.modules[modIndex],
                        isSelected = modIndex == preset.selectedModuleIndex,
                        onClick = {
                            if (draggingChainPos == null) {
                                onModuleSelect(modIndex)
                            }
                        },
                        positionIndex = pos + 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isBeingDragged) {
                                    Modifier
                                        .shadow(8.dp, RoundedCornerShape(10.dp))
                                        .border(2.dp, PedalOrange, RoundedCornerShape(10.dp))
                                } else Modifier
                            )
                    )
                }
            }
        }

        // Grid Row 2 (Positions 6 .. 11)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (pos in 6 until 12) {
                val modIndex = preset.chainOrder.getOrNull(pos) ?: pos
                val isBeingDragged = draggingChainPos == pos

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            cellBounds[pos] = coordinates.boundsInParent()
                        }
                        .zIndex(if (isBeingDragged) 10f else 1f)
                        .graphicsLayer {
                            if (isBeingDragged) {
                                translationX = dragOffset.x
                                translationY = dragOffset.y
                                scaleX = 1.1f
                                scaleY = 1.1f
                            }
                        }
                ) {
                    ChainBlock(
                        module = PEDAL_MODULES[modIndex],
                        state = preset.modules[modIndex],
                        isSelected = modIndex == preset.selectedModuleIndex,
                        onClick = {
                            if (draggingChainPos == null) {
                                onModuleSelect(modIndex)
                            }
                        },
                        positionIndex = pos + 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isBeingDragged) {
                                    Modifier
                                        .shadow(8.dp, RoundedCornerShape(10.dp))
                                        .border(2.dp, PedalOrange, RoundedCornerShape(10.dp))
                                } else Modifier
                            )
                    )
                }
            }
        }
    }
}
