package com.zakeercareer.calculator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zakeercareer.calculator.ui.theme.AllCalculatorThemes
import com.zakeercareer.calculator.ui.theme.CalculatorThemeStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionBottomSheet(
    selectedThemeId: String,
    onSelectTheme: (String) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("theme_selection_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Themes",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = "Expressive Theme Studio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "10 Dribbble-inspired Material 3 concepts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Theme Cards Horizontal Slider
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AllCalculatorThemes, key = { it.id }) { themeStyle ->
                    val isSelected = themeStyle.id == selectedThemeId

                    ThemePreviewCard(
                        themeStyle = themeStyle,
                        isSelected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSelectTheme(themeStyle.id)
                        }
                    )
                }
            }

            // Quick Info Banner for Selected Theme
            val activeTheme = AllCalculatorThemes.find { it.id == selectedThemeId } ?: AllCalculatorThemes[0]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeTheme.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = activeTheme.subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemePreviewCard(
    themeStyle: CalculatorThemeStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    Card(
        modifier = Modifier
            .width(165.dp)
            .height(210.dp)
            .clickable { onClick() }
            .testTag("theme_card_${themeStyle.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = themeStyle.colorScheme.surfaceContainerHigh
        ),
        border = BorderStroke(if (isSelected) 2.5.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header with Tag and Check icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = themeStyle.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = themeStyle.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.colorScheme.onPrimaryContainer
                        )
                    }

                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Mini Calculator Preview Screen & Keypad Mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeStyle.colorScheme.surfaceContainerHighest)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Display line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(themeStyle.colorScheme.surface),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "1,248.50",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.colorScheme.primary,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Keypad Grid Mockup (3x3 chips)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.primaryContainer)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.secondaryContainer)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.primary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.surface)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.surface)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(themeStyle.colorScheme.surface)
                            )
                        }
                    }
                }

                // Theme Name & Subtitle
                Column {
                    Text(
                        text = themeStyle.name,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = themeStyle.buttonShapeStyle,
                        fontSize = 10.5.sp,
                        color = themeStyle.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
