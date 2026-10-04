package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NavigationDestination
import com.example.data.model.ScreenTab
import com.example.ui.components.MusabbihunBottomNav
import com.example.ui.components.MusabbihunHeader
import com.example.ui.screens.AdhkarDetailScreen
import com.example.ui.screens.AdhkarScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SurahReaderScreen
import com.example.ui.screens.TasbeehScreen
import com.example.ui.theme.*
import com.example.viewmodel.MusabbihunViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MusabbihunViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkTheme = when (uiState.appThemeMode) {
                "داكن" -> true
                "فاتح" -> false
                else -> isSystemInDarkTheme()
            }

            MusabbihunTheme(darkTheme = isDarkTheme) {
                // Arabic Native RTL Layout Direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MusabbihunApp(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MusabbihunApp(
    viewModel: MusabbihunViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMainTabs = uiState.currentDestination == NavigationDestination.MAIN_TABS

    // Back handling for sub-screens
    BackHandler(enabled = !isMainTabs) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isMainTabs) {
                MusabbihunHeader(
                    onCalendarClick = { viewModel.navigateTo(NavigationDestination.CALENDAR) },
                    onSettingsClick = { viewModel.navigateTo(NavigationDestination.SETTINGS) },
                    onNotificationClick = { viewModel.showToast("لا توجد إشعارات جديدة حالياً") }
                )
            }
        },
        bottomBar = {
            if (isMainTabs) {
                MusabbihunBottomNav(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Destination Content
            when (uiState.currentDestination) {
                NavigationDestination.MAIN_TABS -> {
                    when (uiState.currentTab) {
                        ScreenTab.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                        ScreenTab.TASBEEH -> TasbeehScreen(uiState = uiState, viewModel = viewModel)
                        ScreenTab.ADHKAR -> AdhkarScreen(uiState = uiState, viewModel = viewModel)
                        ScreenTab.QURAN -> QuranScreen(uiState = uiState, viewModel = viewModel)
                        ScreenTab.PRAYER -> PrayerScreen(uiState = uiState, viewModel = viewModel)
                    }
                }
                NavigationDestination.ADHKAR_DETAIL -> {
                    AdhkarDetailScreen(uiState = uiState, viewModel = viewModel)
                }
                NavigationDestination.SURAH_READER -> {
                    SurahReaderScreen(uiState = uiState, viewModel = viewModel)
                }
                NavigationDestination.CALENDAR -> {
                    CalendarScreen(uiState = uiState, viewModel = viewModel)
                }
                NavigationDestination.SETTINGS -> {
                    SettingsScreen(uiState = uiState, viewModel = viewModel)
                }
            }

            // Floating Toast Notification
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                uiState.toastMessage?.let { message ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = message,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
