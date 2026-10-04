package com.example.data.model

data class DhikrItem(
    val id: Int,
    val title: String,
    val subtitle: String = "",
    val text: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val source: String,
    val virtue: String,
    val isCompleted: Boolean = false,
    val isFavorite: Boolean = false
)

data class DhikrCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val itemsCount: Int,
    val iconName: String,
    val tag: String
)

data class WorshipGoal(
    val id: String,
    val title: String,
    val progressDesc: String,
    val isCompleted: Boolean,
    val inProgress: Boolean = false
)

data class HadithQuote(
    val quote: String,
    val source: String,
    val benefit: String = ""
)

data class Ayah(
    val number: Int,
    val numberArabic: String,
    val text: String,
    val tafsir: String
)

data class Surah(
    val number: Int,
    val numberArabic: String,
    val name: String,
    val simpleName: String,
    val versesCount: Int,
    val versesCountArabic: String,
    val pageNumber: Int,
    val pageNumberArabic: String,
    val isMakki: Boolean,
    val isBookmarked: Boolean = false,
    val ayahs: List<Ayah> = emptyList()
)

data class PrayerScheduleItem(
    val id: String,
    val name: String,
    val timeFormatted: String,
    val subtitle: String,
    val isElapsed: Boolean = false,
    val isNext: Boolean = false,
    val isNotificationOn: Boolean = true,
    val iconType: String
)

data class SunnahRakItem(
    val id: String,
    val prayerName: String,
    val rakahsCount: Int,
    val rakahsCountArabic: String,
    val isCompleted: Boolean
)

data class IslamicEvent(
    val id: String,
    val title: String,
    val hijriDate: String,
    val gregorianDate: String,
    val daysRemaining: Int
)

data class CalendarDay(
    val gregorianDay: Int,
    val hijriDay: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isFriday: Boolean,
    val isSelected: Boolean = false,
    val hasQuranReading: Boolean = false,
    val isDhikrCompleted: Boolean = false
)

enum class ScreenTab {
    HOME,
    TASBEEH,
    ADHKAR,
    QURAN,
    PRAYER
}

enum class NavigationDestination {
    MAIN_TABS,
    ADHKAR_DETAIL,
    SURAH_READER,
    CALENDAR,
    SETTINGS
}
