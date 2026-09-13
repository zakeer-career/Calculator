package com.zakeercareer.calculator.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.data.db.CalculationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItemActionSheet(
    item: CalculationEntity,
    onDismiss: () -> Unit,
    onApplyResult: (String) -> Unit,
    onApplyEquation: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Prominent Expression = Result display header matching video
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${item.expression} = ${item.result}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 4 Action Buttons Row / Grid matching video
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Apply Result
                ActionCard(
                    icon = Icons.Default.AddBox,
                    title = "Apply Result",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_apply_result"),
                    onClick = {
                        onApplyResult(item.result)
                        Toast.makeText(context, "Applied Result (${item.result})", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // 2. Apply Equation
                ActionCard(
                    icon = Icons.Default.Functions,
                    title = "Apply Equation",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_apply_equation"),
                    onClick = {
                        onApplyEquation(item.expression)
                        Toast.makeText(context, "Applied Equation (${item.expression})", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // 3. Copy Result
                ActionCard(
                    icon = Icons.Default.ContentCopy,
                    title = "Copy Result",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_copy_result"),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(item.result))
                        Toast.makeText(context, "Copied Result (${item.result})", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // 4. Copy Equation / Copy
                ActionCard(
                    icon = Icons.Default.CopyAll,
                    title = "Copy Result",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_copy_equation"),
                    onClick = {
                        clipboardManager.setText(AnnotatedString(item.expression))
                        Toast.makeText(context, "Copied Equation (${item.expression})", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}
