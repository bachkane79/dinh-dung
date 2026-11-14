package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.MediaInput
import com.example.model.DeepfakeAnalysisResult

@Composable
fun TrustAIScreen(
    analysisResult: DeepfakeAnalysisResult?,
    isAnalyzing: Boolean,
    onAnalyze: (MediaInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val resolver = LocalContext.current.contentResolver
    var tab by remember { mutableIntStateOf(0) }
    var selection by remember { mutableStateOf<MediaInput?>(null) }
    var showResult by remember { mutableStateOf(true) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selection = MediaInput(resolver, uri)
            showResult = false
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("trust_ai_screen"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        listOf("Nhận diện ảnh", "Nhận diện video").forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = tab == index, enabled = !isAnalyzing,
                                onClick = {
                                    if (tab != index) {
                                        tab = index
                                        selection = null
                                        showResult = false
                                    }
                                },
                                shape = SegmentedButtonDefaults.itemShape(index, 2),
                                label = { Text(label) }
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { picker.launch(if (tab == 0) "image/*" else "video/*") },
                        enabled = !isAnalyzing,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (selection == null) "Chọn tệp" else "Đổi tệp đã chọn") }
                    Text("Tệp tối đa 10 MB. Khi phân tích, tệp được gửi tới Gemini.")
                    Button(
                        onClick = { selection?.let { showResult = true; onAnalyze(it) } },
                        enabled = selection != null && !isAnalyzing,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (isAnalyzing) "Đang phân tích…" else "Phân tích") }
                }
            }
        }
        if (showResult && analysisResult != null) {
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Điểm nghi vấn AI: ${analysisResult.suspicionScore}/100",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(when (analysisResult.suspicionScore) {
                            in 0..39 -> "Ít dấu hiệu quan sát được"
                            in 40..69 -> "Cần kiểm tra thêm"
                            else -> "Có nhiều dấu hiệu đáng ngờ"
                        })
                        Text(analysisResult.fileName)
                        Text(analysisResult.summaryNote)
                        analysisResult.detailedFindings.forEach { Text("• $it") }
                        Text("Sàng lọc bằng ${analysisResult.detectorSource}. Không xác thực được nguồn gốc của tệp.")
                    }
                }
            }
        }
    }
}
