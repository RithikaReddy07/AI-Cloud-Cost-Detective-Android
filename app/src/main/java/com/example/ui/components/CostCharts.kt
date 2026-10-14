package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ForecastPoint
import com.example.ui.theme.*
import java.time.LocalDate

@Composable
fun DailyCostTrendChart(
    dataPoints: List<Pair<LocalDate, Double>>,
    previousPeriodPoints: List<Pair<LocalDate, Double>> = emptyList(),
    currency: String = "USD",
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No spend data in selected range", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxCost = (dataPoints.maxOfOrNull { it.second } ?: 100.0)
        .coerceAtLeast(previousPeriodPoints.maxOfOrNull { it.second } ?: 0.0) * 1.15
    val minCost = 0.0

    Column(modifier = modifier) {
        // Selected point readout tooltip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndex != null && selectedIndex!! in dataPoints.indices) {
                val pt = dataPoints[selectedIndex!!]
                val prevPt = previousPeriodPoints.getOrNull(selectedIndex!!)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CostFormatters.formatDate(pt.first) + ": ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CostFormatters.formatCurrency(pt.second, currency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Cyan400
                    )
                    if (prevPt != null) {
                        Text(
                            text = " (Prev: ${CostFormatters.formatCurrency(prevPt.second, currency)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Cyan500))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Current Period", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (previousPeriodPoints.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Slate400))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous Period", style = MaterialTheme.typography.labelSmall, color = Slate400)
                    }
                }
                Text("Tap chart to inspect", style = MaterialTheme.typography.labelSmall, color = Slate400)
            }
        }

        // Canvas Area
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .pointerInput(dataPoints) {
                    detectTapGestures(
                        onPress = { offset ->
                            val widthPerPoint = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                            val idx = (offset.x / widthPerPoint).toInt().coerceIn(0, dataPoints.size - 1)
                            selectedIndex = idx
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val n = dataPoints.size

            if (n < 2) return@Canvas

            val stepX = width / (n - 1)
            val rangeY = (maxCost - minCost).coerceAtLeast(1.0)

            // Grid lines
            val gridLines = 3
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Previous Period dashed line if present
            if (previousPeriodPoints.size >= 2) {
                val prevPath = Path()
                val prevN = previousPeriodPoints.size
                val prevStepX = width / (prevN - 1)
                previousPeriodPoints.forEachIndexed { i, pt ->
                    val x = i * prevStepX
                    val y = height - ((pt.second - minCost) / rangeY * height).toFloat()
                    if (i == 0) prevPath.moveTo(x, y) else prevPath.lineTo(x, y)
                }
                drawPath(
                    path = prevPath,
                    color = Slate400.copy(alpha = 0.7f),
                    style = Stroke(
                        width = 1.8.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }

            // Build Path for Current Period
            val path = Path()
            val fillPath = Path()
            fillPath.moveTo(0f, height)

            val pointOffsets = mutableListOf<Offset>()

            dataPoints.forEachIndexed { i, pt ->
                val x = i * stepX
                val y = height - ((pt.second - minCost) / rangeY * height).toFloat()
                pointOffsets.add(Offset(x, y))

                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    // Smooth cubic bezier
                    val prev = pointOffsets[i - 1]
                    val controlX1 = prev.x + (x - prev.x) / 2
                    val controlY1 = prev.y
                    val controlX2 = prev.x + (x - prev.x) / 2
                    val controlY2 = y
                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Fill gradient under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Cyan500.copy(alpha = 0.35f), Cyan500.copy(alpha = 0.02f)),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Stroke line
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(Cyan400, CyanGlow)
                ),
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw indicator point if selected
            selectedIndex?.let { idx ->
                if (idx in pointOffsets.indices) {
                    val ptOffset = pointOffsets[idx]
                    // Vertical guide line
                    drawLine(
                        color = Cyan400.copy(alpha = 0.5f),
                        start = Offset(ptOffset.x, 0f),
                        end = Offset(ptOffset.x, height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                    // Glow circle
                    drawCircle(
                        color = CyanGlow,
                        radius = 6.dp.toPx(),
                        center = ptOffset
                    )
                    drawCircle(
                        color = Navy900,
                        radius = 3.dp.toPx(),
                        center = ptOffset
                    )
                }
            }
        }

        // Date range axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = CostFormatters.formatDate(dataPoints.first().first),
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )
            if (dataPoints.size > 2) {
                Text(
                    text = CostFormatters.formatDate(dataPoints[dataPoints.size / 2].first),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400
                )
            }
            Text(
                text = CostFormatters.formatDate(dataPoints.last().first),
                style = MaterialTheme.typography.labelSmall,
                color = Slate400
            )
        }
    }
}

@Composable
fun ServiceBreakdownChart(
    services: List<Pair<String, Double>>,
    totalSpend: Double,
    currency: String = "USD",
    modifier: Modifier = Modifier
) {
    val serviceColors = listOf(
        Cyan500,
        Violet500,
        Emerald500,
        Amber500,
        Color(0xFFEC4899),
        Color(0xFF3B82F6),
        Color(0xFF14B8A6)
    )

    Column(modifier = modifier) {
        services.take(5).forEachIndexed { index, (serviceName, cost) ->
            val color = serviceColors[index % serviceColors.size]
            val pct = if (totalSpend > 0) ((cost / totalSpend) * 100.0) else 0.0

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = serviceName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "${CostFormatters.formatCurrency(cost, currency)} (${String.format("%.1f", pct)}%)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((pct / 100.0).toFloat().coerceIn(0.01f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
fun ForecastTrendChart(
    points: List<ForecastPoint>,
    currency: String = "USD",
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxCost = (points.maxOfOrNull { it.upperBound } ?: 100.0) * 1.15
    val minCost = 0.0

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height
            val n = points.size
            if (n < 2) return@Canvas

            val stepX = width / (n - 1)
            val rangeY = (maxCost - minCost).coerceAtLeast(1.0)

            // Grid lines
            for (i in 0..3) {
                val y = height * (i.toFloat() / 3)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.12f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw 90% Confidence Interval Band for forecast section
            val forecastIndices = points.indices.filter { points[it].isForecast }
            if (forecastIndices.isNotEmpty()) {
                val bandPath = Path()
                // Top curve of band
                forecastIndices.forEachIndexed { i, idx ->
                    val pt = points[idx]
                    val x = idx * stepX
                    val y = height - ((pt.upperBound - minCost) / rangeY * height).toFloat()
                    if (i == 0) bandPath.moveTo(x, y) else bandPath.lineTo(x, y)
                }
                // Bottom curve of band in reverse
                for (i in forecastIndices.size - 1 downTo 0) {
                    val idx = forecastIndices[i]
                    val pt = points[idx]
                    val x = idx * stepX
                    val y = height - ((pt.lowerBound - minCost) / rangeY * height).toFloat()
                    bandPath.lineTo(x, y)
                }
                bandPath.close()

                drawPath(
                    path = bandPath,
                    color = Violet500.copy(alpha = 0.18f)
                )
            }

            // Draw line
            val histPath = Path()
            val fcPath = Path()
            var startedFc = false

            points.forEachIndexed { i, pt ->
                val x = i * stepX
                val y = height - ((pt.cost - minCost) / rangeY * height).toFloat()

                if (!pt.isForecast) {
                    if (i == 0) histPath.moveTo(x, y) else histPath.lineTo(x, y)
                } else {
                    if (!startedFc) {
                        // Connect last hist to first fc
                        val prev = points[i - 1]
                        val prevX = (i - 1) * stepX
                        val prevY = height - ((prev.cost - minCost) / rangeY * height).toFloat()
                        fcPath.moveTo(prevX, prevY)
                        startedFc = true
                    }
                    fcPath.lineTo(x, y)
                }
            }

            // Draw historical solid line
            drawPath(
                path = histPath,
                color = Cyan500,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw forecast dashed line
            drawPath(
                path = fcPath,
                color = Violet500,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // Draw Today vertical dividing line
            val firstFcIdx = points.indexOfFirst { it.isForecast }
            if (firstFcIdx > 0) {
                val divideX = (firstFcIdx - 1) * stepX
                drawLine(
                    color = Amber500,
                    start = Offset(divideX, 0f),
                    end = Offset(divideX, height),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Cyan500))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Historical", style = MaterialTheme.typography.labelSmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Violet500))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Projected Trend", style = MaterialTheme.typography.labelSmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Violet500.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("90% Prediction Range", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
