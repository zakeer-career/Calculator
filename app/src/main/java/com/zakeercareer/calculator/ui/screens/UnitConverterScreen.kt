package com.zakeercareer.calculator.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.ui.viewmodel.CalculatorViewModel
import com.zakeercareer.calculator.util.UnitCategory
import com.zakeercareer.calculator.util.UnitConverter
import com.zakeercareer.calculator.util.UnitItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    val haptic = LocalHapticFeedback.current
    viewModel: CalculatorViewModel,
    selectedCategory: UnitCategory,
    inputValue: String,
    fromUnit: UnitItem,
    toUnit: UnitItem,
    resultValue: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selection Horizontal Bar
        Text("Select Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UnitCategory.entries.forEach { category ->
                ElevatedFilterChip(
                    selected = category == selectedCategory,
                    onClick = { viewModel.selectUnitCategory(category) },
                    label = { Text(category.displayName) },
                    modifier = Modifier.testTag("unit_cat_${category.name}")
                )
            }
        }

        // Input Value Field
        OutlinedTextField(
            value = inputValue,
            onValueChange = { viewModel.updateUnitInputValue(it) },
            label = { Text("Input Value") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unit_input_field"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // From Unit -> Swap -> To Unit Selectors
        val unitsList = UnitConverter.getUnits(selectedCategory)

        // From Unit Selector
        var expandedFrom by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedFrom,
            onExpandedChange = { expandedFrom = !expandedFrom },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "${fromUnit.name} (${fromUnit.symbol})",
                onValueChange = {},
                readOnly = true,
                label = { Text("From Unit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedFrom,
                onDismissRequest = { expandedFrom = false }
            ) {
                unitsList.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text("${unit.name} (${unit.symbol})") },
                        onClick = {
                            viewModel.selectFromUnit(unit)
                            expandedFrom = false
                        }
                    )
                }
            }
        }

        // Centered Swap Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalIconButton(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.swapUnits() },
                modifier = Modifier.testTag("unit_swap_btn")
            ) {
                Icon(Icons.Default.SwapVert, contentDescription = "Swap Units")
            }
        }

        // To Unit Selector
        var expandedTo by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedTo,
            onExpandedChange = { expandedTo = !expandedTo },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "${toUnit.name} (${toUnit.symbol})",
                onValueChange = {},
                readOnly = true,
                label = { Text("To Unit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedTo,
                onDismissRequest = { expandedTo = false }
            ) {
                unitsList.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text("${unit.name} (${unit.symbol})") },
                        onClick = {
                            viewModel.selectToUnit(unit)
                            expandedTo = false
                        }
                    )
                }
            }
        }

        // Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unit_result_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Converted Result",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$resultValue ${toUnit.symbol}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = "$inputValue ${fromUnit.name} = $resultValue ${toUnit.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Save Conversion to History Button
        ElevatedButton(
            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); viewModel.saveUnitConversionHistory() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_unit_history_btn")
        ) {
            Icon(Icons.Default.Bookmark, contentDescription = "Save")
            Text("  Save Conversion to History", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(88.dp))
        Spacer(modifier = Modifier.navigationBarsPadding().height(80.dp))
    }
}
