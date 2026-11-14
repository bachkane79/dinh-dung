package com.example.model

enum class Screen {
    HOME,
    TRUST_CHECK,
    TRUST_PRIVACY,
    TRUST_CARE,
    TRUST_AI,
    TRUST_LAB,
    TRUST_HELP,
    TRUST_MEDIA,
    VOLUNTEER_TEAM,
    HISTORY,
    NOTIFICATIONS,
    PROFILE
}

enum class BottomTab {
    HOME,
    HISTORY,
    SCAN_CENTER,
    NOTIFICATIONS,
    PROFILE
}

enum class RiskLevel {
    SAFE,
    MODERATE,
    HIGH_RISK
}

enum class QueryType {
    URL,
    SMS,
    PHONE,
    IMAGE
}

data class UserProfile(
    val id: String = "usr-01",
    val name: String = "Amelia Andra",
    val email: String = "amelia.andra@trustnet.org",
    val avatarRes: Int? = null,
    val isLoggedIn: Boolean = true,
    val safetyRank: String = "Hiệp sĩ An toàn",
    val levelScore: Int = 1820,
    val totalScans: Int = 18,
    val protectedHours: Int = 240
)

data class HelpGuide(
    val title: String,
    val subtitle: String,
    val overview: String,
    val tips: List<String> = emptyList(),
    val quickActionTitle: String? = null,
    val hotline: String? = null
)

data class RedFlagPoint(
    val title: String,
    val points: Int, // e.g. +35, +25, +15
    val isSevere: Boolean = true
)

data class ScanResult(
    val id: String,
    val target: String,
    val type: QueryType,
    val riskScore: Int, // 0 - 100
    val riskLevel: RiskLevel,
    val redFlags: List<String>,
    val recommendations: List<String>,
    val redFlagPoints: List<RedFlagPoint> = emptyList(),
    val analyzedDomain: String = "",
    val isDomainValid: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityAlert(
    val id: String,
    val title: String,
    val summary: String,
    val tag: String,
    val timeAgo: String,
    val isUrgent: Boolean = false,
    val isRead: Boolean = false
)

data class PrivacyAuditItem(
    val id: String,
    val title: String,
    val description: String,
    val isSecure: Boolean,
    val actionText: String
)

data class CareSituationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val adviceList: List<String>,
    val emergencyNote: String,
    val hotline: String = "1800 96 96 12"
)

// AI Situation Analysis for TrustCare Chatbot/Advisor
data class CareAiAdvice(
    val urgencyLevel: String, // "CỰC KỲ KHẨN CẤP", "CẦN THẬN TRỌNG", "AN TOÀN / BÌNH TĨNH"
    val situationSummary: String,
    val actionSteps: List<String>,
    val legalWarning: String,
    val recommendedHotline: String = "1800 96 96 12",
    val mentalSupportTip: String = ""
)

data class DeepfakeAnalysisResult(
    val fileName: String,
    val isReal: Boolean,
    val confidence: Int,
    val lightingConsistency: Int,
    val facialGeometry: Int,
    val generativePatterns: Int,
    val summaryNote: String,
    val detailedFindings: List<String>,
    val aiProbability: Int = if (!isReal) 88 else 4,
    val detectorSource: String = "Meta AI Detector / Gemini 2.0 Flash"
)

data class QuizQuestion(
    val id: Int,
    val title: String,
    val situationText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class MediaArticle(
    val id: String,
    val title: String,
    val category: String, // Tin tức, Cẩm nang, Video, Quiz, Góc cảnh báo
    val date: String,
    val readMinutes: String,
    val summary: String,
    val content: String,
    val isAlert: Boolean = false
)

data class VolunteerActivity(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val participants: String,
    val description: String,
    val isJoined: Boolean = false
)

enum class SyncStatus {
    SYNCED,
    SYNCING,
    OFFLINE
}
