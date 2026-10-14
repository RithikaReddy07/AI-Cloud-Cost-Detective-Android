package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DateRangeOption
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun DashboardScreen(
    uiState: CostUiState,
    onSelectDateRange: (DateRangeOption) -> Unit,
    onNavigateToCosts: () -> Unit,
    onNavigateToAnomalies: () -> Unit,
    onNavigateToInvestigation: (String) -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToForecasting: () -> Unit,
    onNavigateToOptimization: () -> Unit,
    onNavigateToDataSources: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Personalized Greeting & Health Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Cyan500, Cyan600)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = Navy900,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Cloud FinOps",
                            style = MaterialTheme.typography.labelSmall,
                            color = Cyan500,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Executive Overview",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                DataSourceBadge(
                    mode = uiState.dataSourceInfo.mode,
                    isReachable = uiState.dataSourceInfo.isBackendReachable,
                    onClick = onNavigateToDataSources
                )
            }
        }

        // 2. Premium Hero Spend Card (Everyday Modern Finance style)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Slate900
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL CLOUD EXPENDITURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Slate400
                        )

                        // Delta Badge
                        val isFavorable = uiState.spendChangePct < 0
                        Surface(
                            color = if (isFavorable) EmeraldBgDark else RedBgDark,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.spendChangePct >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isFavorable) Emerald500 else Red500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${if (uiState.spendChangePct >= 0) "+" else ""}${uiState.spendChangePct}%",
                                    color = if (isFavorable) Emerald500 else Red500,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Spend Number
                    Text(
                        text = CostFormatters.formatCurrency(uiState.totalSpend, uiState.preferredCurrency),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "vs. prior period (${CostFormatters.formatCurrency(uiState.previousPeriodSpend, uiState.preferredCurrency)})",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Period Selector Pills inside Hero Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate800)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DateRangeOption.values().forEach { option ->
                            val isSelected = uiState.selectedDateRange == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Cyan500 else Color.Transparent)
                                    .clickable { onSelectDateRange(option) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Navy900 else Slate300
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Attention Alert Card (If critical or active anomalies exist)
        if (uiState.activeAnomaliesCount > 0) {
            val critical = uiState.anomalies.firstOrNull { it.status == com.example.data.model.AnomalyStatus.DETECTED }
            if (critical != null) {
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = RedBgDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToInvestigation(critical.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Red500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Red500,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Spike Detected",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Red100
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Red600, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = critical.severity.name,
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${critical.resourceName} jumped to ${CostFormatters.formatCurrency(critical.actualCost, uiState.preferredCurrency)} (+${critical.percentageDeviation}%)",
                                    fontSize = 12.sp,
                                    color = Slate200,
                                    maxLines = 1,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Investigate",
                                tint = Red100,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Everyday Quick Actions Row
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionChip(
                        icon = Icons.Default.Biotech,
                        iconColor = Violet500,
                        title = "Investigate",
                        badge = "${uiState.activeAnomaliesCount} Spikes",
                        onClick = onNavigateToAnomalies
                    )

                    QuickActionChip(
                        icon = Icons.Default.TrendingUp,
                        iconColor = Cyan500,
                        title = "Forecast",
                        badge = "30-Day Trend",
                        onClick = onNavigateToForecasting
                    )

                    QuickActionChip(
                        icon = Icons.Default.Savings,
                        iconColor = Emerald500,
                        title = "Savings",
                        badge = "${CostFormatters.formatCurrency(uiState.totalPotentialSavings, uiState.preferredCurrency)}/mo",
                        onClick = onNavigateToOptimization
                    )

                    QuickActionChip(
                        icon = Icons.Default.AutoAwesome,
                        iconColor = Amber500,
                        title = "FinOps AI",
                        badge = "Ask Assistant",
                        onClick = onNavigateToAi
                    )
                }
            }
        }

        // 5. Daily Spending Trend Chart
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Spending Trend",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap points to inspect daily variance",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onNavigateToCosts,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ArrowOutward, contentDescription = "Open Cost Explorer", tint = Cyan500)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DailyCostTrendChart(
                        dataPoints = uiState.dailySpendTrend,
                        previousPeriodPoints = uiState.previousPeriodDailyTrend,
                        currency = uiState.preferredCurrency,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 6. Spend by Cloud Service
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Spend by Service",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToCosts) {
                            Text("See All", fontSize = 12.sp, color = Cyan500)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ServiceBreakdownChart(
                        services = uiState.serviceBreakdown,
                        totalSpend = uiState.totalSpend,
                        currency = uiState.preferredCurrency,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 7. Recent Anomalies Feed
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Anomaly Incidents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToAnomalies) {
                        Text("View (${uiState.anomalies.size})", fontSize = 12.sp, color = Cyan500)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.anomalies.take(3).forEach { anom ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToInvestigation(anom.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SeverityBadge(severity = anom.severity)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = anom.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${anom.resourceName} • ${CostFormatters.formatDate(anom.detectionDate)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = CostFormatters.formatCurrency(anom.actualCost, uiState.preferredCurrency),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Red500
                                    )
                                    Text(
                                        text = "+${anom.percentageDeviation}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Red600
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(18.dp)
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
private fun QuickActionChip(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    badge: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .clickable { onClick() }
            .widthIn(min = 120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = badge,
                fontSize = 11.sp,
                color = Cyan600,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}
