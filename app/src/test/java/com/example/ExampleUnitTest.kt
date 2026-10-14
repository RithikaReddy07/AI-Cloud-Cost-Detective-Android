package com.example

import com.example.data.local.DemoDataProvider
import com.example.data.model.AnomalySeverity
import com.example.data.repository.CostRepositoryImpl
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDemoDataReproducibility() {
        val run1 = DemoDataProvider.generateCostRecords(seed = 42L, daysCount = 90)
        val run2 = DemoDataProvider.generateCostRecords(seed = 42L, daysCount = 90)

        assertEquals("Record count must match 90 days * 12 resources", 90 * 12, run1.size)
        assertEquals("Total records between runs must match", run1.size, run2.size)

        for (i in run1.indices) {
            assertEquals("Record ID must match", run1[i].id, run2[i].id)
            assertEquals("Cost amount must be identical for same seed", run1[i].costAmount, run2[i].costAmount, 0.001)
        }
    }

    @Test
    fun testStatisticalAnomalyDetection() {
        val repo = CostRepositoryImpl()
        val anomalies = repo.anomaliesFlow.value

        assertTrue("Anomalies must be detected from 90 days of synthetic data", anomalies.isNotEmpty())

        val criticalStorageAnomaly = anomalies.find {
            it.resourceName == "stprodauditlogs" && it.severity == AnomalySeverity.CRITICAL
        }
        assertNotNull("Critical storage spike must be flagged as critical anomaly", criticalStorageAnomaly)
        assertTrue("Spike percentage must be > 100%", criticalStorageAnomaly!!.percentageDeviation > 100.0)
        assertTrue("Actual cost must exceed baseline", criticalStorageAnomaly.actualCost > criticalStorageAnomaly.baselineCost)
    }

    @Test
    fun testForecastingCalculations() {
        val repo = CostRepositoryImpl()
        val fc30 = repo.generateForecast(30)

        assertEquals("Forecast horizon must be 30", 30, fc30.horizonDays)
        assertTrue("Projected total cost must be positive", fc30.projectedTotalCost > 0.0)
        assertTrue("Forecast points must contain predictions", fc30.points.any { it.isForecast })

        for (pt in fc30.points.filter { it.isForecast }) {
            assertTrue("Upper bound must be >= predicted cost", pt.upperBound >= pt.cost)
            assertTrue("Lower bound must be <= predicted cost", pt.lowerBound <= pt.cost)
        }
    }

    @Test
    fun testInvestigationReportStructure() {
        val repo = CostRepositoryImpl()
        val anomaly = repo.anomaliesFlow.value.first()
        val report = repo.getInvestigationReport(anomaly.id)

        assertNotNull("Investigation report must exist for detected anomaly", report)
        assertTrue("Observed facts must be documented", report!!.observedFacts.isNotEmpty())
        assertTrue("Possible causes must be framed as hypotheses", report.possibleCauses.isNotEmpty())
        assertTrue("Missing evidence must be explicitly acknowledged", report.missingEvidence.isNotEmpty())
        assertTrue("Recommended next steps must be provided", report.recommendedNextSteps.isNotEmpty())
    }
}
