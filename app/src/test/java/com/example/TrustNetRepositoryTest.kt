package com.example

import com.example.data.TrustNetRepository
import com.example.model.QueryType
import com.example.model.RiskLevel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
class TrustNetRepositoryTest {

    private val repository = TrustNetRepository()

    @Test
    fun testInitialScanHistoryNotEmpty() {
        val history = repository.scanHistory.value
        assertTrue(history.isNotEmpty())
        assertEquals(92, history.first().riskScore)
    }

    @Test
    fun testPhishingScanDetectsHighRisk() = runTest {
        val result = repository.analyzeTarget("http://vneid-xacthuc-taikhoan.vn/login?otp=true", QueryType.URL)
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.riskScore >= 80)
        assertTrue(result.redFlags.isNotEmpty())
    }

    @Test
    fun testLegitimateScanDetectsSafe() = runTest {
        val result = repository.analyzeTarget("https://trustnet.edu.vn", QueryType.URL)
        assertEquals(RiskLevel.SAFE, result.riskLevel)
        assertTrue(result.riskScore < 30)
    }

    @Test
    fun testDeepfakeAnalysisReturnsAccurateScores() = runTest {
        val authenticResult = repository.analyzeDeepfake("fuji.jpg", isSimulatedFake = false)
        assertTrue(authenticResult.isReal)
        assertTrue(authenticResult.confidence >= 90)

        val fakeResult = repository.analyzeDeepfake("deepfake.mp4", isSimulatedFake = true)
        assertTrue(!fakeResult.isReal)
        assertTrue(fakeResult.generativePatterns > 70)
    }
}
