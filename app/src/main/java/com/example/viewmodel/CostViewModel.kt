package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CostRepository
import com.example.data.repository.CostRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CostUiState(
    val selectedDateRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    val selectedServiceFilter: String? = null,
    val searchQuery: String = "",
    val selectedSeverityFilter: AnomalySeverity? = null,
    val selectedAnomalyStatusFilter: AnomalyStatus? = null,
    val preferredCurrency: String = "USD",
    val anomalySensitivity: Double = 2.0,
    val allRecords: List<CostRecord> = emptyList(),
    val filteredRecords: List<CostRecord> = emptyList(),
    val totalSpend: Double = 0.0,
    val previousPeriodSpend: Double = 0.0,
    val spendChangePct: Double = 0.0,
    val highestCostService: String = "None",
    val serviceBreakdown: List<Pair<String, Double>> = emptyList(),
    val dailySpendTrend: List<Pair<LocalDate, Double>> = emptyList(),
    val previousPeriodDailyTrend: List<Pair<LocalDate, Double>> = emptyList(),
    val anomalies: List<AnomalyRecord> = emptyList(),
    val activeAnomaliesCount: Int = 0,
    val recommendations: List<OptimizationRecommendation> = emptyList(),
    val totalPotentialSavings: Double = 0.0,
    val resources: List<CloudResource> = emptyList(),
    val dataSourceInfo: DataSourceInfo = DataSourceInfo(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val isChatLoading: Boolean = false,
    val selectedInvestigation: InvestigationReport? = null,
    val activeForecast: ForecastResult? = null,
    val forecastHorizon: Int = 30,
    val exportCsvContent: String? = null,
    val isBackendTesting: Boolean = false,
    val backendTestSuccess: Boolean? = null,
    val statusMessage: String? = null
)

class CostViewModel(
    private val repository: CostRepository = CostRepositoryImpl()
) : ViewModel() {

    private val _selectedDateRange = MutableStateFlow(DateRangeOption.LAST_30_DAYS)
    private val _selectedServiceFilter = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedSeverityFilter = MutableStateFlow<AnomalySeverity?>(null)
    private val _selectedAnomalyStatusFilter = MutableStateFlow<AnomalyStatus?>(null)
    private val _selectedInvestigation = MutableStateFlow<InvestigationReport?>(null)
    private val _forecastHorizon = MutableStateFlow(30)
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _isChatLoading = MutableStateFlow(false)
    private val _exportCsvContent = MutableStateFlow<String?>(null)
    private val _isBackendTesting = MutableStateFlow(false)
    private val _backendTestSuccess = MutableStateFlow<Boolean?>(null)
    private val _statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CostUiState> = combine(
        repository.recordsFlow,
        repository.anomaliesFlow,
        repository.recommendationsFlow,
        repository.resourcesFlow,
        repository.dataSourceFlow,
        repository.preferredCurrency,
        repository.anomalySensitivity,
        _selectedDateRange,
        _selectedServiceFilter,
        _searchQuery,
        _selectedSeverityFilter,
        _selectedAnomalyStatusFilter,
        _selectedInvestigation,
        _forecastHorizon,
        _chatMessages,
        _isChatLoading,
        _exportCsvContent,
        _isBackendTesting,
        _backendTestSuccess,
        _statusMessage
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val records = args[0] as List<CostRecord>
        @Suppress("UNCHECKED_CAST")
        val anomalies = args[1] as List<AnomalyRecord>
        @Suppress("UNCHECKED_CAST")
        val recommendations = args[2] as List<OptimizationRecommendation>
        @Suppress("UNCHECKED_CAST")
        val resources = args[3] as List<CloudResource>
        val dataSource = args[4] as DataSourceInfo
        val currency = args[5] as String
        val sensitivity = args[6] as Double
        val dateRange = args[7] as DateRangeOption
        val serviceFilter = args[8] as String?
        val query = args[9] as String
        val severityFilter = args[10] as AnomalySeverity?
        val statusFilter = args[11] as AnomalyStatus?
        val investigation = args[12] as InvestigationReport?
        val horizon = args[13] as Int
        @Suppress("UNCHECKED_CAST")
        val messages = args[14] as List<ChatMessage>
        val chatLoading = args[15] as Boolean
        val csvContent = args[16] as String?
        val backendTesting = args[17] as Boolean
        val backendSuccess = args[18] as Boolean?
        val statusMsg = args[19] as String?

        // Compute metrics for selected date range
        val today = LocalDate.now()
        val startDate = today.minusDays((dateRange.days - 1).toLong())
        val prevStartDate = startDate.minusDays(dateRange.days.toLong())
        val prevEndDate = startDate.minusDays(1)

        val currentPeriodRecords = records.filter { !it.usageDate.isBefore(startDate) && !it.usageDate.isAfter(today) }
        val prevPeriodRecords = records.filter { !it.usageDate.isBefore(prevStartDate) && !it.usageDate.isAfter(prevEndDate) }

        // Filtered records for Cost Explorer
        val filtered = currentPeriodRecords.filter { record ->
            val matchesService = serviceFilter == null || record.serviceName == serviceFilter
            val matchesQuery = query.isBlank() ||
                record.resourceName.contains(query, ignoreCase = true) ||
                record.resourceGroup.contains(query, ignoreCase = true) ||
                record.region.contains(query, ignoreCase = true) ||
                record.serviceName.contains(query, ignoreCase = true)
            matchesService && matchesQuery
        }

        val totalSpend = currentPeriodRecords.sumOf { it.costAmount }
        val prevSpend = prevPeriodRecords.sumOf { it.costAmount }
        val spendChangePct = if (prevSpend > 0) ((totalSpend - prevSpend) / prevSpend) * 100.0 else 0.0

        val serviceMap = currentPeriodRecords.groupBy { it.serviceName }
            .mapValues { it.value.sumOf { r -> r.costAmount } }
            .toList()
            .sortedByDescending { it.second }

        val highestService = serviceMap.firstOrNull()?.first ?: "None"

        val dailyTrend = currentPeriodRecords.groupBy { it.usageDate }
            .mapValues { it.value.sumOf { r -> r.costAmount } }
            .toList()
            .sortedBy { it.first }

        val prevDailyTrend = prevPeriodRecords.groupBy { it.usageDate }
            .mapValues { it.value.sumOf { r -> r.costAmount } }
            .toList()
            .sortedBy { it.first }

        // Filtered anomalies
        val filteredAnomalies = anomalies.filter { anom ->
            val matchesDate = !anom.detectionDate.isBefore(startDate)
            val matchesSeverity = severityFilter == null || anom.severity == severityFilter
            val matchesStatus = statusFilter == null || anom.status == statusFilter
            val matchesService = serviceFilter == null || anom.serviceName == serviceFilter
            matchesDate && matchesSeverity && matchesStatus && matchesService
        }

        val activeAnomaliesCount = anomalies.count { it.status == AnomalyStatus.DETECTED || it.status == AnomalyStatus.INVESTIGATING }
        val totalSavings = recommendations.mapNotNull { it.estimatedMonthlySavings }.sum()

        // Generate forecast
        val forecast = repository.generateForecast(horizon)

        CostUiState(
            selectedDateRange = dateRange,
            selectedServiceFilter = serviceFilter,
            searchQuery = query,
            selectedSeverityFilter = severityFilter,
            selectedAnomalyStatusFilter = statusFilter,
            preferredCurrency = currency,
            anomalySensitivity = sensitivity,
            allRecords = records,
            filteredRecords = filtered,
            totalSpend = Math.round(totalSpend * 100.0) / 100.0,
            previousPeriodSpend = Math.round(prevSpend * 100.0) / 100.0,
            spendChangePct = Math.round(spendChangePct * 10.0) / 10.0,
            highestCostService = highestService,
            serviceBreakdown = serviceMap,
            dailySpendTrend = dailyTrend,
            previousPeriodDailyTrend = prevDailyTrend,
            anomalies = filteredAnomalies,
            activeAnomaliesCount = activeAnomaliesCount,
            recommendations = recommendations,
            totalPotentialSavings = Math.round(totalSavings * 100.0) / 100.0,
            resources = resources,
            dataSourceInfo = dataSource,
            chatMessages = messages,
            isChatLoading = chatLoading,
            selectedInvestigation = investigation,
            activeForecast = forecast,
            forecastHorizon = horizon,
            exportCsvContent = csvContent,
            isBackendTesting = backendTesting,
            backendTestSuccess = backendSuccess,
            statusMessage = statusMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CostUiState()
    )

    init {
        // Initial friendly greeting message in AI Analyst
        _chatMessages.value = listOf(
            ChatMessage(
                id = "ai-welcome",
                isUser = false,
                text = "Welcome to **AI Cloud Cost Detective**. I am your FinOps analytics assistant.\n\nAsk me about recent spending increases, top cloud services, detected anomalies, or cost forecasts.",
                timestamp = "Now",
                citedSources = listOf("Cost Detective Core Engine"),
                isDeterministicCalculation = true
            )
        )
    }

    fun setDateRange(range: DateRangeOption) {
        _selectedDateRange.value = range
    }

    fun setServiceFilter(service: String?) {
        _selectedServiceFilter.value = service
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSeverityFilter(severity: AnomalySeverity?) {
        _selectedSeverityFilter.value = severity
    }

    fun setAnomalyStatusFilter(status: AnomalyStatus?) {
        _selectedAnomalyStatusFilter.value = status
    }

    fun setForecastHorizon(days: Int) {
        _forecastHorizon.value = days
    }

    fun setCurrency(currency: String) {
        repository.setCurrency(currency)
    }

    fun setAnomalySensitivity(sigmas: Double) {
        repository.setAnomalySensitivity(sigmas)
    }

    fun openInvestigation(anomalyId: String) {
        val report = repository.getInvestigationReport(anomalyId)
        _selectedInvestigation.value = report
    }

    fun closeInvestigation() {
        _selectedInvestigation.value = null
    }

    fun updateAnomalyStatus(anomalyId: String, status: AnomalyStatus, note: String? = null) {
        repository.updateAnomalyStatus(anomalyId, status, note)
        // Refresh open investigation if matched
        if (_selectedInvestigation.value?.anomalyId == anomalyId) {
            _selectedInvestigation.value = repository.getInvestigationReport(anomalyId)
        }
    }

    fun updateRecommendationStatus(recId: String, status: RecommendationStatus) {
        repository.updateRecommendationStatus(recId, status)
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(
            id = "user-${System.currentTimeMillis()}",
            isUser = true,
            text = prompt,
            timestamp = "Just now"
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            try {
                val aiResponse = repository.queryAiAnalyst(prompt)
                _chatMessages.value = _chatMessages.value + aiResponse
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    id = "ai-err-${System.currentTimeMillis()}",
                    isUser = false,
                    text = "Encountered an issue calculating analytical response: ${e.localizedMessage ?: "Unknown error"}. Please check query syntax.",
                    timestamp = "Now"
                )
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun resetDemoData(seed: Long = 42L) {
        repository.resetDemoData(seed)
        _selectedInvestigation.value = null
        _statusMessage.value = "Demo dataset re-seeded ($seed) with 90 days of records."
    }

    fun testBackend(url: String) {
        _isBackendTesting.value = true
        _backendTestSuccess.value = null
        viewModelScope.launch {
            val success = repository.testFastApiConnection(url)
            _isBackendTesting.value = false
            _backendTestSuccess.value = success
            _statusMessage.value = if (success) "Successfully reached FastAPI backend!" else "Could not reach $url (Demo mode active)"
        }
    }

    fun generateCsvExport() {
        val records = uiState.value.filteredRecords
        val csv = repository.exportCostsCsv(records)
        _exportCsvContent.value = csv
    }

    fun clearCsvExport() {
        _exportCsvContent.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
