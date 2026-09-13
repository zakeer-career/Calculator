package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.CalculatorViewModel

@Composable
fun MatrixScreen(viewModel: CalculatorViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Matrix A, 1: Matrix B, 2: Scalar k
    val aRows by viewModel.matrixARows.collectAsStateWithLifecycle()
    val aCols by viewModel.matrixACols.collectAsStateWithLifecycle()
    val matrixA by viewModel.matrixA.collectAsStateWithLifecycle()

    val bRows by viewModel.matrixBRows.collectAsStateWithLifecycle()
    val bCols by viewModel.matrixBCols.collectAsStateWithLifecycle()
    val matrixB by viewModel.matrixB.collectAsStateWithLifecycle()

    val scalarK by viewModel.scalarK.collectAsStateWithLifecycle()
    val matrixResultText by viewModel.matrixResultText.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Matrix A (${aRows}x${aCols})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Matrix B (${bRows}x${bCols})") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Scalar k ($scalarK)") }
            )
        }

        // Active Matrix / Scalar Editor
        when (selectedTab) {
            0 -> MatrixEditor(
                label = "Matrix A",
                rows = aRows,
                cols = aCols,
                matrix = matrixA,
                onDimensionsChanged = { r, c -> viewModel.updateMatrixDimensionsA(r, c) },
                onCellChanged = { r, c, v -> viewModel.updateMatrixACell(r, c, v) }
            )
            1 -> MatrixEditor(
                label = "Matrix B",
                rows = bRows,
                cols = bCols,
                matrix = matrixB,
                onDimensionsChanged = { r, c -> viewModel.updateMatrixDimensionsB(r, c) },
                onCellChanged = { r, c, v -> viewModel.updateMatrixBCell(r, c, v) }
            )
            2 -> ScalarEditor(
                scalarK = scalarK,
                onScalarChanged = { viewModel.updateScalarK(it) }
            )
        }

        // Operation Buttons Grid
        Text(
            text = "Select Matrix Operation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        val operations = listOf(
            "A + B", "A - B", "A × B",
            "k × A", "Det(A)", "Inv(A)",
            "Transpose(A)", "Trace(A)", "Rank(A)"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            operations.forEach { op ->
                ElevatedFilterChip(
                    selected = false,
                    onClick = { viewModel.executeMatrixOperation(op) },
                    label = { Text(op, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("matrix_op_$op")
                )
            }
        }

        // Result Matrix Display Card
        if (matrixResultText.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("matrix_result_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Matrix Result",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = matrixResultText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatrixEditor(
    label: String,
    rows: Int,
    cols: Int,
    matrix: Array<DoubleArray>,
    onDimensionsChanged: (rows: Int, cols: Int) -> Unit,
    onCellChanged: (r: Int, c: Int, value: Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Size: ", style = MaterialTheme.typography.bodySmall)
                    listOf(2, 3, 4).forEach { size ->
                        ElevatedFilterChip(
                            selected = rows == size && cols == size,
                            onClick = { onDimensionsChanged(size, size) },
                            label = { Text("${size}x${size}") },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            // Input Matrix Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (r in 0 until rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0 until cols) {
                                val cellValue = matrix.getOrNull(r)?.getOrNull(c) ?: 0.0
                                var textState by remember(r, c, cellValue) {
                                    mutableStateOf(if (cellValue == 0.0) "0" else cellValue.toString().removeSuffix(".0"))
                                }

                                OutlinedTextField(
                                    value = textState,
                                    onValueChange = { input ->
                                        textState = input
                                        val num = input.toDoubleOrNull()
                                        if (num != null) {
                                            onCellChanged(r, c, num)
                                        } else if (input.isBlank() || input == "-") {
                                            onCellChanged(r, c, 0.0)
                                        }
                                    },
                                    modifier = Modifier
                                        .width(72.dp)
                                        .height(56.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScalarEditor(
    scalarK: Double,
    onScalarChanged: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Scalar Value (k)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = scalarK.toString(),
                onValueChange = { input ->
                    val num = input.toDoubleOrNull() ?: 1.0
                    onScalarChanged(num)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter scalar multiplier k") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
