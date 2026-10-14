package com.example.data.model

import java.time.LocalDate

enum class AnomalySeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class AnomalyStatus {
    DETECTED,
    INVESTIGATING,
    REVIEWED,
    RESOLVED
}

enum class RecommendationStatus {
    NEW,
    REVIEWED,
    ACCEPTED,
    DISMISSED
}

enum class DataSourceMode {
    DEMO_MODE,
    FASTAPI_BACKEND,
    OFFLINE_CACHE
}

enum class DateRangeOption(val label: String, val days: Int) {
    LAST_7_DAYS("7 Days", 7),
    LAST_30_DAYS("30 Days", 30),
    LAST_90_DAYS("90 Days", 90)
}

data class CostRecord(
    val id: String,
    val usageDate: LocalDate,
    val subscriptionId: String = "sub-azure-demo-001",
    val resourceId: String,
    val resourceName: String,
    val resourceGroup: String,
    val serviceName: String,
    val region: String,
    val usageQuantity: Double,
    val unitOfMeasure: String,
    val costAmount: Double,
    val currency: String = "USD",
    val source: String = "Demo Generator"
)

data class AnomalyRecord(
    val id: String,
    val title: String,
    val detectionDate: LocalDate,
    val serviceName: String,
    val resourceId: String,
    val resourceName: String,
    val actualCost: Double,
    val baselineCost: Double,
    val absoluteDeviation: Double,
    val percentageDeviation: Double,
    val severity: AnomalySeverity,
    val status: AnomalyStatus = AnomalyStatus.DETECTED,
    val notes: String = "",
    val detectionMethod: String = "Rolling 14-Day Z-Score (>2.0σ)"
)

data class InvestigationTimelineEvent(
    val timestamp: String,
    val title: String,
    val description: String,
    val isSimulated: Boolean = true
)

data class InvestigationReport(
    val anomalyId: String,
    val anomaly: AnomalyRecord,
    val observedFacts: List<String>,
    val calculatedComparisons: List<String>,
    val possibleCauses: List<String>,
    val missingEvidence: List<String>,
    val recommendedNextSteps: List<String>,
    val timeline: List<InvestigationTimelineEvent>,
    val historicalDailyCosts: List<Pair<LocalDate, Double>>, // date to cost
    val baselineCost: Double
)

data class ForecastPoint(
    val date: LocalDate,
    val cost: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val isForecast: Boolean
)

data class ForecastResult(
    val horizonDays: Int,
    val method: String,
    val points: List<ForecastPoint>,
    val projectedTotalCost: Double,
    val baselinePeriodTotalCost: Double,
    val projectedChangePercent: Double,
    val explanation: String
)

data class OptimizationRecommendation(
    val id: String,
    val title: String,
    val category: String, // Compute, Storage, Database, Networking, Idle
    val serviceName: String,
    val resourceName: String,
    val resourceId: String,
    val explanation: String,
    val suggestedAction: String,
    val potentialRisk: String,
    val estimatedMonthlySavings: Double?, // null if unavailable
    val assumptions: String,
    val confidenceQuality: String, // High, Medium, Low
    val status: RecommendationStatus = RecommendationStatus.NEW
)

data class CloudResource(
    val id: String,
    val name: String,
    val type: String,
    val serviceName: String,
    val resourceGroup: String,
    val region: String,
    val monthlySpend: Double,
    val cpuAvgPercent: Double? = null,
    val memoryAvgPercent: Double? = null,
    val status: String = "Running",
    val isDemo: Boolean = true
)

data class DataSourceInfo(
    val mode: DataSourceMode = DataSourceMode.DEMO_MODE,
    val datasetName: String = "Azure Enterprise Synthetic Benchmark",
    val recordCount: Int = 0,
    val dateRangeStart: LocalDate = LocalDate.now().minusDays(90),
    val dateRangeEnd: LocalDate = LocalDate.now(),
    val seed: Long = 42L,
    val backendUrl: String = "http://10.0.2.2:8000",
    val isBackendReachable: Boolean = false,
    val azureConfigured: Boolean = false,
    val lastSyncTime: String = "Just now"
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val citedSources: List<String> = emptyList(),
    val isDeterministicCalculation: Boolean = false
)
