package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.CalculatorViewModel

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcHistoryBarOptionsSheet(
    viewModel: CalculatorViewModel,
    gridlineStyle: String,
    itemSpacingDp: Int,
    maxItemsCount: Int,
    isAdaptive: Boolean,
    showHistoryBanner: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val calcHistoryAutoScrollTop by viewModel.calcHistoryAutoScrollTop.collectAsStateWithLifecycle()
    val calcHistoryShowGridlines by viewModel.calcHistoryShowGridlines.collectAsStateWithLifecycle()
    val calcHistoryGridlineStrokeWidthDp by viewModel.calcHistoryGridlineStrokeWidthDp.collectAsStateWithLifecycle()
    val calcHistoryGridlineAlpha by viewModel.calcHistoryGridlineAlpha.collectAsStateWithLifecycle()
    val calcHistoryShowItemDividers by viewModel.calcHistoryShowItemDividers.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sheet Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History Bar Options",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Calc Section History Bar Customization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // CARD 1: Gridline Visibility & Style
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gridline Visibility", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Show top divider line above recent calculation history items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = calcHistoryShowGridlines,
                            onCheckedChange = { viewModel.setCalcHistoryShowGridlines(it) },
                            modifier = Modifier.testTag("calc_history_gridline_switch")
                        )
                    }

                    if (calcHistoryShowGridlines) {
                        HorizontalDivider()

                        Text("Gridline Pattern Style:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "DASHED" to "Dashed",
                                "SOLID" to "Solid",
                                "DOTTED" to "Dotted",
                                "NONE" to "None"
                            ).forEach { (styleKey, label) ->
                                ElevatedFilterChip(
                                    selected = gridlineStyle == styleKey,
                                    onClick = { viewModel.setCalcHistoryGridlineStyle(styleKey) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("gridline_style_$styleKey")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text("Gridline Thickness / Weight (Girdling):", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                1.0f to "1.0dp",
                                1.5f to "1.5dp",
                                2.0f to "2.0dp",
                                3.0f to "3.0dp",
                                4.0f to "4.0dp"
                            ).forEach { (wVal, label) ->
                                ElevatedFilterChip(
                                    selected = kotlin.math.abs(calcHistoryGridlineStrokeWidthDp - wVal) < 0.1f,
                                    onClick = { viewModel.setCalcHistoryGridlineStrokeWidthDp(wVal) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("gridline_stroke_$wVal")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text("Gridline Visibility / Opacity:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                0.25f to "25% Low",
                                0.50f to "50% Med",
                                0.75f to "75% High",
                                0.85f to "85% Std",
                                1.00f to "100% Solid"
                            ).forEach { (aVal, label) ->
                                ElevatedFilterChip(
                                    selected = kotlin.math.abs(calcHistoryGridlineAlpha - aVal) < 0.08f,
                                    onClick = { viewModel.setCalcHistoryGridlineAlpha(aVal) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).testTag("gridline_alpha_$aVal")
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dividers Between History Items", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text("Show separator gridlines between calculation history rows", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = calcHistoryShowItemDividers,
                                onCheckedChange = { viewModel.setCalcHistoryShowItemDividers(it) },
                                modifier = Modifier.testTag("show_item_dividers_switch")
                            )
                        }
                    }
                }
            }

            // CARD 2: Auto-Scroll Customization (Up/Top vs Down/Bottom)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Auto-Scroll Direction", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Choose whether new calculation results automatically scroll to the top or bottom of the history list",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ElevatedFilterChip(
                            selected = calcHistoryAutoScrollTop,
                            onClick = { viewModel.setCalcHistoryAutoScrollTop(true) },
                            label = { Text("Auto Scroll UP (Top)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("auto_scroll_up_chip")
                        )
                        ElevatedFilterChip(
                            selected = !calcHistoryAutoScrollTop,
                            onClick = { viewModel.setCalcHistoryAutoScrollTop(false) },
                            label = { Text("Auto Scroll DOWN (Bottom)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("auto_scroll_down_chip")
                        )
                    }
                }
            }

            // CARD 3: Layout Spacing & Max Item Count
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Item Spacing (Gap):", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2, 4, 6, 8, 10).forEach { dpVal ->
                                ElevatedFilterChip(
                                    selected = itemSpacingDp == dpVal,
                                    onClick = { viewModel.setCalcHistoryItemSpacingDp(dpVal) },
                                    label = { Text("${dpVal}dp", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("item_spacing_$dpVal")
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Max Items Displayed:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 2, 3, 4, 5, 7, 10).forEach { count ->
                                ElevatedFilterChip(
                                    selected = maxItemsCount == count,
                                    onClick = { viewModel.setCalcHistoryMaxItemsCount(count) },
                                    label = { Text("$count", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f).testTag("max_items_$count")
                                )
                            }
                        }
                    }
                }
            }

            // CARD 4: Adaptive Height & Quick Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Adaptive Bar Resizing", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Auto-adjust bar height based on available screen space", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isAdaptive,
                            onCheckedChange = { viewModel.setCalcHistoryIsAdaptive(it) },
                            modifier = Modifier.testTag("calc_history_adaptive_switch")
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Show History Banner", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Display quick top history bar above calculator display", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = showHistoryBanner,
                            onCheckedChange = { viewModel.setShowCalcHistoryBar(it) },
                            modifier = Modifier.testTag("show_calc_history_bar_switch")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
