package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CalculationEntity

@Composable
fun CustomHistoryGridline(
    style: String,
    modifier: Modifier = Modifier,
    strokeWidthDp: Float = 1.5f,
    gridlineAlpha: Float = 0.85f,
    color: Color = MaterialTheme.colorScheme.outline
) {
    val effectiveAlpha = gridlineAlpha.coerceIn(0.05f, 1.0f)
    val effectiveColor = color.copy(alpha = effectiveAlpha)

    val sw = strokeWidthDp.coerceIn(0.5f, 6.0f)

    when (style) {
        "SOLID" -> HorizontalDivider(modifier = modifier, thickness = sw.dp, color = effectiveColor)
        "DOTTED" -> Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height((sw + 2f).dp)
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 6f), 0f)
            drawLine(
                color = effectiveColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = sw * 2f,
                pathEffect = pathEffect
            )
        }
        "DASHED" -> Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height((sw + 2f).dp)
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
            drawLine(
                color = effectiveColor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = sw * 2f,
                pathEffect = pathEffect
            )
        }
        else -> HorizontalDivider(modifier = modifier, thickness = sw.dp, color = effectiveColor)
    }
}

@Composable
fun CalcSectionHistoryBar(
    historyList: List<CalculationEntity>,
    maxItemsCount: Int = 3,
    gridlineStyle: String = "SOLID",
    showGridlines: Boolean = true,
    strokeWidthDp: Float = 1.5f,
    gridlineAlpha: Float = 0.85f,
    showItemDividers: Boolean = true,
    autoScrollTop: Boolean = true,
    itemSpacingDp: Int = 6,
    isAdaptive: Boolean = true,
    isExpanded: Boolean = false,
    compactView: Boolean = false,
    onToggleExpand: () -> Unit,
    onSelectHistoryItem: (CalculationEntity) -> Unit,
    onToggleFavorite: (CalculationEntity) -> Unit,
    onOpenBarSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedList = if (isExpanded) historyList.take(20) else historyList.take(maxItemsCount.coerceIn(1, 15))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 15f && !isExpanded && historyList.isNotEmpty()) {
                        onToggleExpand()
                    } else if (dragAmount < -15f && isExpanded) {
                        onToggleExpand()
                    }
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Header Bar with drag indicator and controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (historyList.isNotEmpty()) onToggleExpand() }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History Bar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(18.dp)
                    )
                    Text(
                        text = if (isExpanded) "Recent History (Expanded)" else "Recent History",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (historyList.isEmpty()) "0" else "${displayedList.size}/${historyList.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenBarSettings,
                        modifier = Modifier
                            .height(28.dp)
                            .width(28.dp)
                            .testTag("history_bar_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Customize History Bar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.height(16.dp)
                        )
                    }
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = onToggleExpand,
                            modifier = Modifier
                                .height(28.dp)
                                .width(28.dp)
                                .testTag("toggle_expand_history_bar_btn")
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse History" else "Swipe Down or Tap to Expand",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
            }

            if (showGridlines && gridlineStyle != "NONE") {
                CustomHistoryGridline(
                    style = gridlineStyle,
                    strokeWidthDp = strokeWidthDp,
                    gridlineAlpha = gridlineAlpha,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(itemSpacingDp.dp.coerceIn(2.dp, 16.dp)))

            if (historyList.isEmpty()) {
                Text(
                    text = "No recent calculations. Perform a calculation to see history here.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                // History Items List
                val maxHeightDp = if (isExpanded) 220.dp else if (compactView) 85.dp else (maxItemsCount * 38 + 12).dp.coerceIn(70.dp, 180.dp)
                val listState = rememberLazyListState()

                LaunchedEffect(historyList.firstOrNull()?.id, historyList.size, displayedList.size, autoScrollTop) {
                    if (historyList.isNotEmpty() && displayedList.isNotEmpty()) {
                        kotlinx.coroutines.delay(30)
                        if (autoScrollTop) {
                            listState.animateScrollToItem(0)
                        } else {
                            val targetIndex = (displayedList.size - 1).coerceAtLeast(0)
                            listState.animateScrollToItem(targetIndex)
                        }
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxHeightDp),
                    verticalArrangement = Arrangement.spacedBy(itemSpacingDp.dp)
                ) {
                    itemsIndexed(displayedList, key = { _, item -> item.id }) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSelectHistoryItem(item) }
                                .padding(vertical = 3.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onToggleFavorite(item) },
                                modifier = Modifier
                                    .height(26.dp)
                                    .width(26.dp)
                                    .testTag("fav_item_${item.id}")
                            ) {
                                Icon(
                                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (item.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.height(16.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(1f).padding(start = 6.dp)
                            ) {
                                Text(
                                    text = item.expression,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = item.result,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (showItemDividers && showGridlines && index < displayedList.size - 1 && gridlineStyle != "NONE") {
                            CustomHistoryGridline(
                                style = gridlineStyle,
                                strokeWidthDp = strokeWidthDp,
                                gridlineAlpha = (gridlineAlpha * 0.7f).coerceIn(0.05f, 1.0f),
                                modifier = Modifier.padding(vertical = (itemSpacingDp / 2).dp.coerceIn(1.dp, 6.dp)),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
