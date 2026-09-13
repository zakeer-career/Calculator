package com.zakeercareer.calculator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.data.db.CalculationEntity
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinSheet(
    viewModel: CalculatorViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val trashList by viewModel.trashList.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Recycle Bin",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Recycle Bin (${trashList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (trashList.isNotEmpty()) {
                    Button(
                        onClick = { viewModel.emptyTrash() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.testTag("empty_trash_btn")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Empty Trash", fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider()

            if (trashList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.height(48.dp).width(48.dp)
                        )
                        Text(
                            text = "Recycle Bin is empty",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Deleted items will be temporarily stored here",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trashList, key = { it.id }) { item ->
                        TrashItemCard(
                            item = item,
                            onRestore = { viewModel.restoreFromTrash(item) },
                            onDeleteForever = { viewModel.permanentlyDeleteFromTrash(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrashItemCard(
    item: CalculationEntity,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    val dateStr = remember(item.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trash_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.expression,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "= ${item.result}",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${item.category} • $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onRestore,
                    modifier = Modifier.testTag("restore_trash_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore Item",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDeleteForever,
                    modifier = Modifier.testTag("delete_forever_trash_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Forever",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
