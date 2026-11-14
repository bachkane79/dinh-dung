package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TrustNetRepository
import com.example.model.*
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import com.example.data.MediaInput

class TrustNetViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val privacyPreferences = application.getSharedPreferences("trust_privacy", 0)
    val repository = TrustNetRepository(
        completedPrivacyItems = privacyPreferences.getStringSet("completed", emptySet()).orEmpty()
    )

    // Navigation state
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentBottomTab = MutableStateFlow(BottomTab.HOME)
    val currentBottomTab: StateFlow<BottomTab> = _currentBottomTab.asStateFlow()

    // Navigation back stack for fluid back navigation
    private val screenStack = mutableListOf<Screen>(Screen.HOME)

    // Theme mode state (defaults to clean Light mode matching the reference image)
    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // User Profile & Authentication State
    val currentUser: StateFlow<UserProfile> = repository.currentUser

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    // Contextual Help Guide Modal State ('?' button)
    private val _activeHelpGuide = MutableStateFlow<HelpGuide?>(null)
    val activeHelpGuide: StateFlow<HelpGuide?> = _activeHelpGuide.asStateFlow()

    // Data from Repository
    val scanHistory: StateFlow<List<ScanResult>> = repository.scanHistory
    val alerts: StateFlow<List<SecurityAlert>> = repository.alerts
    val privacyItems: StateFlow<List<PrivacyAuditItem>> = repository.privacyItems
    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus
    val lastSyncTimestamp: StateFlow<Long> = repository.lastSyncTimestamp
    val realtimeSyncEnabled: StateFlow<Boolean> = repository.realtimeSyncEnabled

    // TrustCheck state
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _activeScanResult = MutableStateFlow<ScanResult?>(null)
    val activeScanResult: StateFlow<ScanResult?> = _activeScanResult.asStateFlow()

    // TrustAI state
    private val _isAnalyzingAi = MutableStateFlow(false)
    val isAnalyzingAi: StateFlow<Boolean> = _isAnalyzingAi.asStateFlow()

    private val _activeDeepfakeResult = MutableStateFlow<DeepfakeAnalysisResult?>(null)
    val activeDeepfakeResult: StateFlow<DeepfakeAnalysisResult?> = _activeDeepfakeResult.asStateFlow()

    // TrustLab Quiz state
    private val _quizCurrentIndex = MutableStateFlow(0)
    val quizCurrentIndex: StateFlow<Int> = _quizCurrentIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(100)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<Int?>(null)
    val selectedQuizOption: StateFlow<Int?> = _selectedQuizOption.asStateFlow()

    private val _isQuizAnswered = MutableStateFlow(false)
    val isQuizAnswered: StateFlow<Boolean> = _isQuizAnswered.asStateFlow()

    // TrustCare AI Situation Guidance State
    private val _isAnalyzingCare = MutableStateFlow(false)
    val isAnalyzingCare: StateFlow<Boolean> = _isAnalyzingCare.asStateFlow()

    private val _careMessages = MutableStateFlow<List<CareMessage>>(emptyList())
    val careMessages = _careMessages.asStateFlow()
    private val _careError = MutableStateFlow<String?>(null)
    val careError = _careError.asStateFlow()
    private var careRequest: Job? = null
    private var careRevision = 0

    // TrustAI Custom Detector API URL
    // Dialog & Selection states
    private val _selectedCareSituation = MutableStateFlow<CareSituationItem?>(null)
    val selectedCareSituation: StateFlow<CareSituationItem?> = _selectedCareSituation.asStateFlow()

    private val _selectedArticle = MutableStateFlow<MediaArticle?>(null)
    val selectedArticle: StateFlow<MediaArticle?> = _selectedArticle.asStateFlow()

    private val _showQuickScanModal = MutableStateFlow(false)
    val showQuickScanModal: StateFlow<Boolean> = _showQuickScanModal.asStateFlow()

    private val _showVolunteerJoinDialog = MutableStateFlow(false)
    val showVolunteerJoinDialog: StateFlow<Boolean> = _showVolunteerJoinDialog.asStateFlow()

    private val _showIncidentReportDialog = MutableStateFlow(false)
    val showIncidentReportDialog: StateFlow<Boolean> = _showIncidentReportDialog.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Navigation helpers
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
        // Sync bottom tab if applicable
        when (screen) {
            Screen.HOME -> _currentBottomTab.value = BottomTab.HOME
            Screen.HISTORY -> _currentBottomTab.value = BottomTab.HISTORY
            Screen.NOTIFICATIONS -> _currentBottomTab.value = BottomTab.NOTIFICATIONS
            Screen.PROFILE -> _currentBottomTab.value = BottomTab.PROFILE
            else -> {}
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            val previousScreen = screenStack.last()
            _currentScreen.value = previousScreen
            when (previousScreen) {
                Screen.HOME -> _currentBottomTab.value = BottomTab.HOME
                Screen.HISTORY -> _currentBottomTab.value = BottomTab.HISTORY
                Screen.NOTIFICATIONS -> _currentBottomTab.value = BottomTab.NOTIFICATIONS
                Screen.PROFILE -> _currentBottomTab.value = BottomTab.PROFILE
                else -> {}
            }
            return true
        } else if (_currentScreen.value != Screen.HOME) {
            _currentScreen.value = Screen.HOME
            _currentBottomTab.value = BottomTab.HOME
            screenStack.clear()
            screenStack.add(Screen.HOME)
            return true
        }
        return false
    }

    fun selectBottomTab(tab: BottomTab) {
        _currentBottomTab.value = tab
        when (tab) {
            BottomTab.HOME -> navigateTo(Screen.HOME)
            BottomTab.HISTORY -> navigateTo(Screen.HISTORY)
            BottomTab.SCAN_CENTER -> _showQuickScanModal.value = true
            BottomTab.NOTIFICATIONS -> navigateTo(Screen.NOTIFICATIONS)
            BottomTab.PROFILE -> navigateTo(Screen.PROFILE)
        }
    }

    fun openHelpForCurrentScreen() {
        val guide = repository.helpGuides[_currentScreen.value] ?: repository.helpGuides[Screen.HOME]
        _activeHelpGuide.value = guide
    }

    fun dismissHelp() {
        _activeHelpGuide.value = null
    }

    fun setAuthModalVisible(visible: Boolean) {
        _showAuthModal.value = visible
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val ok = repository.login(email, pass)
            if (ok) {
                _showAuthModal.value = false
                _activeScanResult.value = null
                showToast("Đăng nhập thành công!")
            }
            else showToast("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.")
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            val ok = repository.register(name, email, pass)
            if (ok) {
                _showAuthModal.value = false
                _activeScanResult.value = null
                showToast("Đã tạo tài khoản.")
            } else showToast("Đăng ký thất bại. Kiểm tra thông tin hoặc thử email khác.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            resetCareChat()
            repository.logout()
            _activeScanResult.value = null
            showToast("Đã đăng xuất.")
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
        }
    }

    fun performScan(targetText: String, type: QueryType = QueryType.URL, media: MediaInput? = null) {
        if (_isScanning.value || (targetText.isBlank() && media == null)) return
        viewModelScope.launch {
            _isScanning.value = true
            _activeScanResult.value = null
            try {
                val owner = repository.currentUser.value.id
                val scan = repository.analyzeTarget(targetText, type, media)
                if (repository.currentUser.value.id == owner) {
                    _activeScanResult.value = scan
                    navigateTo(Screen.TRUST_CHECK)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: IllegalArgumentException) {
                showToast(error.message ?: "Không đọc được tệp.")
            } catch (_: Exception) {
                showToast("Không thể phân tích. Hãy kiểm tra kết nối và khóa Gemini.")
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun performAiDetection(media: MediaInput) {
        if (_isAnalyzingAi.value) return
        viewModelScope.launch {
            _isAnalyzingAi.value = true
            _activeDeepfakeResult.value = null
            try {
                _activeDeepfakeResult.value = repository.analyzeDeepfake(media)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IllegalArgumentException) {
                showToast(error.message ?: "Không đọc được tệp.")
            } catch (_: Exception) {
                showToast("Không thể phân tích tệp. Hãy kiểm tra kết nối và khóa Gemini.")
            } finally {
                _isAnalyzingAi.value = false
            }
        }
    }

    fun sendCareMessage(text: String) {
        if (text.isBlank() || _isAnalyzingCare.value) return
        val messages = _careMessages.value
        // Update the pending message if the preceding request failed.
        _careMessages.value = if (messages.lastOrNull()?.fromUser == true) {
            messages.dropLast(1) + CareMessage(true, messages.last().text + "\n\n" + text)
        } else messages + CareMessage(true, text)
        retryCareReply()
    }

    fun retryCareReply() {
        if (_isAnalyzingCare.value || _careMessages.value.lastOrNull()?.fromUser != true) return
        _isAnalyzingCare.value = true
        _careError.value = null
        val conversation = _careMessages.value
        val revision = ++careRevision
        careRequest = viewModelScope.launch {
            try {
                val reply = repository.getCareReply(conversation)
                _careMessages.value = conversation + CareMessage(false, reply)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IllegalArgumentException) {
                _careError.value = error.message ?: "Kiểm tra khóa Gemini trong mục Cá nhân."
            } catch (_: Exception) {
                _careError.value = "Chưa nhận được phản hồi. Kiểm tra kết nối hoặc khóa Gemini và thử lại."
            } finally {
                if (revision == careRevision) _isAnalyzingCare.value = false
            }
        }
    }

    fun resetCareChat() {
        careRevision++
        careRequest?.cancel()
        _careMessages.value = emptyList()
        _careError.value = null
        _isAnalyzingCare.value = false
    }

    fun answerQuiz(optionIndex: Int) {
        if (_isQuizAnswered.value) return
        _selectedQuizOption.value = optionIndex
        _isQuizAnswered.value = true
        val currentQ = repository.quizQuestions.getOrNull(_quizCurrentIndex.value)
        if (currentQ != null && optionIndex == currentQ.correctIndex) {
            _quizScore.value += 100
        }
    }

    fun nextQuizQuestion() {
        val nextIdx = _quizCurrentIndex.value + 1
        if (nextIdx < repository.quizQuestions.size) {
            _quizCurrentIndex.value = nextIdx
            _selectedQuizOption.value = null
            _isQuizAnswered.value = false
        } else {
            _quizCurrentIndex.value = 0
            _selectedQuizOption.value = null
            _isQuizAnswered.value = false
            showToast("Chúc mừng bạn đã hoàn thành bài kiểm tra kỹ năng an toàn số!")
        }
    }

    fun selectCareSituation(item: CareSituationItem?) {
        _selectedCareSituation.value = item
    }

    fun selectArticle(article: MediaArticle?) {
        _selectedArticle.value = article
    }

    fun togglePrivacyItem(id: String) {
        repository.togglePrivacyAuditItem(id)
        val completed = privacyItems.value.filter { it.isSecure }.map { it.id }.toSet()
        privacyPreferences.edit().putStringSet("completed", completed).apply()
    }

    fun triggerSync() {
        viewModelScope.launch {
            val synced = repository.triggerManualSync()
            showToast(if (synced) "Đã đồng bộ dữ liệu." else "Không thể đồng bộ. Hãy kiểm tra kết nối.")
        }
    }

    fun toggleRealtimeSync(enabled: Boolean) {
        repository.toggleRealtimeSync(enabled)
        showToast(if (enabled) "Đã bật đồng bộ thời gian thực" else "Đã tắt đồng bộ thời gian thực")
    }

    fun setQuickScanModalVisible(visible: Boolean) {
        _showQuickScanModal.value = visible
    }

    fun setVolunteerJoinDialogVisible(visible: Boolean) {
        _showVolunteerJoinDialog.value = visible
    }

    fun setIncidentReportDialogVisible(visible: Boolean) {
        _showIncidentReportDialog.value = visible
    }

    fun setCustomApiKey(key: String) {
        repository.aiService.customApiKey = key.trim()
        showToast(if (key.isBlank()) "Đã xóa API key tùy chỉnh" else "Đã đặt khóa Gemini cho phiên này.")
    }

    fun submitIncidentReport(target: String, details: String) {
        viewModelScope.launch {
            val success = repository.submitIncidentReport(target, details)
            if (success) {
                showToast("Đã gửi phản ánh lên máy chủ Neon PostgreSQL an toàn!")
            } else {
                showToast("Gửi phản ánh thất bại. Vui lòng đăng nhập và kiểm tra kết nối.")
            }
        }
    }

    fun submitVolunteerRegistration(name: String, school: String, phone: String) {
        viewModelScope.launch {
            val success = repository.submitVolunteerRegistration(name, school, phone)
            showToast(if (success) "Đã gửi đăng ký tình nguyện." else "Gửi đăng ký thất bại. Vui lòng đăng nhập và kiểm tra kết nối.")
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearHistory() {
        viewModelScope.launch {
            val success = repository.clearHistory()
            if (success) _activeScanResult.value = null
            showToast(if (success) "Đã xóa lịch sử của bạn." else "Chưa xóa được lịch sử. Vui lòng thử lại.")
        }
    }
}
