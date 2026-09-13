package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiveLayoutPreviewCard(
    displayHeightDp: Int,
    displayWidthPaddingDp: Int,
    displayCornerRadiusDp: Int,
    displayMainFontSizeSp: Int,
    displayPreviewFontSizeSp: Int,
    keypadHeightScale: Float,
    keypadWidthPaddingDp: Int,
    keypadGridSpacingDp: Int,
    keypadBtnCornerRadiusDp: Int,
    keypadBtnFontSizeSp: Int,
    lockKeypadHeight: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Layout Preview",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (lockKeypadHeight) "Height Locked (No Jumping)" else "Auto-Height (Resizable)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Preview Display Card
            val miniDisplayHeight = (displayHeightDp * 0.7f).dp.coerceIn(70.dp, 160.dp)
            val miniMainFont = (displayMainFontSizeSp * 0.75f).sp
            val miniPreviewFont = (displayPreviewFontSizeSp * 0.75f).sp

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(miniDisplayHeight)
                    .padding(horizontal = (displayWidthPaddingDp * 0.6f).dp),
                shape = RoundedCornerShape((displayCornerRadiusDp * 0.7f).dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "125 × 8 = 1000",
                        fontSize = miniPreviewFont,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "1000",
                        fontSize = miniMainFont,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Preview Keypad Grid
            val miniGridGap = (keypadGridSpacingDp * 0.7f).dp
            val miniBtnCorner = (keypadBtnCornerRadiusDp * 0.7f).dp
            val miniBtnFont = (keypadBtnFontSizeSp * 0.75f).sp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (keypadWidthPaddingDp * 0.6f).dp),
                verticalArrangement = Arrangement.spacedBy(miniGridGap)
            ) {
                val previewButtons = listOf(
                    listOf("C", "(", ")", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "−"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=", "%")
                )

                previewButtons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(miniGridGap)
                    ) {
                        row.forEach { btnText ->
                            val isOp = btnText in listOf("÷", "×", "−", "+", "=")
                            val isAction = btnText in listOf("C", "(", ")", "%")
                            val btnBg = when {
                                btnText == "=" -> MaterialTheme.colorScheme.primary
                                isOp -> MaterialTheme.colorScheme.primaryContainer
                                isAction -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.surfaceContainer
                            }
                            val btnTextColor = when {
                                btnText == "=" -> MaterialTheme.colorScheme.onPrimary
                                isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                                isAction -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            val scaleRatio = (if (keypadHeightScale in 30f..70f) keypadHeightScale else 52f) / 50f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((34 * scaleRatio).dp)
                                    .clip(RoundedCornerShape(miniBtnCorner))
                                    .background(btnBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = btnText,
                                    fontSize = miniBtnFont,
                                    fontWeight = FontWeight.Bold,
                                    color = btnTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
