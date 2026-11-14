package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BottomTab
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DeepBlueAccent
import com.example.ui.theme.DeepBluePrimary

@Composable
fun TrustNetBottomNav(
    selectedTab: BottomTab,
    unreadAlertsCount: Int,
    onTabSelected: (BottomTab) -> Unit,
    onCenterShieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Capsule Dock Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(33.dp),
                    spotColor = DeepBluePrimary.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(33.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                DockItem(
                    icon = Icons.Default.Home,
                    contentDescription = "Trang chủ",
                    isSelected = selectedTab == BottomTab.HOME,
                    testTag = "nav_home",
                    onClick = { onTabSelected(BottomTab.HOME) }
                )

                // Tab 2: History / Search
                DockItem(
                    icon = Icons.Default.Search,
                    contentDescription = "Tìm kiếm / Lịch sử",
                    isSelected = selectedTab == BottomTab.HISTORY,
                    testTag = "nav_history",
                    onClick = { onTabSelected(BottomTab.HISTORY) }
                )

                // Center Button: ICONIC SHIELD (KHIÊN AN TOÀN) AS IN ORIGINAL DESIGN
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(DeepBluePrimary)
                        .clickable { onCenterShieldClick() }
                        .testTag("nav_center_shield_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Quét nhanh bảo mật",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Tab 4: Notifications / Alerts
                DockItem(
                    icon = Icons.Default.Assessment,
                    contentDescription = "Thông báo",
                    isSelected = selectedTab == BottomTab.NOTIFICATIONS,
                    badgeCount = unreadAlertsCount,
                    testTag = "nav_notifications",
                    onClick = { onTabSelected(BottomTab.NOTIFICATIONS) }
                )

                // Tab 5: Profile (matches active deep blue badge)
                DockItem(
                    icon = Icons.Default.Person,
                    contentDescription = "Hồ sơ",
                    isSelected = selectedTab == BottomTab.PROFILE,
                    testTag = "nav_profile",
                    onClick = { onTabSelected(BottomTab.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun DockItem(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    testTag: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) DeepBlueAccent else Color.Transparent,
        label = "dockItemBg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "dockItemIcon"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (badgeCount > 0 && !isSelected) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(text = "$badgeCount", fontSize = 9.sp)
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
