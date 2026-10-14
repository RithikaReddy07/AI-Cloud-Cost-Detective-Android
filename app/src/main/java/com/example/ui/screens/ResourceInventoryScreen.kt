package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.CloudResource
import com.example.ui.components.CostFormatters
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun ResourceInventoryScreen(
    uiState: CostUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredResources = uiState.resources.filter { res ->
        searchQuery.isBlank() ||
            res.name.contains(searchQuery, ignoreCase = true) ||
            res.serviceName.contains(searchQuery, ignoreCase = true) ||
            res.resourceGroup.contains(searchQuery, ignoreCase = true) ||
            res.region.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("resource_inventory_screen")
    ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter ${uiState.resources.size} resources...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Synthetic Notice
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Cyan500, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Demo Mode: Synthetic enterprise Azure resources",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Resource List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = filteredResources,
                    key = { it.id }
                ) { res ->
                    ResourceCard(res = res, currency = uiState.preferredCurrency)
                }
        }
    }
}

@Composable
private fun ResourceCard(res: CloudResource, currency: String) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = res.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${CostFormatters.formatCurrency(res.monthlySpend, currency)}/mo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Cyan600
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${res.serviceName} • ${res.resourceGroup} • ${res.region}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (res.cpuAvgPercent != null || res.memoryAvgPercent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    res.cpuAvgPercent?.let { cpu ->
                        val isIdle = cpu < 10.0
                        Surface(
                            color = if (isIdle) AmberBgDark.copy(alpha = 0.4f) else Slate200,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "CPU: $cpu% ${if (isIdle) "(IDLE)" else ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isIdle) Amber500 else Slate800,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    res.memoryAvgPercent?.let { mem ->
                        Surface(
                            color = Slate200,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "RAM: $mem%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${res.id}",
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                maxLines = 1
            )
        }
    }
}
