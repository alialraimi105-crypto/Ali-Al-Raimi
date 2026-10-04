package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

const val PROFILE_AVATAR_URL = "https://lh3.googleusercontent.com/aida/AEtjO1VDtGVWgxX-A0WA4Gem-igSMN41o7SAPBtX9JUtw7YvgfQJ_JIQp-HWQEddbTVVou0eyKgXaOOAPZDc2QP25BQwFr3ONt1RgVyG38ipesOrOmYdX3M4QBfK5nCldXrj19j5kkKcXxTDLNREE2La2yoeZjVfr02OGl3wo5gHvD9N0luYXnOIOz3wVy_Xwj31idxP9YVv1XTXmqew0cdyPXgaVszZ9-hGt5XDvDsEd65s8NBeYwwRa2qwuQ"
const val BRAND_LOGO_URL = "https://lh3.googleusercontent.com/aida/AEtjO1UWnpVQyXbO741LdsG-FlOlWVNVBNMUUGSgE-mw56SdfjKLtl9kzcV8Ct6e8SPa1TCjX81NWkQb0tC-xb03mq2ODvgCQHYVfX1whRvAllE3qxGmlLz-KRRiwGIWBVBkwFEYh1_-w-po3uMafYmZlAq4fLxqOR1VJ2F_QGByuYi_Vqx4beZ6IErtdQzhIm_xmRm8_UzuzFgfNupuGE-sycgGS4SaF3bmK56hh6uc_gVOmg0YAUA5k6qEFQ"

@Composable
fun MusabbihunHeader(
    onCalendarClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right side (in RTL): Brand identity with logo and typography
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Profile / Brand Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = BRAND_LOGO_URL,
                    contentDescription = "شعار مسبحون",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column {
                Text(
                    text = "مسبّحون",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "رفيقك اليومي للذكر",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Left side (in RTL): Quick actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Calendar button
            IconButton(
                onClick = onCalendarClick,
                modifier = Modifier.testTag("header_calendar_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "التقويم",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Settings button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("header_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "الإعدادات",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Notifications button with notification badge dot
            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("header_notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "الإشعارات",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                )
            }
        }
    }
}
