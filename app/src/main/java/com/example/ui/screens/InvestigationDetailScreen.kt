package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnomalyStatus
import com.example.data.model.InvestigationReport
import com.example.ui.components.CostFormatters
import com.example.ui.components.DailyCostTrendChart
import com.example.ui.components.SeverityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun InvestigationDetailScreen(
    report: InvestigationReport,
    currency: String,
    onBack: () -> Unit,
    onUpdateStatus: (String, AnomalyStatus, String?) -> Unit,
    onAskAiAboutAnomaly: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNoteText by remember { mutableStateOf(report.anomaly.notes) }
    var currentStatus by remember { mutableStateOf(report.anomaly.status) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("investigation_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header: Anomaly Info & Case File Badge
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
                        SeverityBadge(severity = report.anomaly.severity)
                        Text(
                            text = CostFormatters.formatDateFull(report.anomaly.detectionDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = report.anomaly.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${report.anomaly.serviceName} • ${report.anomaly.resourceName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Picker Row
                    Text("Investigation Status:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnomalyStatus.values().forEach { st ->
                            val isSelected = currentStatus == st
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    currentStatus = st
                                    onUpdateStatus(report.anomalyId, st, null)
                                },
                                label = { Text(st.name.replace("_", " "), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Cyan500,
                                    selectedLabelColor = Navy900
                                )
                            )
                        }
                    }

                    if (report.anomaly.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.StickyNote2, contentDescription = null, tint = Amber500, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Note: ${report.anomaly.notes}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section A: What Happened?
        item {
            InvestigationSectionCard(title = "Section A: What Happened?", initiallyExpanded = true) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Baseline Cost", style = MaterialTheme.typography.labelSmall)
                            Text(
                                CostFormatters.formatCurrency(report.baselineCost, currency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Observed Spend", style = MaterialTheme.typography.labelSmall)
                            Text(
                                CostFormatters.formatCurrency(report.anomaly.actualCost, currency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Red500
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Absolute Jump", style = MaterialTheme.typography.labelSmall)
                            Text(
                                "+${CostFormatters.formatCurrency(report.anomaly.absoluteDeviation, currency)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Red600
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Statistical Method: ${report.anomaly.detectionMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section B: Cost Evidence Chart
        item {
            InvestigationSectionCard(title = "Section B: Cost Evidence (Historical Trend)", initiallyExpanded = true) {
                DailyCostTrendChart(
                    dataPoints = report.historicalDailyCosts,
                    currency = currency,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section C: Possible Causes (Hypotheses)
        item {
            InvestigationSectionCard(
                title = "Section C: Plausible Hypotheses",
                badge = "Hypotheses",
                initiallyExpanded = true
            ) {
                report.possibleCauses.forEach { cause ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Violet500,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = cause,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Section D: Evidence Quality Breakdown
        item {
            InvestigationSectionCard(title = "Section D: Evidence Quality Breakdown", initiallyExpanded = true) {
                EvidenceGroup("1. Observed Facts (Verified)", report.observedFacts, Emerald500, Icons.Default.Check)
                Spacer(modifier = Modifier.height(8.dp))
                EvidenceGroup("2. Calculated Comparisons", report.calculatedComparisons, Cyan500, Icons.Default.Calculate)
                Spacer(modifier = Modifier.height(8.dp))
                EvidenceGroup("3. Missing Evidence (Not Available in Demo)", report.missingEvidence, Amber500, Icons.Default.WarningAmber)
                Spacer(modifier = Modifier.height(8.dp))
                EvidenceGroup("4. Recommended Next Steps", report.recommendedNextSteps, Violet500, Icons.Default.FactCheck)
            }
        }

        // Section E: Investigation Timeline
        item {
            InvestigationSectionCard(title = "Section E: Incident Timeline", initiallyExpanded = false) {
                report.timeline.forEachIndexed { index, event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (event.isSimulated) Slate400 else Cyan500)
                            )
                            if (index < report.timeline.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(34.dp)
                                        .background(Slate300)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (event.isSimulated) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = Slate200, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            "Simulated",
                                            fontSize = 9.sp,
                                            color = Slate600,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${event.timestamp} — ${event.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section F: Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onAskAiAboutAnomaly("Explain the anomaly for ${report.anomaly.resourceName} detected on ${report.anomaly.detectionDate}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy900),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ask AI Analyst to Explain This Incident", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showNoteDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add / Edit Investigation Notes")
                }
            }
        }
    }

    // Add Note Dialog
    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Investigation Note") },
            text = {
                OutlinedTextField(
                    value = currentNoteText,
                    onValueChange = { currentNoteText = it },
                    placeholder = { Text("Record hypothesis, root cause verified, or ticket...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStatus(report.anomalyId, currentStatus, currentNoteText)
                        showNoteDialog = false
                    }
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InvestigationSectionCard(
    title: String,
    badge: String? = null,
    initiallyExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Violet100, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = badge,
                                color = Violet500,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Slate400
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun EvidenceGroup(
    title: String,
    items: List<String>,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(15.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
