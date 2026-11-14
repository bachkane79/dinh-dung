package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.CareMessage

@Composable
fun TrustCareScreen(
    messages: List<CareMessage>,
    isAnalyzing: Boolean,
    error: String?,
    onSend: (String) -> Unit,
    onRetry: () -> Unit,
    onReset: () -> Unit
) {
    var draft by rememberSaveable { mutableStateOf("") }
    val scroll = rememberLazyListState()
    LaunchedEffect(messages.size, isAnalyzing, error) {
        if (messages.isNotEmpty()) {
            scroll.animateScrollToItem(messages.size - if (isAnalyzing || error != null) 0 else 1)
        }
    }

    Column(Modifier.fillMaxSize().imePadding().padding(horizontal = 16.dp).testTag("trust_care_screen")) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Trò chuyện với TrustCare", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            TextButton(enabled = messages.isNotEmpty(), onClick = { draft = ""; onReset() }) { Text("Cuộc trò chuyện mới") }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = scroll,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Text("Bạn đang gặp chuyện gì trên mạng? Hãy kể tình huống và điều bạn muốn được giúp.")
                    Spacer(Modifier.height(8.dp))
                    listOf("Mình đang bị bắt nạt trên mạng", "Có người đe dọa phát tán ảnh của mình",
                        "Mình vừa bấm vào một đường link đáng ngờ").forEach { example ->
                        SuggestionChip(onClick = { draft = example }, label = { Text(example) })
                    }
                }
            }
            items(messages) { message ->
                Box(Modifier.fillMaxWidth(), contentAlignment = if (message.fromUser) Alignment.CenterEnd else Alignment.CenterStart) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.88f),
                        shape = MaterialTheme.shapes.large,
                        color = if (message.fromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(if (message.fromUser) "Bạn" else "TrustCare", style = MaterialTheme.typography.labelMedium)
                            Text(message.text)
                        }
                    }
                }
            }
            if (isAnalyzing) item { Text("TrustCare đang trả lời…") }
            if (error != null) item {
                Text(error, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onRetry) { Text("Thử gửi lại") }
            }
        }
        Text(
            "Các lượt gần đây được gửi tới Gemini. Tránh nhập mật khẩu, OTP hoặc thông tin nhận dạng.",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = draft, onValueChange = { if (it.length <= 2000) draft = it },
                modifier = Modifier.weight(1f), placeholder = { Text("Nhập tin nhắn…") }, maxLines = 4
            )
            Button(
                enabled = draft.isNotBlank() && !isAnalyzing,
                onClick = { onSend(draft.trim()); draft = "" }
            ) { Text("Gửi") }
        }
    }
}
