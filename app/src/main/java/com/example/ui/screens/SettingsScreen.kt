package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun SettingsScreen(
    uiState: CostUiState,
    onCurrencyChange: (String) -> Unit,
    onSensitivityChange: (Double) -> Unit,
    onResetDemoData: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var seedInput by remember { mutableStateOf("42") }
    var currentSliderVal by remember { mutableStateOf(uiState.anomalySensitivity.toFloat()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Currency Selection
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Display Currency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Formats all financial metrics and charts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        listOf("USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)").forEachIndexed { index, curr ->
                            val code = curr.take(3)
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 4),
                                onClick = { onCurrencyChange(code) },
                                selected = uiState.preferredCurrency == code,
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = Cyan500,
                                    activeContentColor = Navy900
                                )
                            ) {
                                Text(code, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Anomaly Sensitivity
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Anomaly Detection Sensitivity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(color = Cyan300.copy(alpha = 0.25f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "${String.format("%.1f", currentSliderVal)}σ",
                                color = Cyan600,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Controls the Z-Score standard deviation multiplier. Lower values flag smaller spikes as anomalies; higher values flag only severe surges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = currentSliderVal,
                        onValueChange = { currentSliderVal = it },
                        onValueChangeFinished = { onSensitivityChange(currentSliderVal.toDouble()) },
                        valueRange = 1.5f..3.0f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = Cyan500,
                            activeTrackColor = Cyan500
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1.5σ (Sensitive)", style = MaterialTheme.typography.labelSmall, color = Slate400)
                        Text("2.0σ (Standard)", style = MaterialTheme.typography.labelSmall, color = Slate400)
                        Text("3.0σ (Critical Only)", style = MaterialTheme.typography.labelSmall, color = Slate400)
                    }
                }
            }
        }

        // Reset / Reseed Demo Data
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Demo Data Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Regenerate synthetic 90-day records with custom random seeds for testing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset / Reseed Demo Dataset")
                    }
                }
            }
        }

        // About Academic Project Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Cyan500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About AI Cloud Cost Detective",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Final-Year Computer Science & Cloud Engineering Project\n\n" +
                            "• Architecture: Jetpack Compose, Kotlin Flow, MVVM, Clean Architecture\n" +
                            "• Backend Support: FastAPI, SQLAlchemy, PostgreSQL 16\n" +
                            "• Algorithms: Rolling Z-score anomaly detection, OLS regression forecasting\n" +
                            "• Platform: Android Native Client\n" +
                            "• Version: 1.0.0 (Release-Ready)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reseed Synthetic Dataset") },
            text = {
                Column {
                    Text("Enter a random seed to generate reproducible cloud expenditure data:")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = seedInput,
                        onValueChange = { seedInput = it },
                        label = { Text("Seed (integer)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val seed = seedInput.toLongOrNull() ?: 42L
                        onResetDemoData(seed)
                        showResetDialog = false
                    }
                ) {
                    Text("Regenerate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
