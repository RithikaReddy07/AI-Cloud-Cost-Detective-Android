package com.example.data.remote

import com.example.data.model.CostRecord
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

data class HealthResponse(
    val status: String,
    val database: String? = null,
    val version: String? = null
)

data class CostSummaryResponse(
    val totalSpend: Double,
    val currency: String,
    val recordCount: Int,
    val startDate: String,
    val endDate: String
)

data class ServiceBreakdownItem(
    val serviceName: String,
    val totalCost: Double,
    val percentage: Double
)

data class AiChatRequest(
    val prompt: String,
    val contextPeriodDays: Int = 30
)

data class AiChatResponse(
    val response: String,
    val sources: List<String> = emptyList(),
    val model: String? = null
)

interface FastApiCostService {

    @GET("/api/health")
    suspend fun getHealth(): Response<HealthResponse>

    @GET("/api/costs")
    suspend fun getCosts(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("service") service: String? = null
    ): Response<List<CostRecord>>

    @GET("/api/costs/summary")
    suspend fun getCostSummary(): Response<CostSummaryResponse>

    @GET("/api/costs/breakdown")
    suspend fun getCostBreakdown(): Response<List<ServiceBreakdownItem>>

    @POST("/api/ai/chat")
    suspend fun chatWithAi(
        @Body request: AiChatRequest
    ): Response<AiChatResponse>
}
