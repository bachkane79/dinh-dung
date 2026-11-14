package com.example.data

import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NeonDatabaseClient {
    @Volatile private var token: String? = null
    private val httpClient = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()
    private val json = "application/json".toMediaType()

    private suspend fun request(path: String, method: String = "GET", body: JSONObject? = null): JSONObject =
        withContext(Dispatchers.IO) {
            val base = BuildConfig.TRUSTNET_API_URL.trimEnd('/')
            if (base.isBlank()) throw IOException("TrustNet API chưa được cấu hình")
            val sessionToken = token
            if (path in setOf("history", "reports", "volunteers", "logout") && sessionToken == null) {
                throw IOException("TrustNet API yêu cầu đăng nhập")
            }
            val request = Request.Builder().url("$base/$path")
                .method(method, body?.toString()?.toRequestBody(json))
            sessionToken?.let { request.header("Authorization", "Bearer $it") }
            httpClient.newCall(request.build()).execute().use { response ->
                if (!response.isSuccessful) throw IOException("TrustNet API HTTP ${response.code}")
                JSONObject(response.body?.string() ?: throw IOException("Empty response"))
            }
        }

    suspend fun getScanHistory(): List<ScanResult> {
        val rows = request("history").getJSONArray("rows")
        return List(rows.length()) { index ->
            val row = rows.getJSONObject(index)
            ScanResult(
                id = row.getString("id"), target = row.getString("target"),
                type = QueryType.valueOf(row.getString("type")), riskScore = row.getInt("risk_score"),
                riskLevel = RiskLevel.entries.find { it.name == row.optString("risk_level") } ?: RiskLevel.UNKNOWN,
                redFlags = row.optString("red_flags").split("||").filter { it.isNotBlank() },
                recommendations = row.optString("recommendations").split("||").filter { it.isNotBlank() },
                isDomainValid = false, timestamp = row.optLong("ts", System.currentTimeMillis())
            )
        }
    }

    suspend fun insertScanResult(scan: ScanResult) {
        request("history", "POST", JSONObject()
            .put("id", scan.id).put("target", scan.target).put("type", scan.type.name)
            .put("risk_score", scan.riskScore).put("risk_level", scan.riskLevel.name)
            .put("red_flags", scan.redFlags.joinToString("||"))
            .put("recommendations", scan.recommendations.joinToString("||")))
    }

    suspend fun getSecurityAlerts(): List<SecurityAlert> {
        val rows = request("alerts").getJSONArray("rows")
        return List(rows.length()) { index ->
            val row = rows.getJSONObject(index)
            SecurityAlert(row.getString("id"), row.getString("title"), row.getString("summary"),
                row.getString("tag"), row.getString("time_ago"), row.optBoolean("is_urgent"))
        }
    }

    suspend fun insertIncidentReport(target: String, details: String) {
        request("reports", "POST", JSONObject().put("target", target).put("details", details))
    }

    suspend fun registerVolunteer(name: String, school: String, phone: String) {
        request("volunteers", "POST", JSONObject().put("name", name).put("school", school).put("phone", phone))
    }

    suspend fun clearHistory() { request("history", "DELETE") }

    suspend fun authenticate(email: String, password: String, name: String? = null): UserProfile {
        val response = request(if (name == null) "login" else "register", "POST",
            JSONObject().put("email", email).put("password", password).put("name", name))
        val user = response.getJSONObject("user")
        val profile = UserProfile(id = user.getString("id"), name = user.getString("name"),
            email = user.getString("email"), isLoggedIn = true, levelScore = user.getInt("level_score"),
            totalScans = 0, protectedHours = 0)
        token = response.getString("token")
        return profile
    }

    suspend fun logout() {
        try {
            request("logout", "POST", JSONObject())
        } finally {
            token = null
        }
    }
}
