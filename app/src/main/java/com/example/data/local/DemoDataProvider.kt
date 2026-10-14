package com.example.data.local

import com.example.data.model.CloudResource
import com.example.data.model.CostRecord
import java.time.LocalDate
import java.util.Random

object DemoDataProvider {

    val RESOURCES = listOf(
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Compute/virtualMachines/vm-prod-api-01",
            name = "vm-prod-api-01",
            type = "Microsoft.Compute/virtualMachines",
            serviceName = "Virtual Machines",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 288.0,
            cpuAvgPercent = 64.2,
            memoryAvgPercent = 71.5,
            status = "Running"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Compute/virtualMachines/vm-prod-worker-02",
            name = "vm-prod-worker-02",
            type = "Microsoft.Compute/virtualMachines",
            serviceName = "Virtual Machines",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 432.0,
            cpuAvgPercent = 78.4,
            memoryAvgPercent = 82.0,
            status = "Running"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-dev/providers/Microsoft.Compute/virtualMachines/vm-dev-sandbox-idle",
            name = "vm-dev-sandbox-idle",
            type = "Microsoft.Compute/virtualMachines",
            serviceName = "Virtual Machines",
            resourceGroup = "rg-dev",
            region = "West US 2",
            monthlySpend = 310.0,
            cpuAvgPercent = 3.1, // IDLE! Clear optimization target
            memoryAvgPercent = 8.5,
            status = "Running"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-staging/providers/Microsoft.Compute/virtualMachines/vm-staging-web-01",
            name = "vm-staging-web-01",
            type = "Microsoft.Compute/virtualMachines",
            serviceName = "Virtual Machines",
            resourceGroup = "rg-staging",
            region = "East US",
            monthlySpend = 86.4,
            cpuAvgPercent = 14.5,
            memoryAvgPercent = 32.0,
            status = "Running"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Storage/storageAccounts/stprodauditlogs",
            name = "stprodauditlogs",
            type = "Microsoft.Storage/storageAccounts",
            serviceName = "Storage Accounts",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 520.0,
            status = "Active"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Storage/storageAccounts/stbackupcold",
            name = "stbackupcold",
            type = "Microsoft.Storage/storageAccounts",
            serviceName = "Storage Accounts",
            resourceGroup = "rg-production",
            region = "West US 2",
            monthlySpend = 145.0,
            status = "Active"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Sql/servers/sql-core-srv/databases/sql-core-db",
            name = "sql-core-db",
            type = "Microsoft.Sql/servers/databases",
            serviceName = "Azure SQL Database",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 380.0,
            cpuAvgPercent = 48.0,
            memoryAvgPercent = 65.0,
            status = "Online"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-analytics/providers/Microsoft.Sql/servers/sql-dw-srv/databases/sql-analytics-dw",
            name = "sql-analytics-dw",
            type = "Microsoft.Sql/servers/databases",
            serviceName = "Azure SQL Database",
            resourceGroup = "rg-analytics",
            region = "East US",
            monthlySpend = 720.0,
            cpuAvgPercent = 52.0,
            memoryAvgPercent = 58.0,
            status = "Online"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.Web/serverfarms/app-frontend-portal",
            name = "app-frontend-portal",
            type = "Microsoft.Web/serverfarms",
            serviceName = "App Service",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 216.0,
            cpuAvgPercent = 38.0,
            memoryAvgPercent = 55.0,
            status = "Running"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.DocumentDB/databaseAccounts/cosmos-session-store",
            name = "cosmos-session-store",
            type = "Microsoft.DocumentDB/databaseAccounts",
            serviceName = "Azure Cosmos DB",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 480.0,
            status = "Online"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-network/providers/Microsoft.Network/bandwidth/vnet-egress-crossregion",
            name = "vnet-egress-crossregion",
            type = "Microsoft.Network/bandwidth",
            serviceName = "Virtual Network",
            resourceGroup = "rg-network",
            region = "Global",
            monthlySpend = 195.0,
            status = "Active"
        ),
        CloudResource(
            id = "/subscriptions/sub-azure-demo-001/resourceGroups/rg-production/providers/Microsoft.ContainerService/managedClusters/aks-prod-cluster",
            name = "aks-prod-cluster",
            type = "Microsoft.ContainerService/managedClusters",
            serviceName = "Kubernetes Service",
            resourceGroup = "rg-production",
            region = "East US",
            monthlySpend = 640.0,
            cpuAvgPercent = 61.0,
            memoryAvgPercent = 70.0,
            status = "Running"
        )
    )

    fun generateCostRecords(seed: Long = 42L, daysCount: Int = 90): List<CostRecord> {
        val random = Random(seed)
        val records = mutableListOf<CostRecord>()
        val today = LocalDate.now()
        val startDate = today.minusDays((daysCount - 1).toLong())

        // Base daily cost definition per resource (mean normal daily cost)
        val baseResourceDailyCosts = mapOf(
            "vm-prod-api-01" to 9.60,
            "vm-prod-worker-02" to 14.40,
            "vm-dev-sandbox-idle" to 10.33,
            "vm-staging-web-01" to 2.88,
            "stprodauditlogs" to 7.50,
            "stbackupcold" to 4.80,
            "sql-core-db" to 12.65,
            "sql-analytics-dw" to 24.00,
            "app-frontend-portal" to 7.20,
            "cosmos-session-store" to 15.00,
            "vnet-egress-crossregion" to 6.50,
            "aks-prod-cluster" to 21.30
        )

        val unitsMap = mapOf(
            "Virtual Machines" to "Hours",
            "Storage Accounts" to "GB-Months",
            "Azure SQL Database" to "vCore-Hours",
            "App Service" to "App-Hours",
            "Azure Cosmos DB" to "100 RU/s",
            "Virtual Network" to "GB Egress",
            "Kubernetes Service" to "Cluster-Hours"
        )

        for (dayOffset in 0 until daysCount) {
            val date = startDate.plusDays(dayOffset.toLong())
            val dayOfWeek = date.dayOfWeek.value // 1 (Mon) to 7 (Sun)
            val isWeekend = dayOfWeek in 6..7

            // Slight gradual business growth over the 90-day period (e.g. +10% over 90 days)
            val growthFactor = 1.0 + (dayOffset.toDouble() / daysCount.toDouble()) * 0.12

            for (res in RESOURCES) {
                val base = baseResourceDailyCosts[res.name] ?: 10.0

                // Weekend reduction for dev/staging and user-traffic APIs
                val weekendDampener = if (isWeekend) {
                    when (res.resourceGroup) {
                        "rg-dev", "rg-staging" -> 0.70
                        "rg-analytics" -> 0.50
                        else -> 0.88
                    }
                } else 1.0

                // Gaussian fluctuation around base: standard deviation ~ 8%
                val normalVariation = 1.0 + (random.nextGaussian() * 0.08)
                var dailyCost = base * growthFactor * weekendDampener * normalVariation

                // Deliberate reproducible anomalies:
                // 1. Critical Anomaly: Runaway Blob Storage Logging on 'stprodauditlogs'
                //    Happened 4 days ago - spend surged by +480% due to verbose debug log loop
                val daysAgo = today.toEpochDay() - date.toEpochDay()
                if (res.name == "stprodauditlogs" && daysAgo in 3..5) {
                    val surgeMultiplier = when (daysAgo) {
                        4L -> 5.8 // Peak spike
                        3L -> 4.2
                        5L -> 3.1
                        else -> 1.0
                    }
                    dailyCost = base * surgeMultiplier
                }

                // 2. High Anomaly: Cosmos DB Autoscale RU Max Out on 'cosmos-session-store'
                //    Happened 12 days ago - spike of +210%
                if (res.name == "cosmos-session-store" && daysAgo in 11..13) {
                    val surgeMultiplier = if (daysAgo == 12L) 3.1 else 2.2
                    dailyCost = base * surgeMultiplier
                }

                // 3. High Anomaly: Unexpected Cross-region Data Egress on 'vnet-egress-crossregion'
                //    Happened 24 days ago - unoptimized DB dump replication spike (+350%)
                if (res.name == "vnet-egress-crossregion" && daysAgo == 24L) {
                    dailyCost = base * 4.5
                }

                // 4. Medium Anomaly: Unscheduled Batch Job on 'vm-prod-worker-02'
                //    Happened 38 days ago on weekend (+180%)
                if (res.name == "vm-prod-worker-02" && daysAgo in 37..38) {
                    dailyCost = base * 2.8
                }

                // Ensure positive
                if (dailyCost < 0.1) dailyCost = 0.1
                val roundedCost = Math.round(dailyCost * 100.0) / 100.0

                val quantity = when (res.serviceName) {
                    "Virtual Machines" -> 24.0
                    "Storage Accounts" -> Math.round((roundedCost / 0.02) * 10.0) / 10.0
                    "Azure SQL Database" -> 24.0
                    "App Service" -> 24.0
                    "Azure Cosmos DB" -> Math.round((roundedCost / 0.008) * 10.0) / 10.0
                    "Virtual Network" -> Math.round((roundedCost / 0.087) * 10.0) / 10.0
                    "Kubernetes Service" -> 24.0
                    else -> 1.0
                }

                records.add(
                    CostRecord(
                        id = "rec-${date}-${res.name}",
                        usageDate = date,
                        subscriptionId = "sub-azure-demo-001",
                        resourceId = res.id,
                        resourceName = res.name,
                        resourceGroup = res.resourceGroup,
                        serviceName = res.serviceName,
                        region = res.region,
                        usageQuantity = quantity,
                        unitOfMeasure = unitsMap[res.serviceName] ?: "Units",
                        costAmount = roundedCost,
                        currency = "USD",
                        source = "Demo Generator"
                    )
                )
            }
        }

        return records
    }
}
