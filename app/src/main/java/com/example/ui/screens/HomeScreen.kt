package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Screen
import com.example.model.SecurityAlert
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    latestAlert: SecurityAlert?,
    lastSyncTime: Long,
    onNavigate: (Screen) -> Unit,
    onQuickScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Prominent Hero Mascot Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = DeepBlueAccentSoft,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DeepBluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TRỢ LÝ AN TOÀN SỐ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBluePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Không gian số an toàn\nbắt đầu từ bạn!",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 27.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Mascot Illustration Thumbnail
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(DeepBlueAccentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.trustnet_mascot_hero_1794196429617),
                                contentDescription = "TrustNet Robot Mascot",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent Quick Scan Pill Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onQuickScanClick() }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("home_quick_scan_bar"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DeepBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Dán link, tin nhắn hoặc SĐT kiểm tra...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(DeepBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Quét ngay",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }


        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giải pháp an toàn số",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Đầy đủ & Miễn phí",
                    fontSize = 12.sp,
                    color = DeepBluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 3. PROMINENT 6 CORE SOLUTIONS (2-Column Grid of High-Visual Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: TrustCheck & TrustPrivacy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustCheck",
                        subtitle = "Kiểm tra Link, SMS & SĐT",
                        tag = "AI 2.0",
                        tagBg = DeepBlueAccentSoft,
                        tagColor = DeepBluePrimary,
                        icon = Icons.Default.Search,
                        gradientColors = listOf(DeepBluePrimary, DeepBlueGlow),
                        testTag = "home_nav_trust_check",
                        onClick = { onNavigate(Screen.TRUST_CHECK) }
                    )

                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustPrivacy",
                        subtitle = "Bảo mật tài khoản & quyền",
                        tag = "Bảo vệ",
                        tagBg = SafetyGreenSoft,
                        tagColor = SafetyGreen,
                        icon = Icons.Default.Lock,
                        gradientColors = listOf(Color(0xFF059669), SafetyGreen),
                        testTag = "home_nav_trust_privacy",
                        onClick = { onNavigate(Screen.TRUST_PRIVACY) }
                    )
                }

                // Row 2: TrustCare & TrustAI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustCare",
                        subtitle = "Ứng xử & tâm lý học đường",
                        tag = "SOS 24/7",
                        tagBg = TrustCareOrangeSoft,
                        tagColor = TrustCareOrange,
                        icon = Icons.Default.Favorite,
                        gradientColors = listOf(Color(0xFFC2410C), TrustCareOrange),
                        testTag = "home_nav_trust_care",
                        onClick = { onNavigate(Screen.TRUST_CARE) }
                    )

                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustAI",
                        subtitle = "Nhận diện Deepfake AI",
                        tag = "Flash AI",
                        tagBg = TrustAIPurpleSoft,
                        tagColor = TrustAIPurple,
                        icon = Icons.Default.Memory,
                        gradientColors = listOf(Color(0xFF6D28D9), TrustAIPurple),
                        testTag = "home_nav_trust_ai",
                        onClick = { onNavigate(Screen.TRUST_AI) }
                    )
                }

                // Row 3: TrustLab & TrustHelp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustLab",
                        subtitle = "Luyện tập & thử thách số",
                        tag = "Quiz Game",
                        tagBg = WarningAmberSoft,
                        tagColor = Color(0xFFB45309),
                        icon = Icons.Default.School,
                        gradientColors = listOf(Color(0xFFD97706), WarningAmber),
                        testTag = "home_nav_trust_lab",
                        onClick = { onNavigate(Screen.TRUST_LAB) }
                    )

                    ProminentFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "TrustHelp",
                        subtitle = "Tổng đài ứng cứu khẩn cấp",
                        tag = "Hotline 111",
                        tagBg = Color(0xFFFEE2E2),
                        tagColor = DangerRed,
                        icon = Icons.Default.HelpCenter,
                        gradientColors = listOf(Color(0xFFB91C1C), DangerRed),
                        testTag = "home_nav_trust_help",
                        onClick = { onNavigate(Screen.TRUST_HELP) }
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Cộng đồng & Truyền thông",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 4. Community & Media Banners (Visually Elevated)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // TrustNet Media
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(3.dp, RoundedCornerShape(24.dp))
                        .clickable { onNavigate(Screen.TRUST_MEDIA) }
                        .testTag("home_card_media"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(88.dp)
                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                .background(Brush.verticalGradient(listOf(DeepBluePrimary, DeepBlueGlow)))
                        )
                        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DeepBlueAccentSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = DeepBluePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "TrustNet Media",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cổng thông tin & thủ đoạn mới",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Volunteer Team
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(3.dp, RoundedCornerShape(24.dp))
                        .clickable { onNavigate(Screen.VOLUNTEER_TEAM) }
                        .testTag("home_card_volunteer"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(88.dp)
                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                .background(Brush.verticalGradient(listOf(Color(0xFF7C3AED), Color(0xFFA78BFA))))
                        )
                        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(TrustAIPurpleSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = TrustAIPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Xung Kích Số",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tình nguyện an toàn số",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
private fun ProminentFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    tag: String,
    tagBg: Color,
    tagColor: Color,
    icon: ImageVector,
    gradientColors: List<Color>,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Harmonious vertical color column on the left
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(Brush.verticalGradient(gradientColors))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gradient Icon Box
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Tag Chip
                    Surface(
                        color = tagBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}
