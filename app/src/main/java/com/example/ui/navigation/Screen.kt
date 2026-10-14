package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Onboarding : Screen("onboarding", "Welcome")
    object Dashboard : Screen("dashboard", "Overview", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    object Costs : Screen("costs", "Costs", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    object Anomalies : Screen("anomalies", "Anomalies", Icons.Filled.Warning, Icons.Outlined.WarningAmber)
    object AiAnalyst : Screen("ai_analyst", "AI Analyst", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object More : Screen("more", "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)

    // Secondary Sub-Screens
    object Investigation : Screen("investigation", "Investigation")
    object Forecasting : Screen("forecasting", "Forecasting")
    object Optimization : Screen("optimization", "Optimization")
    object Resources : Screen("resources", "Resources")
    object DataSources : Screen("data_sources", "Data Sources")
    object Settings : Screen("settings", "Settings")

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(
                Dashboard,
                Costs,
                Anomalies,
                AiAnalyst,
                More
            )
    }
}
