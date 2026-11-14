package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.model.BottomTab
import com.example.model.Screen
import com.example.ui.components.ArticleDetailDialog
import com.example.ui.components.AuthModal
import com.example.ui.components.CareAdviceDetailDialog
import com.example.ui.components.IncidentReportDialog
import com.example.ui.components.QuickScanDialog
import com.example.ui.components.TrustNetBottomNav
import com.example.ui.components.TrustNetHelpModal
import com.example.ui.components.TrustNetTopBar
import com.example.ui.components.VolunteerJoinDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TrustAIScreen
import com.example.ui.screens.TrustCareScreen
import com.example.ui.screens.TrustCheckScreen
import com.example.ui.screens.TrustHelpScreen
import com.example.ui.screens.TrustLabScreen
import com.example.ui.screens.TrustMediaScreen
import com.example.ui.screens.TrustPrivacyScreen
import com.example.ui.screens.VolunteerTeamScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TrustNetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TrustNetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(themeMode = themeMode) {
                TrustNetApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TrustNetApp(viewModel: TrustNetViewModel) {
    val context = LocalContext.current

    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentBottomTab by viewModel.currentBottomTab.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scanHistory by viewModel.scanHistory.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val privacyItems by viewModel.privacyItems.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val lastSyncTimestamp by viewModel.lastSyncTimestamp.collectAsState()
    val realtimeSyncEnabled by viewModel.realtimeSyncEnabled.collectAsState()

    val isScanning by viewModel.isScanning.collectAsState()
    val activeScanResult by viewModel.activeScanResult.collectAsState()

    val isAnalyzingAi by viewModel.isAnalyzingAi.collectAsState()
    val activeDeepfakeResult by viewModel.activeDeepfakeResult.collectAsState()

    val quizIndex by viewModel.quizCurrentIndex.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()
    val selectedQuizOption by viewModel.selectedQuizOption.collectAsState()
    val isQuizAnswered by viewModel.isQuizAnswered.collectAsState()

    val selectedCareSituation by viewModel.selectedCareSituation.collectAsState()
    val isAnalyzingCare by viewModel.isAnalyzingCare.collectAsState()
    val activeCareAdvice by viewModel.activeCareAdvice.collectAsState()
    val customAiDetectorUrl by viewModel.customAiDetectorUrl.collectAsState()
    val selectedArticle by viewModel.selectedArticle.collectAsState()
    val showQuickScanModal by viewModel.showQuickScanModal.collectAsState()
    val showVolunteerJoinDialog by viewModel.showVolunteerJoinDialog.collectAsState()
    val showIncidentReportDialog by viewModel.showIncidentReportDialog.collectAsState()
    val showAuthModal by viewModel.showAuthModal.collectAsState()
    val activeHelpGuide by viewModel.activeHelpGuide.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Handle Toast notifications
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Android hardware/gesture back handling
    BackHandler(enabled = currentScreen != Screen.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TrustNetTopBar(
                currentScreen = currentScreen,
                syncStatus = syncStatus,
                unreadAlertsCount = alerts.count { !it.isRead },
                onBackClick = { viewModel.navigateBack() },
                onHelpClick = { viewModel.openHelpForCurrentScreen() },
                onNotificationClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) }
            )
        },
        bottomBar = {
            TrustNetBottomNav(
                selectedTab = currentBottomTab,
                unreadAlertsCount = alerts.count { !it.isRead },
                onTabSelected = { tab -> viewModel.selectBottomTab(tab) },
                onCenterShieldClick = { viewModel.setQuickScanModalVisible(true) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.HOME -> HomeScreen(
                        latestAlert = alerts.firstOrNull(),
                        lastSyncTime = lastSyncTimestamp,
                        onNavigate = { viewModel.navigateTo(it) },
                        onQuickScanClick = { viewModel.setQuickScanModalVisible(true) }
                    )

                    Screen.TRUST_CHECK -> TrustCheckScreen(
                        scanResult = activeScanResult,
                        isScanning = isScanning,
                        onPerformScan = { query, type -> viewModel.performScan(query, type) },
                        onReportClick = { viewModel.setIncidentReportDialogVisible(true) }
                    )

                    Screen.TRUST_PRIVACY -> TrustPrivacyScreen(
                        privacyItems = privacyItems,
                        onToggleItem = { viewModel.togglePrivacyItem(it) }
                    )

                    Screen.TRUST_CARE -> TrustCareScreen(
                        isAnalyzing = isAnalyzingCare,
                        careAdvice = activeCareAdvice,
                        onAnalyzeSituation = { situation -> viewModel.analyzeCareSituation(situation) }
                    )

                    Screen.TRUST_AI -> TrustAIScreen(
                        analysisResult = activeDeepfakeResult,
                        isAnalyzing = isAnalyzingAi,
                        customApiUrl = customAiDetectorUrl,
                        onCustomApiUrlChange = { viewModel.setCustomAiDetectorUrl(it) },
                        onAnalyze = { isSimulatedFake -> viewModel.performAiDetection(isSimulatedFake) }
                    )

                    Screen.TRUST_LAB -> TrustLabScreen(
                        questions = viewModel.repository.quizQuestions,
                        currentIndex = quizIndex,
                        score = quizScore,
                        selectedOption = selectedQuizOption,
                        isAnswered = isQuizAnswered,
                        onAnswer = { viewModel.answerQuiz(it) },
                        onNextQuestion = { viewModel.nextQuizQuestion() }
                    )

                    Screen.TRUST_HELP -> TrustHelpScreen()

                    Screen.TRUST_MEDIA -> TrustMediaScreen(
                        articles = viewModel.repository.mediaArticles,
                        onArticleClick = { viewModel.selectArticle(it) }
                    )

                    Screen.VOLUNTEER_TEAM -> VolunteerTeamScreen(
                        activities = viewModel.repository.volunteerActivities,
                        onJoinClick = { viewModel.setVolunteerJoinDialogVisible(true) },
                        onReportClick = { viewModel.setIncidentReportDialogVisible(true) },
                        onActivityClick = { viewModel.showToast("Đã chọn hoạt động: ${it.title}") }
                    )

                    Screen.HISTORY -> HistoryScreen(
                        scanHistory = scanHistory,
                        onSelectScan = { scan ->
                            viewModel.performScan(scan.target, scan.type)
                        },
                        onClearHistory = { viewModel.clearHistory() }
                    )

                    Screen.NOTIFICATIONS -> NotificationsScreen(
                        alerts = alerts,
                        syncStatus = syncStatus,
                        onAlertClick = { alert ->
                            viewModel.repository.markAlertAsRead(alert.id)
                        },
                        onSyncClick = { viewModel.triggerSync() }
                    )

                    Screen.PROFILE -> ProfileScreen(
                        user = currentUser,
                        themeMode = themeMode,
                        realtimeSyncEnabled = realtimeSyncEnabled,
                        syncStatus = syncStatus,
                        lastSyncTimestamp = lastSyncTimestamp,
                        currentApiKey = viewModel.repository.aiService.getEffectiveApiKey(),
                        onSetThemeMode = { viewModel.setThemeMode(it) },
                        onToggleRealtimeSync = { viewModel.toggleRealtimeSync(it) },
                        onManualSync = { viewModel.triggerSync() },
                        onSaveApiKey = { viewModel.setCustomApiKey(it) },
                        onOpenAuth = { viewModel.setAuthModalVisible(true) },
                        onLogout = { viewModel.logout() },
                        onViewHistory = { viewModel.navigateTo(Screen.HISTORY) },
                        onHelpClick = { viewModel.openHelpForCurrentScreen() }
                    )
                }
            }

            // Contextual Help Guide Modal ('?' button clicked)
            activeHelpGuide?.let { guide ->
                TrustNetHelpModal(
                    guide = guide,
                    onDismiss = { viewModel.dismissHelp() }
                )
            }

            // User Authentication Modal (Sign In / Register / Google Auth)
            if (showAuthModal) {
                AuthModal(
                    onDismiss = { viewModel.setAuthModalVisible(false) },
                    onLogin = { email, pass -> viewModel.login(email, pass) },
                    onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                    onGoogleLogin = { viewModel.loginWithGoogle() }
                )
            }

            // Quick Scan Modal (from central floating dock button)
            if (showQuickScanModal) {
                QuickScanDialog(
                    onDismiss = { viewModel.setQuickScanModalVisible(false) },
                    onPerformScan = { target, type -> viewModel.performScan(target, type) }
                )
            }

            // Care Situation Detail Modal
            selectedCareSituation?.let { item ->
                CareAdviceDetailDialog(
                    item = item,
                    onDismiss = { viewModel.selectCareSituation(null) }
                )
            }

            // Media Article Detail Modal
            selectedArticle?.let { article ->
                ArticleDetailDialog(
                    article = article,
                    onDismiss = { viewModel.selectArticle(null) }
                )
            }

            // Volunteer Team Join Form Modal
            if (showVolunteerJoinDialog) {
                VolunteerJoinDialog(
                    onDismiss = { viewModel.setVolunteerJoinDialogVisible(false) },
                    onSubmit = { name, school ->
                        viewModel.submitVolunteerRegistration(name, school, "")
                    }
                )
            }

            // Incident Report Modal
            if (showIncidentReportDialog) {
                IncidentReportDialog(
                    onDismiss = { viewModel.setIncidentReportDialogVisible(false) },
                    onSubmit = { target, details ->
                        viewModel.submitIncidentReport(target, details)
                    }
                )
            }
        }
    }
}
