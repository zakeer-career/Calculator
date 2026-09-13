package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationGuideSheet(
    onDismiss: () -> Unit,
    onTryExample: ((String) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTabIdx by remember { mutableIntStateOf(0) }

    val categories = listOf("Unit Methods (km→cm)", "Percentages & Tips", "Scientific & Tricks", "Memory Keys", "Converters", "Matrix")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("calculation_guide_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Header Row (without redundant close icon button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Calculation Guide & Tricks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Unit methods, shortcuts, syntax & memory keys",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab selection row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIdx,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                categories.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTabIdx == idx,
                        onClick = { selectedTabIdx = idx },
                        text = { Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Tab Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTabIdx) {
                    0 -> KmToCmUnitMethodsGuideContent(onTryExample, onDismiss)
                    1 -> PercentageAndTipsGuideContent(onTryExample, onDismiss)
                    2 -> ScientificAndTricksGuideContent(onTryExample, onDismiss)
                    3 -> MemoryGuideContent(onTryExample, onDismiss)
                    4 -> ConvertersGuideContent(onTryExample, onDismiss)
                    5 -> MatrixGuideContent(onTryExample, onDismiss)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun KmToCmUnitMethodsGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Kilometer to Centimeter (km → cm) Methods", Icons.Default.Straighten)

    Text(
        "3 distinct mathematical methods to accurately convert Kilometers to Centimeters:",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val methods = listOf(
        Triple(
            "5.2 × 100000",
            "520000 cm",
            "1. Direct Multiplication Method:\nSince 1 km = 100,000 cm, multiply the kilometer value directly by 10^5 (100,000).\nFormula: 5.2 km × 100,000 = 520,000 cm."
        ),
        Triple(
            "(5.2 × 1000) × 100",
            "520000 cm",
            "2. Step-by-Step Metric Scale Method:\nConvert km → meters (× 1,000), then meters → centimeters (× 100).\nDimensional Analysis: 5.2 km × (1000 m/1 km) × (100 cm/1 m) = 520,000 cm."
        ),
        Triple(
            "5.2 × 10^5",
            "520000 cm",
            "3. Scientific Notation Method:\nExpress the conversion factor as powers of 10.\nNotation: 5.2 × 10^0 km = 5.2 × 10^5 cm."
        )
    )

    methods.forEach { (expr, res, desc) ->
        ExampleCard(
            expression = expr,
            result = res,
            description = desc,
            onTryExample = {
                onTryExample?.invoke(expr)
                onDismiss()
            }
        )
    }
}

@Composable
private fun PercentageAndTipsGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Percentage Operations & Quick Tips", Icons.Default.Percent)

    val examples = listOf(
        Triple("100 + 15%", "115", "Fast Percentage Markup (100 + 15% vs 100 × 1.15)"),
        Triple("100 − 20%", "80", "Fast Discount Application (100 − 20% vs 100 × 0.80)"),
        Triple("85 × 15%", "12.75", "Quick Tip Splitting Rule (15% tip on $85 bill)"),
        Triple("85 × 20%", "17", "Quick Tip Splitting Rule (20% tip on $85 bill)"),
        Triple("100 ÷ 10%", "1000", "Base Value Finder (Finds original amount where 10% = 100)"),
        Triple("50%", "0.5", "Standalone Decimal Conversion")
    )

    examples.forEach { (expr, res, desc) ->
        ExampleCard(
            expression = expr,
            result = res,
            description = desc,
            onTryExample = {
                onTryExample?.invoke(expr)
                onDismiss()
            }
        )
    }
}

@Composable
private fun ScientificAndTricksGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Scientific Shortcuts & Functions", Icons.Default.Functions)

    val examples = listOf(
        Triple("sin(30)", "0.5", "Trigonometric Sine (Toggle DEG mode)"),
        Triple("asin(0.5)", "30", "Inverse Sine (Press INV key for inverse functions)"),
        Triple("√16", "4", "Square Root function sqrt(x)"),
        Triple("2^8", "256", "Exponentiation / Power shortcut y^x"),
        Triple("17 % 5", "2", "Modulo Arithmetic (Remainder of 17 ÷ 5)"),
        Triple("5!", "120", "Factorial function n!"),
        Triple("log(100)", "2", "Base-10 Logarithm"),
        Triple("ln(e)", "1", "Natural Logarithm (base e)")
    )

    examples.forEach { (expr, res, desc) ->
        ExampleCard(
            expression = expr,
            result = res,
            description = desc,
            onTryExample = {
                onTryExample?.invoke(expr)
                onDismiss()
            }
        )
    }
}

@Composable
private fun MemoryGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Step-by-Step Memory Key Guide", Icons.Default.Calculate)

    val keys = listOf(
        Triple("M+", "Add Result", "Adds evaluated current expression to stored memory.\nExample: Type 150 × 2, tap M+"),
        Triple("M-", "Subtract Result", "Subtracts evaluated expression from memory.\nExample: Type 50, tap M-"),
        Triple("MR", "Memory Recall", "Recalls stored memory value into input field.\nExample: Tap MR to retrieve value"),
        Triple("MC", "Memory Clear", "Clears stored memory back to 0.")
    )

    keys.forEach { (key, title, desc) ->
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            key,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (key == "M+" || key == "MR") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                onTryExample?.invoke(if (key == "M+") "150 × 2" else "MR")
                                onDismiss()
                            }
                    ) {
                        Text(
                            "Try It",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConvertersGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Currency & Unit Converters", Icons.Default.CurrencyExchange)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Currency Converter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("• Converts over 150 global currencies in real-time.", style = MaterialTheme.typography.bodyMedium)
            Text("• Shows official country flags for easy identification.", style = MaterialTheme.typography.bodyMedium)
            Text("• Saves conversion results directly into History Stream.", style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unit Converter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("• Includes Length, Area, Volume, Weight, Temperature, Speed, Time, Pressure & Energy.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun MatrixGuideContent(
    onTryExample: ((String) -> Unit)?,
    onDismiss: () -> Unit
) {
    GuideSectionHeader("Matrix Calculator", Icons.Default.GridOn)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Grid Dimensions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("• Configure Matrix A and Matrix B dimensions from 1×1 up to 4×4.", style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Supported Operations", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("• A + B, A − B, A × B (Dot Product), k × A", style = MaterialTheme.typography.bodyMedium)
            Text("• Det(A) (Determinant), Inv(A) (Inverse), Transpose & Rank", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun GuideSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExampleCard(
    expression: String,
    result: String,
    description: String,
    onTryExample: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onTryExample != null) { onTryExample?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        expression,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " = ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                    Text(
                        result,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onTryExample != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        "Try It",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
