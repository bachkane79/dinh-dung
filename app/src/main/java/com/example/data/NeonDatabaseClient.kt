package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.QueryType
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.model.SecurityAlert
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NeonDatabaseClient {

    companion object {
        private const val TAG = "NeonDatabaseClient"
        private const val NEON_SQL_URL = "https://ep-sweet-base-be3qnotp-pooler.c-9.us-east-2.aws.neon.tech/sql"
    }

    private fun getConnectionString(): String {
        val raw = try { BuildConfig.NEON_CONNECTION_STRING } catch (_: Exception) { "" }
        return if (raw.isNotBlank() && raw != "MY_NEON_CONNECTION_STRING") raw.trim() else ""
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun executeQuery(sql: String): JSONObject? = withContext(Dispatchers.IO) {
        val connectionString = getConnectionString()
        if (connectionString.isBlank()) {
            Log.w(TAG, "NEON_CONNECTION_STRING is not configured in Secrets; skipping remote SQL.")
            return@withContext null
        }

        try {
            val bodyObj = JSONObject().apply {
                put("query", sql)
            }
            val request = Request.Builder()
                .url(NEON_SQL_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Neon-Connection-String", connectionString)
                .post(bodyObj.toString().toRequestBody(jsonMediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                JSONObject(responseBody)
            } else {
                Log.e(TAG, "Neon query error (${response.code}): $responseBody")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Neon request failed: ${e.message}", e)
            null
        }
    }

    suspend fun getScanHistory(): List<ScanResult> = withContext(Dispatchers.IO) {
        val resultList = mutableListOf<ScanResult>()
        val json = executeQuery("SELECT id, target, type, risk_score, risk_level, red_flags, recommendations, extract(epoch from created_at)*1000 as ts FROM scan_history ORDER BY created_at DESC LIMIT 50;")
        if (json != null && json.has("rows")) {
            val rows = json.getJSONArray("rows")
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                val flags = row.optString("red_flags").split("||").filter { it.isNotBlank() }
                val recs = row.optString("recommendations").split("||").filter { it.isNotBlank() }
                val typeStr = row.optString("type", "URL")
                val levelStr = row.optString("risk_level", "SAFE")

                resultList.add(
                    ScanResult(
                        id = row.optString("id"),
                        target = row.optString("target"),
                        type = try { QueryType.valueOf(typeStr) } catch (_: Exception) { QueryType.URL },
                        riskScore = row.optInt("risk_score", 0),
                        riskLevel = try { RiskLevel.valueOf(levelStr) } catch (_: Exception) { RiskLevel.SAFE },
                        redFlags = flags,
                        recommendations = recs,
                        timestamp = row.optLong("ts", System.currentTimeMillis())
                    )
                )
            }
        }
        resultList
    }

    suspend fun insertScanResult(scan: ScanResult): Boolean = withContext(Dispatchers.IO) {
        val safeTarget = scan.target.replace("'", "''")
        val safeFlags = scan.redFlags.joinToString("||").replace("'", "''")
        val safeRecs = scan.recommendations.joinToString("||").replace("'", "''")

        val sql = "INSERT INTO scan_history (id, target, type, risk_score, risk_level, red_flags, recommendations) " +
                "VALUES ('${scan.id}', '$safeTarget', '${scan.type.name}', ${scan.riskScore}, '${scan.riskLevel.name}', '$safeFlags', '$safeRecs') " +
                "ON CONFLICT (id) DO NOTHING;"

        val res = executeQuery(sql)
        res != null
    }

    suspend fun getSecurityAlerts(): List<SecurityAlert> = withContext(Dispatchers.IO) {
        val alerts = mutableListOf<SecurityAlert>()
        val json = executeQuery("SELECT id, title, summary, tag, time_ago, is_urgent FROM security_alerts ORDER BY created_at DESC LIMIT 20;")
        if (json != null && json.has("rows")) {
            val rows = json.getJSONArray("rows")
            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                alerts.add(
                    SecurityAlert(
                        id = row.optString("id"),
                        title = row.optString("title"),
                        summary = row.optString("summary"),
                        tag = row.optString("tag"),
                        timeAgo = row.optString("time_ago"),
                        isUrgent = row.optBoolean("is_urgent", false)
                    )
                )
            }
        }
        alerts
    }

    suspend fun insertIncidentReport(target: String, details: String): Boolean = withContext(Dispatchers.IO) {
        val safeTarget = target.replace("'", "''")
        val safeDetails = details.replace("'", "''")
        val id = "rep-${System.currentTimeMillis()}"

        val sql = "INSERT INTO incident_reports (id, target, details, status) VALUES ('$id', '$safeTarget', '$safeDetails', 'PENDING');"
        val res = executeQuery(sql)
        res != null
    }

    suspend fun registerVolunteer(name: String, school: String, phone: String): Boolean = withContext(Dispatchers.IO) {
        val safeName = name.replace("'", "''")
        val safeSchool = school.replace("'", "''")
        val safePhone = phone.replace("'", "''")
        val id = "vol-${System.currentTimeMillis()}"

        val sql = "INSERT INTO volunteer_registrations (id, full_name, school, phone) VALUES ('$id', '$safeName', '$safeSchool', '$safePhone');"
        val res = executeQuery(sql)
        res != null
    }

    suspend fun clearHistory(): Boolean = withContext(Dispatchers.IO) {
        val res = executeQuery("TRUNCATE TABLE scan_history;")
        res != null
    }

    suspend fun authenticateUser(email: String, passwordHash: String): UserProfile? = withContext(Dispatchers.IO) {
        val safeEmail = email.replace("'", "''")
        val safePass = passwordHash.replace("'", "''")
        val json = executeQuery("SELECT id, name, email, safety_rank, level_score, total_scans FROM users WHERE email = '$safeEmail' AND password_hash = '$safePass' LIMIT 1;")
        if (json != null && json.has("rows")) {
            val rows = json.getJSONArray("rows")
            if (rows.length() > 0) {
                val r = rows.getJSONObject(0)
                return@withContext UserProfile(
                    id = r.optString("id"),
                    name = r.optString("name", "Người dùng TrustNet"),
                    email = r.optString("email", email),
                    isLoggedIn = true,
                    safetyRank = r.optString("safety_rank", "Hiệp sĩ An toàn"),
                    levelScore = r.optInt("level_score", 1820),
                    totalScans = r.optInt("total_scans", 18)
                )
            }
        }
        null
    }

    suspend fun registerUser(name: String, email: String, passwordHash: String): UserProfile? = withContext(Dispatchers.IO) {
        val safeName = name.replace("'", "''")
        val safeEmail = email.replace("'", "''")
        val safePass = passwordHash.replace("'", "''")
        val userId = "usr-${System.currentTimeMillis()}"

        val sql = """
            INSERT INTO users (id, name, email, password_hash, safety_rank, level_score, total_scans)
            VALUES ('$userId', '$safeName', '$safeEmail', '$safePass', 'Hiệp sĩ An toàn', 1820, 18)
            ON CONFLICT (email) DO UPDATE SET name = EXCLUDED.name
            RETURNING id, name, email, safety_rank, level_score, total_scans;
        """.trimIndent()

        val json = executeQuery(sql)
        if (json != null && json.has("rows")) {
            val rows = json.getJSONArray("rows")
            if (rows.length() > 0) {
                val r = rows.getJSONObject(0)
                return@withContext UserProfile(
                    id = r.optString("id", userId),
                    name = r.optString("name", name),
                    email = r.optString("email", email),
                    isLoggedIn = true,
                    safetyRank = r.optString("safety_rank", "Hiệp sĩ An toàn"),
                    levelScore = r.optInt("level_score", 1820),
                    totalScans = r.optInt("total_scans", 18)
                )
            }
        }
        null
    }
}
