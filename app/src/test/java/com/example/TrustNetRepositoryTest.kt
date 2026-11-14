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
    fun unverifiedDomainIsNeverCertifiedSafe() = runTest {
        val result = repository.analyzeTarget("https://example.invalid", QueryType.URL)
        assertNotEquals(RiskLevel.SAFE, result.riskLevel)
        assertFalse(result.isDomainValid)
    }

    @Test
    fun loginPathAloneDoesNotProvePhishing() = runTest {
        // Syntax analysis without a Gemini key.
        org.junit.Assume.assumeTrue(repository.aiService.getEffectiveApiKey().isBlank())
        val result = repository.analyzeTarget("https://example.com/login", QueryType.URL)
        assertEquals(RiskLevel.UNKNOWN, result.riskLevel)
    }

    @Test
    fun unauthenticatedWritesFail() = runTest {
        assertFalse(repository.submitIncidentReport("example.invalid", "test"))
        assertFalse(repository.submitVolunteerRegistration("Test", "Test", ""))
    }
}
