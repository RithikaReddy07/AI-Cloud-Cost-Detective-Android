package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import com.example.ui.components.CostFormatters
import com.example.ui.components.DataSourceBadge
import com.example.ui.theme.*
import com.example.viewmodel.CostUiState

@Composable
fun DataSourcesScreen(
    uiState: CostUiState,
    onTestBackend: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var backendUrlInput by remember { mutableStateOf(uiState.dataSourceInfo.backendUrl) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("data_sources_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // 1. Active Data Source Card
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
                                text = "Active Data Source",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            DataSourceBadge(
                                mode = uiState.dataSourceInfo.mode,
                                isReachable = uiState.dataSourceInfo.isBackendReachable
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow("Dataset Name", uiState.dataSourceInfo.datasetName)
                        InfoRow("Record Count", "${uiState.dataSourceInfo.recordCount} records")
                        InfoRow("Span", "${CostFormatters.formatDateFull(uiState.dataSourceInfo.dateRangeStart)} to ${CostFormatters.formatDateFull(uiState.dataSourceInfo.dateRangeEnd)}")
                        InfoRow("Random Seed", "${uiState.dataSourceInfo.seed}")
                        InfoRow("Status", uiState.dataSourceInfo.lastSyncTime)
                    }
                }
            }

            // 2. Existing FastAPI Backend Integration Panel
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sensors, contentDescription = null, tint = Cyan500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FastAPI Backend Connection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connects to your existing Python 3.11 / FastAPI / SQLAlchemy backend on port 8000. Use http://10.0.2.2:8000 for standard Android emulator.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = backendUrlInput,
                            onValueChange = { backendUrlInput = it },
                            label = { Text("FastAPI Server Base URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onTestBackend(backendUrlInput) },
                            enabled = !uiState.isBackendTesting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isBackendTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Navy900
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pinging GET /api/health...")
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Test Backend Connection", fontWeight = FontWeight.Bold)
                            }
                        }

                        uiState.backendTestSuccess?.let { success ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = if (success) EmeraldBgDark else RedBgDark,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (success) Emerald500 else Red500,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (success) "FastAPI backend reachable! Live data enabled." else "Backend unreachable. Demo Mode continues smoothly.",
                                        fontSize = 12.sp,
                                        color = if (success) Emerald100 else Red100
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Future Azure Integration Architecture
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Slate400)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Future Azure Integration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Slate800,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Azure connection not configured — Demo Mode is active",
                                color = Cyan400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "When ready to connect real Azure subscriptions, the architecture is pre-wired to ingest from:\n" +
                                "1. Azure Cost Management REST API (Exports & Query)\n" +
                                "2. Azure Resource Graph (Topology mapping)\n" +
                                "3. Azure Monitor Metrics (vCore, CPU, and Disk metrics)\n" +
                                "4. Azure Activity Log (Audit events & deployments)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Azure credentials or credit cards are required to test or evaluate this project.",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                    }
                }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
