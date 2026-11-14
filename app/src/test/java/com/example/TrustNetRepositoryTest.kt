package com.example

import com.example.data.TrustNetRepository
import com.example.model.QueryType
import com.example.model.RiskLevel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class TrustNetRepositoryTest {
    private val repository = TrustNetRepository()

    @Test
    fun startsWithGuestAndNoSampleHistory() {
        assertFalse(repository.currentUser.value.isLoggedIn)
        assertTrue(repository.scanHistory.value.isEmpty())
    }

    @Test
    fun offlineScanReturnsLowScoreAndExplainsItsLimits() = runTest {
        org.junit.Assume.assumeTrue(repository.aiService.getEffectiveApiKey().isBlank())
        val result = repository.analyzeTarget("https://example.invalid", QueryType.URL)
        assertEquals(0, result.riskScore)
        assertTrue(result.recommendations.first().contains("Điểm dự phòng"))
        assertFalse(result.isDomainValid)
    }

    @Test
    fun loginPathAloneDoesNotProvePhishing() = runTest {
        // Syntax analysis without a Gemini key.
        org.junit.Assume.assumeTrue(repository.aiService.getEffectiveApiKey().isBlank())
        val result = repository.analyzeTarget("https://example.com/login", QueryType.URL)
        assertEquals(RiskLevel.SAFE, result.riskLevel)
        assertEquals(0, result.riskScore)
    }

    @Test
    fun offlineScanScoresObservableSignals() = runTest {
        org.junit.Assume.assumeTrue(repository.aiService.getEffectiveApiKey().isBlank())
        val scan = repository.analyzeTarget("Gửi mã OTP ngay lập tức và đóng phí để nhận thưởng", QueryType.SMS)
        assertEquals(70, scan.riskScore)
        assertEquals(RiskLevel.HIGH_RISK, scan.riskLevel)
        assertEquals(3, scan.redFlags.size)
    }

    @Test
    fun checklistHasUniqueItemsAndRestoresSavedSelection() {
        val items = TrustNetRepository(completedPrivacyItems = setOf("passkeys")).privacyItems.value
        assertTrue(items.size >= 50)
        assertEquals(items.size, items.map { it.id }.toSet().size)
        assertEquals(listOf("passkeys"), items.filter { it.isSecure }.map { it.id })
    }

    @Test
    fun unauthenticatedWritesFail() = runTest {
        assertFalse(repository.submitIncidentReport("example.invalid", "test"))
        assertFalse(repository.submitVolunteerRegistration("Test", "Test", ""))
    }
}
