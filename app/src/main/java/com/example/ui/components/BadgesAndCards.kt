package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.AnomalySeverity
import com.example.data.model.AnomalyStatus
import com.example.data.model.DataSourceMode
import com.example.data.model.RecommendationStatus
import com.example.ui.theme.*

@Composable
fun SeverityBadge(
    severity: AnomalySeverity,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (severity) {
        AnomalySeverity.CRITICAL -> Triple(Red100, Red600, Icons.Default.Warning)
        AnomalySeverity.HIGH -> Triple(Amber100, Amber600, Icons.Default.PriorityHigh)
        AnomalySeverity.MEDIUM -> Triple(Violet100, Violet500, Icons.Default.Info)
        AnomalySeverity.LOW -> Triple(Slate200, Slate700, Icons.Default.CheckCircle)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.testTag("badge_severity_${severity.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = severity.name,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = severity.name,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: AnomalyStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        AnomalyStatus.DETECTED -> Pair(Red100, Red600)
        AnomalyStatus.INVESTIGATING -> Pair(Amber100, Amber600)
        AnomalyStatus.REVIEWED -> Pair(Violet100, Violet500)
        AnomalyStatus.RESOLVED -> Pair(Emerald100, Emerald600)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.testTag("badge_status_${status.name.lowercase()}")
    ) {
        Text(
            text = status.name.replace("_", " "),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RecommendationStatusBadge(
    status: RecommendationStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        RecommendationStatus.NEW -> Pair(Cyan300.copy(alpha = 0.25f), Cyan600)
        RecommendationStatus.REVIEWED -> Pair(Amber100, Amber600)
        RecommendationStatus.ACCEPTED -> Pair(Emerald100, Emerald600)
        RecommendationStatus.DISMISSED -> Pair(Slate200, Slate600)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status.name,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun DataSourceBadge(
    mode: DataSourceMode,
    isReachable: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (label, bg, fg) = when (mode) {
        DataSourceMode.DEMO_MODE -> Triple("Demo Mode (Synthetic)", Slate800, Cyan400)
        DataSourceMode.FASTAPI_BACKEND -> Triple(
            if (isReachable) "FastAPI Live" else "FastAPI (Offline)",
            if (isReachable) EmeraldBgDark else RedBgDark,
            if (isReachable) Emerald500 else Red500
        )
        DataSourceMode.OFFLINE_CACHE -> Triple("Offline Cache", Slate800, Amber500)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, fg.copy(alpha = 0.4f)),
        modifier = modifier
            .testTag("data_source_badge")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = fg,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    deltaText: String? = null,
    isDeltaPositiveBad: Boolean = true,
    isDeltaPositive: Boolean = true,
    icon: ImageVector,
    iconTint: Color = Cyan500,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (deltaText != null || subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (deltaText != null) {
                        val isFavorable = if (isDeltaPositiveBad) !isDeltaPositive else isDeltaPositive
                        val deltaColor = if (isFavorable) Emerald600 else Red600
                        val deltaIcon = if (isDeltaPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(deltaColor.copy(alpha = 0.1f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = deltaIcon,
                                contentDescription = null,
                                tint = deltaColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = deltaText,
                                color = deltaColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
