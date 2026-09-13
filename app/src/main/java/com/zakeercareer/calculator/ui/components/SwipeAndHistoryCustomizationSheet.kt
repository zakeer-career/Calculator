package com.zakeercareer.calculator.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeAndHistoryCustomizationSheet(
    viewModel: CalculatorViewModel,
    swipeLeftAction: String,
    swipeRightAction: String,
    gridlinesEnabled: Boolean,
    historyDeletionLocked: Boolean,
    onOpenRecycleBin: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Customization",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "History & Swipe Customization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // SECTION 1: HISTORY DELETION LOCK & RECYCLE BIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (historyDeletionLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock History Deletion",
                        tint = if (historyDeletionLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text("Lock History Deletion", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (historyDeletionLocked) "Prevent accidental deletion of history" else "Deletion allowed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = historyDeletionLocked,
                    onCheckedChange = { viewModel.setHistoryDeletionLocked(it) },
                    modifier = Modifier.testTag("lock_history_deletion_switch")
                )
            }

            // Shortcut to Recycle Bin
            ElevatedButton(
                onClick = {
                    onDismiss()
                    onOpenRecycleBin()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_recycle_bin_sheet_btn")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Open Recycle Bin")
            }

            HorizontalDivider()

            // SECTION 2: HISTORY GRIDLINES TOGGLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.GridOn, contentDescription = "History Gridlines", tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text("Show Gridlines in History", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Display subtle separator gridlines between calculation cards", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = gridlinesEnabled,
                    onCheckedChange = { viewModel.setHistoryGridlinesEnabled(it) },
                    modifier = Modifier.testTag("history_gridlines_switch")
                )
            }

            HorizontalDivider()

            // SECTION 3: SWIPE ACTIONS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Swipe, contentDescription = "Swipe Actions", tint = MaterialTheme.colorScheme.tertiary)
                Text("Customize Swipe Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }

            // Swipe Left Action Choice
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Swipe Left (←) Action:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "DELETE" to "🗑 Delete",
                        "COPY" to "📋 Copy Result",
                        "INSERT" to "📥 Insert to Calc",
                        "FAVORITE" to "★ Favorite",
                        "NOTE" to "📝 Edit Note"
                    ).forEach { (actionKey, label) ->
                        ElevatedFilterChip(
                            selected = swipeLeftAction == actionKey,
                            onClick = { viewModel.setHistorySwipeLeftAction(actionKey) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("swipe_left_$actionKey")
                        )
                    }
                }
            }

            // Swipe Right Action Choice
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Swipe Right (→) Action:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "INSERT" to "📥 Insert to Calc",
                        "COPY" to "📋 Copy Result",
                        "FAVORITE" to "★ Favorite",
                        "NOTE" to "📝 Edit Note",
                        "DELETE" to "🗑 Delete"
                    ).forEach { (actionKey, label) ->
                        ElevatedFilterChip(
                            selected = swipeRightAction == actionKey,
                            onClick = { viewModel.setHistorySwipeRightAction(actionKey) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("swipe_right_$actionKey")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
