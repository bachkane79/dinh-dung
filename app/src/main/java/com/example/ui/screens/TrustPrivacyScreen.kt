package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.model.PrivacyAuditItem

@Composable
fun TrustPrivacyScreen(
    privacyItems: List<PrivacyAuditItem>,
    onToggleItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var onlyPending by rememberSaveable { mutableStateOf(false) }
    val completed = privacyItems.count { it.isSecure }
    val groups = privacyItems.groupBy { it.category }

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("trust_privacy_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Checklist bảo mật", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(6.dp))
            Text("Đã hoàn thành $completed/${privacyItems.size} việc")
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { completed.toFloat() / privacyItems.size.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth()
            )
            FilterChip(
                selected = onlyPending, onClick = { onlyPending = !onlyPending },
                label = { Text("Chỉ hiện việc chưa làm") }
            )
        }
        if (onlyPending && completed == privacyItems.size) item { Text("Bạn đã hoàn thành checklist.") }
        groups.forEach { (category, group) ->
            val visible = if (onlyPending) group.filterNot { it.isSecure } else group
            if (visible.isNotEmpty()) {
                item(key = category) {
                    Column(Modifier.padding(top = 10.dp)) {
                        Text(category, style = MaterialTheme.typography.titleMedium)
                        Text("${group.count { it.isSecure }}/${group.size} đã làm", style = MaterialTheme.typography.bodySmall)
                    }
                }
                items(visible, key = { it.id }) { task ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth().testTag("privacy_item_${task.id}")
                                .toggleable(task.isSecure, role = Role.Checkbox, onValueChange = { onToggleItem(task.id) })
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(task.isSecure, onCheckedChange = null)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(task.title, style = MaterialTheme.typography.titleSmall)
                                Text(task.description, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
