package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QueryType
import com.example.model.RiskLevel
import com.example.model.ScanResult
import com.example.ui.components.RiskGauge
import com.example.ui.theme.*

@Composable
fun TrustCheckScreen(
    scanResult: ScanResult?,
    isScanning: Boolean,
    onPerformScan: (String, QueryType) -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var queryInput by remember { mutableStateOf("https://vneid-xacthuc-taikhoan.vn/login.php?ref=otp") }
    var selectedType by remember { mutableStateOf(QueryType.URL) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trust_check_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Input Card with harmonious Deep Blue vertical color column
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
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(Brush.verticalGradient(listOf(DeepBluePrimary, DeepBlueGlow)))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp)
                    ) {
                        // Segmented Type Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                QueryType.URL to "Tên miền / Link",
                                QueryType.SMS to "Tin nhắn",
                                QueryType.PHONE to "Số điện thoại"
                            ).forEach { (type, label) ->
                                val isSelected = selectedType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) DeepBluePrimary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { selectedType = type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = queryInput,
                            onValueChange = { queryInput = it },
                            placeholder = {
                                Text(
                                    text = when (selectedType) {
                                        QueryType.URL -> "Dán liên kết hoặc tên miền..."
                                        QueryType.SMS -> "Dán nội dung tin nhắn SMS..."
                                        QueryType.PHONE -> "Nhập số điện thoại cần tra cứu..."
                                        QueryType.IMAGE -> "Nhập tên tệp ảnh..."
                                    },
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trust_check_input"),
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Scan Button
                        Button(
                            onClick = {
                                if (queryInput.isNotBlank() && !isScanning) {
                                    onPerformScan(queryInput.trim(), selectedType)
                                }
                            },
                            enabled = !isScanning && queryInput.isNotBlank(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBluePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("trust_check_submit_button")
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("AI Đang Phân Tích Tên Miền...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kiểm tra ngay", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. Risk Gauge Result Card & Domain Validation
        if (scanResult != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(26.dp)),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Semi-circular Risk Gauge
                        RiskGauge(
                            score = scanResult.riskScore,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val (statusText, statusBg, statusColor) = when (scanResult.riskLevel) {
                            RiskLevel.SAFE -> Triple("MỨC ĐỘ AN TOÀN", Color(0xFFEAF9EE), SafetyGreen)
                            RiskLevel.MODERATE -> Triple("RỦI RO TRUNG BÌNH", Color(0xFFFEF3C7), WarningAmber)
                            RiskLevel.HIGH_RISK -> Triple("RỦI RO CAO / ĐỘC HẠI", Color(0xFFFEE2E2), DangerRed)
                        }

                        Surface(
                            color = statusBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        // Domain Legitimacy Card
                        if (scanResult.analyzedDomain.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Tên miền đã phân tích:",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = scanResult.analyzedDomain,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Surface(
                                        color = if (scanResult.isDomainValid) SafetyGreenSoft else Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (scanResult.isDomainValid) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = if (scanResult.isDomainValid) SafetyGreen else DangerRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (scanResult.isDomainValid) "Tên miền hợp lệ" else "Tên miền bất thường",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (scanResult.isDomainValid) SafetyGreen else DangerRed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Red Flags with Risk Points (+X points)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = DangerRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Dấu hiệu đáng ngờ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "Điểm rủi ro",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (scanResult.redFlagPoints.isNotEmpty()) {
                            scanResult.redFlagPoints.forEach { flagPoint ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (flagPoint.isSevere) DangerRed else WarningAmber)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = flagPoint.title,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        color = if (flagPoint.isSevere) DangerRed.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "+${flagPoint.points} điểm",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (flagPoint.isSevere) DangerRed else WarningAmber,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            scanResult.redFlags.forEach { flag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(DangerRed)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = flag,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recommendations Card
            if (scanResult.recommendations.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DeepBlueAccentSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = DeepBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Khuyến nghị an toàn",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            scanResult.recommendations.forEach { rec ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(DeepBluePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = rec,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Report Button
            item {
                OutlinedButton(
                    onClick = onReportClick,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("report_scam_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Báo cáo sự cố này lên mạng lưới an toàn",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
