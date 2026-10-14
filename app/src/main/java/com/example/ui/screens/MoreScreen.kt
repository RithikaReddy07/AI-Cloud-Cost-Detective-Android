package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    onNavigateToForecasting: () -> Unit,
    onNavigateToOptimization: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToDataSources: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Advanced Tools & Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "FinOps investigations, forecasting, and data management",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Timeline,
                iconColor = Violet500,
                title = "Cost Forecasting",
                subtitle = "7, 30, and 90-day statistical spending projections with prediction intervals",
                onClick = onNavigateToForecasting
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Savings,
                iconColor = Emerald500,
                title = "Optimization Recommendations",
                subtitle = "Evidence-based rightsizing, idle resource scheduling, and lifecycle rules",
                onClick = onNavigateToOptimization
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Dns,
                iconColor = Cyan500,
                title = "Cloud Resource Inventory",
                subtitle = "Explore 12 enterprise Azure resources, utilization telemetry, and costs",
                onClick = onNavigateToResources
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Storage,
                iconColor = Amber500,
                title = "Data Sources & Azure Connection",
                subtitle = "Manage active synthetic demo dataset, Azure readiness, and FastAPI backend",
                onClick = onNavigateToDataSources
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Tune,
                iconColor = Slate500,
                title = "Settings & Sensitivity",
                subtitle = "Currency format, statistical anomaly sensitivity (σ), and project info",
                onClick = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun MoreMenuCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Slate400
            )
        }
    }
}
