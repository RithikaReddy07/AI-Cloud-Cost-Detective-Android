package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.OptimizationRecommendation
import com.example.data.model.RecommendationStatus
import com.example.ui.components.CostFormatters
import com.example.ui.components.RecommendationStatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun OptimizationScreen(
    uiState: CostUiState,
    onUpdateStatus: (String, RecommendationStatus) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = listOf("All", "Compute", "Storage", "Database")

    val filteredRecs = if (selectedCategory == null || selectedCategory == "All") {
        uiState.recommendations
    } else {
        uiState.recommendations.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("optimization_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
            // Total Savings Banner Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = EmeraldBgDark
                    ),
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
                                    text = "Identified Potential Savings",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Emerald100
                                )
                                Text(
                                    text = "${CostFormatters.formatCurrency(uiState.totalPotentialSavings, uiState.preferredCurrency)} / mo",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = Emerald500,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Backed by verified utilization baselines and official pricing tiers. No arbitrary percentages.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (cat == "All" && selectedCategory == null) || selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = if (cat == "All") null else cat
                            },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cyan500,
                                selectedLabelColor = Navy900
                            )
                        )
                    }
                }
            }

            // Recommendation Cards
            items(
                items = filteredRecs,
                key = { it.id }
            ) { rec ->
                RecommendationItemCard(
                    rec = rec,
                    currency = uiState.preferredCurrency,
                    onUpdateStatus = { st -> onUpdateStatus(rec.id, st) }
                )
            }

            // Disclaimer
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Cyan500, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Human Review Mandatory: AI Cost Detective never automatically deletes, halts, or downsizes infrastructure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
        }
    }
}

@Composable
private fun RecommendationItemCard(
    rec: OptimizationRecommendation,
    currency: String,
    onUpdateStatus: (RecommendationStatus) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rec_card_${rec.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Cyan300.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = rec.category.uppercase(),
                        color = Cyan600,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                RecommendationStatusBadge(status = rec.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rec.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${rec.serviceName} • ${rec.resourceName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation & Evidence
            Text(
                text = rec.explanation,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Savings or Unavailable Notice
            Surface(
                color = if (rec.estimatedMonthlySavings != null) EmeraldBgDark.copy(alpha = 0.5f) else AmberBgDark.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (rec.estimatedMonthlySavings != null) Icons.Default.Savings else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (rec.estimatedMonthlySavings != null) Emerald500 else Amber500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (rec.estimatedMonthlySavings != null) {
                                "Estimated Savings: ${CostFormatters.formatCurrency(rec.estimatedMonthlySavings, currency)} / month"
                            } else {
                                "Savings Estimate Unavailable"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (rec.estimatedMonthlySavings != null) Emerald500 else Amber500
                        )
                        Text(
                            text = rec.assumptions,
                            fontSize = 11.sp,
                            color = Slate300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggested Action & Risk
            Text(
                text = "Action: ${rec.suggestedAction}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Risk: ${rec.potentialRisk}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (rec.status != RecommendationStatus.ACCEPTED) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(RecommendationStatus.ACCEPTED) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (rec.status != RecommendationStatus.DISMISSED) {
                    OutlinedButton(
                        onClick = { onUpdateStatus(RecommendationStatus.DISMISSED) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Dismiss", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
