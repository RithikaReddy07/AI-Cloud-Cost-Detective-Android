package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CostRecord
import com.example.ui.components.CostFormatters
import com.example.ui.components.DailyCostTrendChart
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostExplorerScreen(
    uiState: CostUiState,
    onSearchQueryChange: (String) -> Unit,
    onServiceFilterChange: (String?) -> Unit,
    onExportCsv: () -> Unit,
    onClearCsvExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedRecordForDetails by remember { mutableStateOf<CostRecord?>(null) }

    val servicesList = listOf(
        "All Services",
        "Virtual Machines",
        "Storage Accounts",
        "Azure SQL Database",
        "App Service",
        "Azure Cosmos DB",
        "Virtual Network",
        "Kubernetes Service"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cost_explorer_screen")
    ) {
        // Search & Filter Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Search Input
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search by resource, group, or region...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cost_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Service Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    servicesList.forEach { srvName ->
                        val isSelected = if (srvName == "All Services") {
                            uiState.selectedServiceFilter == null
                        } else {
                            uiState.selectedServiceFilter == srvName
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (srvName == "All Services") {
                                    onServiceFilterChange(null)
                                } else {
                                    onServiceFilterChange(if (isSelected) null else srvName)
                                }
                            },
                            label = { Text(srvName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cyan500,
                                selectedLabelColor = Navy900
                            )
                        )
                    }
                }
            }
        }

        // Summary Statistics Strip
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Filtered Expenditure", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = CostFormatters.formatCurrency(
                            uiState.filteredRecords.sumOf { it.costAmount },
                            uiState.preferredCurrency
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Cyan500
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Records", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${uiState.filteredRecords.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CSV Export Button
                FilledTonalButton(
                    onClick = onExportCsv,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("export_csv_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontSize = 12.sp)
                }
            }
        }

        // Detailed Cost Records List
        if (uiState.filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No cost records match the current filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.filteredRecords.sortedByDescending { it.usageDate },
                    key = { it.id }
                ) { record ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRecordForDetails = record }
                            .testTag("cost_record_${record.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = CostFormatters.formatDate(record.usageDate),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Cyan600
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${record.serviceName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = record.resourceName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )

                                Text(
                                    text = "${record.resourceGroup} • ${record.region} • ${record.usageQuantity} ${record.unitOfMeasure}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CostFormatters.formatCurrency(record.costAmount, uiState.preferredCurrency),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = record.currency,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Record Detail Modal Bottom Sheet
    selectedRecordForDetails?.let { record ->
        ModalBottomSheet(
            onDismissRequest = { selectedRecordForDetails = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Cost Record Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                DetailRow("Resource Name", record.resourceName)
                DetailRow("Service Name", record.serviceName)
                DetailRow("Resource Group", record.resourceGroup)
                DetailRow("Region", record.region)
                DetailRow("Usage Date", CostFormatters.formatDateFull(record.usageDate))
                DetailRow("Usage Quantity", "${record.usageQuantity} ${record.unitOfMeasure}")
                DetailRow("Cost Amount", CostFormatters.formatCurrency(record.costAmount, uiState.preferredCurrency))
                DetailRow("Data Source", record.source)
                DetailRow("Subscription ID", record.subscriptionId)
                DetailRow("Full Resource URI", record.resourceId)

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { selectedRecordForDetails = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }

    // Export CSV Dialog Preview
    uiState.exportCsvContent?.let { csvText ->
        AlertDialog(
            onDismissRequest = onClearCsvExport,
            title = { Text("Exported Cost Records (CSV)") },
            text = {
                Column {
                    Text("Generated ${uiState.filteredRecords.size} rows formatted to standard FinOps specification.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = csvText.take(400) + "...",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Cost Detective CSV", csvText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied CSV to clipboard", Toast.LENGTH_SHORT).show()
                        onClearCsvExport()
                    }
                ) {
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = onClearCsvExport) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}
