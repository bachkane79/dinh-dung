package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DeepfakeAnalysisResult
import com.example.ui.theme.*

@Composable
fun TrustAIScreen(
    analysisResult: DeepfakeAnalysisResult?,
    isAnalyzing: Boolean,
    customApiUrl: String,
    onCustomApiUrlChange: (String) -> Unit,
    onAnalyze: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVideoTab by remember { mutableStateOf(false) }
    var showApiSettings by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trust_ai_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Upload & Detector Box with Purple Vertical Color Column
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Harmonious Purple vertical color column
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(280.dp)
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF6D28D9), TrustAIPurple)))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Segmented Tabs
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (!isVideoTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                                        .clickable { isVideoTab = false }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nhận diện ảnh",
                                        fontSize = 13.sp,
                                        fontWeight = if (!isVideoTab) FontWeight.Bold else FontWeight.Medium,
                                        color = if (!isVideoTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isVideoTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                                        .clickable { isVideoTab = true }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nhận diện video",
                                        fontSize = 13.sp,
                                        fontWeight = if (isVideoTab) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isVideoTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Image Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.trustai_sample_photo_1794196501475),
                                contentDescription = "Mẫu phân tích AI",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onAnalyze(false) },
                                enabled = !isAnalyzing,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TrustAIPurple,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("ai_analyze_authentic_button")
                            ) {
                                if (isAnalyzing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("Phân tích ảnh thật", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { onAnalyze(true) },
                                enabled = !isAnalyzing,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("ai_analyze_fake_button")
                            ) {
                                Text("Thử Deepfake giả", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom API Detector toggle
                        Row(
                            modifier = Modifier
                                .clickable { showApiSettings = !showApiSettings }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (showApiSettings) Icons.Default.ExpandLess else Icons.Default.Tune,
                                contentDescription = null,
                                tint = TrustAIPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cài đặt API Check AI (Meta AI / Custom endpoint)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TrustAIPurple
                            )
                        }

                        AnimatedVisibility(visible = showApiSettings) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customApiUrl,
                                    onValueChange = onCustomApiUrlChange,
                                    placeholder = {
                                        Text(
                                            "https://api.meta.ai/v1/content-credentials...",
                                            fontSize = 11.sp
                                        )
                                    },
                                    label = { Text("URL API Check AI tùy chọn", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_ai_detector_url_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Mặc định sử dụng Meta Content Credentials & Gemini Vision AI.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Results Card
        if (analysisResult != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (analysisResult.isReal) SafetyGreenSoft else Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (analysisResult.isReal) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (analysisResult.isReal) SafetyGreen else DangerRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (analysisResult.isReal) "Ảnh thật (Authentic)" else "Cảnh báo Deepfake AI",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Nguồn: ${analysisResult.detectorSource}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // AI Probability Tag
                            Surface(
                                color = if (analysisResult.aiProbability > 50) Color(0xFFFEE2E2) else SafetyGreenSoft,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${analysisResult.aiProbability}% AI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (analysisResult.aiProbability > 50) DangerRed else SafetyGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = analysisResult.summaryNote,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Technical Metrics
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AiMetricRow("Ánh sáng & Phản chiếu tự nhiên", "${analysisResult.lightingConsistency}%")
                            AiMetricRow("Cấu trúc hình học khuôn mặt", "${analysisResult.facialGeometry}%")
                            AiMetricRow("Dấu vết mô hình AI tạo sinh", "${analysisResult.generativePatterns}%")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Detailed Findings List
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Chi tiết phân tích Meta & AI:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                analysisResult.detailedFindings.forEach { finding ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 5.dp)
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(TrustAIPurple)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = finding,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun AiMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
