package com.zakeercareer.calculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Tune
import com.zakeercareer.calculator.ui.components.SwipeAndHistoryCustomizationSheet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.data.db.CalculationEntity
import com.zakeercareer.calculator.ui.components.HistoryItemActionSheet
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: CalculatorViewModel,
    historyList: List<CalculationEntity>,
    selectedCategoryFilter: String,
    searchQuery: String,
    onInsertToCalc: (String) -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showCustomizationSheet by remember { mutableStateOf(false) }
    var showRecycleBinSheet by remember { mutableStateOf(false) }
    var entryForEditingNote by remember { mutableStateOf<CalculationEntity?>(null) }
    var selectedItemForAction by remember { mutableStateOf<CalculationEntity?>(null) }

    val historyGridlinesEnabled by viewModel.historyGridlinesEnabled.collectAsStateWithLifecycle()
    val historyDeletionLocked by viewModel.historyDeletionLocked.collectAsStateWithLifecycle()
    val swipeLeftAction by viewModel.historySwipeLeftAction.collectAsStateWithLifecycle()
    val swipeRightAction by viewModel.historySwipeRightAction.collectAsStateWithLifecycle()
    val gridlineStyle by viewModel.calcHistoryGridlineStyle.collectAsStateWithLifecycle()
    val itemSpacingDp by viewModel.calcHistoryItemSpacingDp.collectAsStateWithLifecycle()
    val maxItemsCount by viewModel.calcHistoryMaxItemsCount.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search equation, result, or note...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_field"),
            singleLine = true
        )

        // Category Filter Chips & Clear Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "All",
                    "STANDARD" to "Standard",
                    "MATRIX" to "Matrix",
                    "UNIT" to "Unit",
                    "CURRENCY" to "Currency",
                    "FAVORITES" to "★ Favorites"
                ).forEach { (key, label) ->
                    ElevatedFilterChip(
                        selected = selectedCategoryFilter == key,
                        onClick = { viewModel.setFilterCategory(key) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("filter_chip_$key")
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showCustomizationSheet = true },
                    modifier = Modifier.testTag("customize_history_swipe_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Customize Swipe & Lock",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { showRecycleBinSheet = true },
                    modifier = Modifier.testTag("recycle_bin_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Recycle Bin",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { viewModel.setHistoryGridlinesEnabled(!historyGridlinesEnabled) },
                    modifier = Modifier.testTag("toggle_gridlines_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Toggle Gridlines",
                        tint = if (historyGridlinesEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = {
                        if (historyDeletionLocked) {
                            Toast.makeText(context, "🔒 History deletion is locked to prevent accidental deletion", Toast.LENGTH_SHORT).show()
                        } else {
                            showClearDialog = true
                        }
                    },
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Icon(
                        imageVector = if (historyDeletionLocked) Icons.Default.Lock else Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = if (historyDeletionLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Privacy Card Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Privacy", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Privacy Protected: All calculations & history stored 100% on-device.",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // History List
        if (historyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No calculations found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            val listState = rememberLazyListState()

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        entry = item,
                        showGridlines = historyGridlinesEnabled,
                        swipeLeftAction = swipeLeftAction,
                        swipeRightAction = swipeRightAction,
                        onToggleFavorite = { viewModel.toggleFavorite(item) },
                        onEditNote = { entryForEditingNote = item },
                        onDelete = {
                            val success = viewModel.deleteHistoryEntry(item)
                            if (!success) {
                                Toast.makeText(context, "🔒 History deletion is locked to prevent accidental deletion", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCopyResult = {
                            clipboardManager.setText(AnnotatedString(item.result))
                            Toast.makeText(context, "Copied result to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onInsertToCalc = {
                            onInsertToCalc(item.result)
                            Toast.makeText(context, "Inserted result into Calculator", Toast.LENGTH_SHORT).show()
                        },
                        onItemClick = {
                            selectedItemForAction = item
                        }
                    )
                }
            }
        }
    }

    // Customization Sheet
    if (showCustomizationSheet) {
        SwipeAndHistoryCustomizationSheet(
            viewModel = viewModel,
            swipeLeftAction = swipeLeftAction,
            swipeRightAction = swipeRightAction,
            gridlinesEnabled = historyGridlinesEnabled,
            historyDeletionLocked = historyDeletionLocked,
            onOpenRecycleBin = { showRecycleBinSheet = true },
            onDismiss = { showCustomizationSheet = false }
        )
    }

    // Recycle Bin Sheet
    if (showRecycleBinSheet) {
        com.zakeercareer.calculator.ui.components.RecycleBinSheet(
            viewModel = viewModel,
            onDismiss = { showRecycleBinSheet = false }
        )
    }

    // Action Modal Sheet (Apply Result, Apply Equation, Copy Result, Copy)
    selectedItemForAction?.let { item ->
        HistoryItemActionSheet(
            item = item,
            onDismiss = { selectedItemForAction = null },
            onApplyResult = { res -> viewModel.applyResult(res) },
            onApplyEquation = { eq -> viewModel.applyEquation(eq) }
        )
    }

    // Clear History Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to clear history entries for '$selectedCategoryFilter'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Note Dialog
    entryForEditingNote?.let { item ->
        var noteText by remember { mutableStateOf(item.note ?: "") }
        AlertDialog(
            onDismissRequest = { entryForEditingNote = null },
            title = { Text("Custom Note / Label") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note e.g., Monthly Rent or Det Result") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateEntryNote(item, noteText)
                        entryForEditingNote = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryForEditingNote = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItemCard(
    entry: CalculationEntity,
    showGridlines: Boolean = true,
    swipeLeftAction: String = "DELETE",
    swipeRightAction: String = "INSERT",
    onToggleFavorite: () -> Unit,
    onEditNote: () -> Unit,
    onDelete: () -> Unit,
    onCopyResult: () -> Unit,
    onInsertToCalc: () -> Unit,
    onItemClick: () -> Unit = {}
) {
    val df = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val dateStr = remember(entry.timestamp) { df.format(Date(entry.timestamp)) }

    fun handleAction(actionKey: String) {
        when (actionKey) {
            "DELETE" -> onDelete()
            "COPY" -> onCopyResult()
            "INSERT" -> onInsertToCalc()
            "FAVORITE" -> onToggleFavorite()
            "NOTE" -> onEditNote()
            else -> onDelete()
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    handleAction(swipeLeftAction)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    handleAction(swipeRightAction)
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val targetValue = dismissState.targetValue
            val isSwiping = targetValue != SwipeToDismissBoxValue.Settled

            val isLeftSwipe = targetValue == SwipeToDismissBoxValue.EndToStart || direction == SwipeToDismissBoxValue.EndToStart
            val activeAction = if (isLeftSwipe) swipeLeftAction else swipeRightAction

            val (bgColor, icon, label, tint) = when (activeAction) {
                "DELETE" -> Quadruple(
                    MaterialTheme.colorScheme.errorContainer,
                    Icons.Default.Delete,
                    "Delete",
                    MaterialTheme.colorScheme.onErrorContainer
                )
                "COPY" -> Quadruple(
                    MaterialTheme.colorScheme.primaryContainer,
                    Icons.Default.ContentCopy,
                    "Copy Result",
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                "INSERT" -> Quadruple(
                    MaterialTheme.colorScheme.secondaryContainer,
                    Icons.Default.Input,
                    "Insert to Calc",
                    MaterialTheme.colorScheme.onSecondaryContainer
                )
                "FAVORITE" -> Quadruple(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    Icons.Default.Star,
                    "Favorite",
                    MaterialTheme.colorScheme.onTertiaryContainer
                )
                "NOTE" -> Quadruple(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    Icons.Default.Edit,
                    "Edit Note",
                    MaterialTheme.colorScheme.primary
                )
                else -> Quadruple(
                    MaterialTheme.colorScheme.errorContainer,
                    Icons.Default.Delete,
                    "Delete",
                    MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (isSwiping) bgColor else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isLeftSwipe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                if (isSwiping) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isLeftSwipe) {
                            Icon(imageVector = icon, contentDescription = label, tint = tint)
                            Text(text = label, color = tint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Text(text = label, color = tint, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(imageVector = icon, contentDescription = label, tint = tint)
                        }
                    }
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = if (showGridlines) BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)) else null
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row (Category, Details, Favorite)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!entry.details.isNullOrBlank()) {
                            Text(" • ${entry.details}", style = MaterialTheme.typography.labelSmall)
                        }
                        Text(" • $dateStr", style = MaterialTheme.typography.labelSmall)
                    }

                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (entry.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (showGridlines) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                }

                // Custom Note if exists
                if (!entry.note.isNullOrBlank()) {
                    Text(
                        text = "📝 ${entry.note}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    if (showGridlines) {
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }

                // Expression & Result
                Text(
                    text = entry.expression,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (showGridlines) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }

                Text(
                    text = "= ${entry.result}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (showGridlines) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                }

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEditNote) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                    }
                    IconButton(onClick = onCopyResult) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Result")
                    }
                    if (entry.category == "STANDARD") {
                        IconButton(onClick = onInsertToCalc) {
                            Icon(Icons.Default.Input, contentDescription = "Insert into Calculator")
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Entry", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this.isNullOrBlank()

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
