package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Screen
import com.example.model.SyncStatus

@Composable
fun TrustNetTopBar(
    currentScreen: Screen,
    syncStatus: SyncStatus,
    unreadAlertsCount: Int,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (currentScreen) {
        Screen.HOME -> "TrustNet"
        Screen.TRUST_CHECK -> "TrustCheck"
        Screen.TRUST_PRIVACY -> "TrustPrivacy"
        Screen.TRUST_CARE -> "TrustCare"
        Screen.TRUST_AI -> "TrustAI"
        Screen.TRUST_LAB -> "TrustLab"
        Screen.TRUST_HELP -> "TrustHelp"
        Screen.TRUST_MEDIA -> "TrustMedia"
        Screen.VOLUNTEER_TEAM -> "Xung Kích Số"
        Screen.HISTORY -> "Lịch sử"
        Screen.NOTIFICATIONS -> "Thông báo"
        Screen.PROFILE -> "Hồ sơ"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Left Button: Circular White Card (Back button or Logo)
        if (currentScreen != Screen.HOME) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onBackClick() }
                    .testTag("back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(44.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onNotificationClick() }
                    .testTag("home_alerts_button"),
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        if (unreadAlertsCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text("$unreadAlertsCount", fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Center Title: Clean, minimal & bold
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Right Button: Minimalist '?' Help Button (opens contextual guide)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(44.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onHelpClick() }
                .testTag("screen_help_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Hướng dẫn",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
