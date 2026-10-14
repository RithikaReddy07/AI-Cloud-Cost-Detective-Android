package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.data.model.AnomalyStatus
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.CostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostDetectiveApp(
    viewModel: CostViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val topLevelRoutes = remember {
        setOf(
            Screen.Dashboard.route,
            Screen.Costs.route,
            Screen.Anomalies.route,
            Screen.AiAnalyst.route,
            Screen.More.route
        )
    }
    val isTopLevelRoute = currentRoute in topLevelRoutes

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (currentRoute) {
                                Screen.Dashboard.route -> "Cost Detective"
                                Screen.Costs.route -> "Cost Explorer"
                                Screen.Anomalies.route -> "Anomaly Center"
                                Screen.AiAnalyst.route -> "FinOps AI Analyst"
                                Screen.More.route -> "More Tools"
                                Screen.Forecasting.route -> "Cost Forecasting"
                                Screen.Optimization.route -> "Optimization"
                                Screen.Resources.route -> "Resource Inventory"
                                Screen.DataSources.route -> "Data Sources"
                                Screen.Settings.route -> "Settings"
                                Screen.Investigation.route -> "Investigation Dossier"
                                else -> "Cost Detective"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        if (isTopLevelRoute) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Cyan500.copy(alpha = 0.15f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FinOps",
                                    color = Cyan500,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (!isTopLevelRoute) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelRoute,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(200)) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Cyan500.copy(alpha = 0.2f),
                                selectedIconColor = Cyan600,
                                selectedTextColor = Cyan600,
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate400
                            ),
                            modifier = Modifier.testTag("nav_tab_${screen.route}")
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(260)) + fadeIn(tween(260))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(260)) + fadeOut(tween(260))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(260)) + fadeIn(tween(260))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(260)) + fadeOut(tween(260))
            }
        ) {
            // Screen 1: Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onContinue = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Screen 2: Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    uiState = uiState,
                    onSelectDateRange = { range -> viewModel.setDateRange(range) },
                    onNavigateToCosts = { navController.navigate(Screen.Costs.route) },
                    onNavigateToAnomalies = { navController.navigate(Screen.Anomalies.route) },
                    onNavigateToInvestigation = { anomalyId ->
                        viewModel.openInvestigation(anomalyId)
                        navController.navigate(Screen.Investigation.route)
                    },
                    onNavigateToAi = { navController.navigate(Screen.AiAnalyst.route) },
                    onNavigateToForecasting = { navController.navigate(Screen.Forecasting.route) },
                    onNavigateToOptimization = { navController.navigate(Screen.Optimization.route) },
                    onNavigateToDataSources = { navController.navigate(Screen.DataSources.route) }
                )
            }

            // Screen 3: Cost Explorer
            composable(Screen.Costs.route) {
                CostExplorerScreen(
                    uiState = uiState,
                    onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                    onServiceFilterChange = { srv -> viewModel.setServiceFilter(srv) },
                    onExportCsv = { viewModel.generateCsvExport() },
                    onClearCsvExport = { viewModel.clearCsvExport() }
                )
            }

            // Screen 4: Anomaly Center
            composable(Screen.Anomalies.route) {
                AnomalyCenterScreen(
                    uiState = uiState,
                    onSeverityFilterChange = { sev -> viewModel.setSeverityFilter(sev) },
                    onStatusFilterChange = { st -> viewModel.setAnomalyStatusFilter(st) },
                    onNavigateToInvestigation = { anomalyId ->
                        viewModel.openInvestigation(anomalyId)
                        navController.navigate(Screen.Investigation.route)
                    },
                    onQuickReview = { anomalyId ->
                        viewModel.updateAnomalyStatus(anomalyId, AnomalyStatus.REVIEWED)
                    }
                )
            }

            // Screen 5: Investigation Dossier
            composable(Screen.Investigation.route) {
                val report = uiState.selectedInvestigation
                if (report != null) {
                    InvestigationDetailScreen(
                        report = report,
                        currency = uiState.preferredCurrency,
                        onBack = { navController.popBackStack() },
                        onUpdateStatus = { id, st, note ->
                            viewModel.updateAnomalyStatus(id, st, note)
                        },
                        onAskAiAboutAnomaly = { prompt ->
                            viewModel.sendAiPrompt(prompt)
                            navController.navigate(Screen.AiAnalyst.route)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            // Screen 6: AI Cost Analyst
            composable(Screen.AiAnalyst.route) {
                AiAnalystScreen(
                    uiState = uiState,
                    onSendMessage = { prompt -> viewModel.sendAiPrompt(prompt) }
                )
            }

            // Screen 7: More Tools Hub
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToForecasting = { navController.navigate(Screen.Forecasting.route) },
                    onNavigateToOptimization = { navController.navigate(Screen.Optimization.route) },
                    onNavigateToResources = { navController.navigate(Screen.Resources.route) },
                    onNavigateToDataSources = { navController.navigate(Screen.DataSources.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Screen 8: Forecasting
            composable(Screen.Forecasting.route) {
                ForecastingScreen(
                    uiState = uiState,
                    onSelectHorizon = { h -> viewModel.setForecastHorizon(h) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Screen 9: Optimization
            composable(Screen.Optimization.route) {
                OptimizationScreen(
                    uiState = uiState,
                    onUpdateStatus = { id, st -> viewModel.updateRecommendationStatus(id, st) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Screen 10: Resource Inventory
            composable(Screen.Resources.route) {
                ResourceInventoryScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() }
                )
            }

            // Screen 11: Data Sources & Backend
            composable(Screen.DataSources.route) {
                DataSourcesScreen(
                    uiState = uiState,
                    onTestBackend = { url -> viewModel.testBackend(url) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Screen 12: Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    onCurrencyChange = { c -> viewModel.setCurrency(c) },
                    onSensitivityChange = { s -> viewModel.setAnomalySensitivity(s) },
                    onResetDemoData = { seed -> viewModel.resetDemoData(seed) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
