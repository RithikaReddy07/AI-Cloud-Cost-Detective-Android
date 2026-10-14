package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CostFormatters
import com.example.ui.components.ForecastTrendChart
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun ForecastingScreen(
    uiState: CostUiState,
    onSelectHorizon: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forecast = uiState.activeForecast

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("forecasting_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizon Selector
        item {
            Column {
                Text(
                    text = "Forecast Horizon",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf(7, 30, 90).forEachIndexed { index, days ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                            onClick = { onSelectHorizon(days) },
                            selected = uiState.forecastHorizon == days,
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Cyan500,
                                activeContentColor = Navy900
                            )
                        ) {
                            Text("$days Days", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Summary Metrics Grid
        if (forecast != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Projected Spend",
                        value = CostFormatters.formatCurrency(forecast.projectedTotalCost, uiState.preferredCurrency),
                        deltaText = "${if (forecast.projectedChangePercent >= 0) "+" else ""}${forecast.projectedChangePercent}%",
                        isDeltaPositiveBad = true,
                        isDeltaPositive = forecast.projectedChangePercent >= 0,
                        subtitle = "vs prior ${forecast.horizonDays}d",
                        icon = Icons.Default.Timeline,
                        iconTint = Violet500,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Baseline Spend",
                        value = CostFormatters.formatCurrency(forecast.baselinePeriodTotalCost, uiState.preferredCurrency),
                        subtitle = "Prior ${forecast.horizonDays}d recorded",
                        icon = Icons.Default.History,
                        iconTint = Cyan500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive Canvas Forecast Chart
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
                                text = "Projection with 90% Confidence Interval",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ForecastTrendChart(
                            points = forecast.points,
                            currency = uiState.preferredCurrency,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Methodology & Assumptions
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Functions, contentDescription = null, tint = Cyan500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Explainable Model Architecture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Model: ${forecast.method}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = forecast.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Important Disclaimers:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Forecasts are statistical extrapolations based on historical trends, not guaranteed future provider bills.\n• Estimates assume no abrupt infrastructure provisioning, deallocation, or major cloud architectural changes.\n• Actual billing fluctuates with user traffic volume and dynamic scaling rules.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
