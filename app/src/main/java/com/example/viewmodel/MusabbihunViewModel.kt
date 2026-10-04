package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppPreferencesEntity
import com.example.data.local.MusabbihunDatabase
import com.example.data.local.TasbeehSessionEntity
import com.example.data.model.Ayah
import com.example.data.model.CalendarDay
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.HadithQuote
import com.example.data.model.IslamicEvent
import com.example.data.model.NavigationDestination
import com.example.data.model.PrayerScheduleItem
import com.example.data.model.ScreenTab
import com.example.data.model.SunnahRakItem
import com.example.data.model.Surah
import com.example.data.model.WorshipGoal
import com.example.data.repository.MusabbihunRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MusabbihunUiState(
    val currentTab: ScreenTab = ScreenTab.HOME,
    val currentDestination: NavigationDestination = NavigationDestination.MAIN_TABS,
    // Home preview state
    val homePreviewState: String = "current", // current, completed, nolocation, night
    val worshipProgressPercent: Int = 65,
    val worshipCompletedCount: Int = 6,
    val worshipTotalCount: Int = 9,
    val hadith: HadithQuote = HadithQuote("", ""),
    val worshipGoals: List<WorshipGoal> = emptyList(),
    // Prayer countdown (seconds)
    val prayerCountdownSeconds: Int = 5070, // 01:24:30
    val prayerSchedule: List<PrayerScheduleItem> = emptyList(),
    val heroSoundEnabled: Boolean = true,
    // Sunan Rawatib (12 Rak'ahs)
    val sunanList: List<SunnahRakItem> = emptyList(),
    val completedRakCount: Int = 6,
    // Tasbeeh
    val tasbeehCount: Int = 33,
    val tasbeehTarget: Int = 100, // 33, 99, 100, or Int.MAX_VALUE
    val tasbeehActivePhrase: String = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
    val tasbeehShortTitle: String = "سُبْحَانَ اللَّهِ",
    val tasbeehDailyTotal: Int = 165,
    val tasbeehSoundEnabled: Boolean = true,
    val tasbeehVibrationEnabled: Boolean = true,
    val milestoneMessage: String? = "أتممت الدورة الأولى بنجاح (٣٣ تسبيحة)",
    // Adhkar
    val adhkarCategoryFilter: String = "all", // all, daily, prayer, sleep, events
    val adhkarSearchQuery: String = "",
    val adhkarCategories: List<DhikrCategory> = emptyList(),
    val morningAdhkarList: List<DhikrItem> = emptyList(),
    // Adhkar Detail / Focused Counter
    val selectedDhikrIndex: Int = 4, // 0-indexed, 5th is Sayyid al-Istighfar
    val activeDhikrCurrentCount: Int = 0,
    val isDhikrIndexMode: Boolean = false, // false = Counter view, true = Index list
    val isFavoriteDhikr: Boolean = false,
    val dhikrTextSizeMultiplier: Float = 1.0f,
    // Quran
    val quranSearchQuery: String = "",
    val surahs: List<Surah> = emptyList(),
    val activeSurah: Surah? = null,
    val activeAyah: Ayah? = null,
    val isTafsirDrawerOpen: Boolean = false,
    val quranFontSize: Float = 24f,
    val quranBookmarked: Boolean = true,
    // Calendar
    val calendarDays: List<CalendarDay> = emptyList(),
    val upcomingEvents: List<IslamicEvent> = emptyList(),
    // Settings
    val quranGoalPages: Int = 7,
    val tasbeehGoal: Int = 500,
    val appThemeMode: String = "فاتح",
    val appTextSize: String = "متوسط",
    val calculationMethod: String = "تقويم أم القرى (مكة المكرمة)",
    val notifyMorning: Boolean = true,
    val notifyEvening: Boolean = true,
    val notifySleep: Boolean = true,
    val notifyFridayKahf: Boolean = true,
    val fullAdhanAudio: Boolean = true,
    val toastMessage: String? = null
)

class MusabbihunViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MusabbihunRepository
    private val vibrator: Vibrator?

    private val _uiState = MutableStateFlow(MusabbihunUiState())
    val uiState: StateFlow<MusabbihunUiState> = _uiState.asStateFlow()

    init {
        val db = MusabbihunDatabase.getInstance(application)
        repository = MusabbihunRepository(db.dao())

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        loadInitialData()
        startCountdownTimer()
    }

    private fun loadInitialData() {
        val initialSurahs = repository.getSurahsList()
        val kahfSurah = initialSurahs.firstOrNull { it.number == 18 }
        val firstAyah = kahfSurah?.ayahs?.firstOrNull()

        _uiState.update { current ->
            current.copy(
                hadith = repository.getHadithOfTheDay(),
                worshipGoals = repository.getDailyWorshipGoals(),
                prayerSchedule = repository.getTodayPrayerSchedule(),
                sunanList = repository.getSunanRawatib(),
                morningAdhkarList = repository.getMorningAdhkarList(),
                adhkarCategories = repository.getAdhkarCategories(),
                surahs = initialSurahs,
                activeSurah = kahfSurah,
                activeAyah = firstAyah,
                calendarDays = repository.getOctober2026Days(),
                upcomingEvents = repository.getUpcomingEvents()
            )
        }

        viewModelScope.launch {
            repository.tasbeehSession.collectLatest { session ->
                if (session != null) {
                    _uiState.update {
                        it.copy(
                            tasbeehCount = session.count,
                            tasbeehTarget = session.target,
                            tasbeehActivePhrase = session.activePhrase,
                            tasbeehDailyTotal = session.dailyTotal,
                            tasbeehSoundEnabled = session.soundEnabled,
                            tasbeehVibrationEnabled = session.vibrationEnabled
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.preferences.collectLatest { prefs ->
                if (prefs != null) {
                    _uiState.update {
                        it.copy(
                            quranGoalPages = prefs.quranDailyGoalPages,
                            tasbeehGoal = prefs.tasbeehDailyGoal,
                            quranFontSize = prefs.fontScale,
                            appTextSize = prefs.appTextSize,
                            appThemeMode = prefs.themeMode,
                            calculationMethod = prefs.calculationMethod,
                            notifyMorning = prefs.notifyMorningAdhkar,
                            notifyEvening = prefs.notifyEveningAdhkar,
                            notifySleep = prefs.notifySleepAdhkar,
                            notifyFridayKahf = prefs.notifyKahfFriday,
                            fullAdhanAudio = prefs.audioAdhanEnabled
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.sunanStates.collectLatest { states ->
                if (states.isNotEmpty()) {
                    val map = states.associate { it.id to it.isCompleted }
                    _uiState.update { current ->
                        val updatedList = current.sunanList.map { item ->
                            val isChecked = map[item.id] ?: item.isCompleted
                            item.copy(isCompleted = isChecked)
                        }
                        val count = updatedList.filter { it.isCompleted }.sumOf { it.rakahsCount }
                        current.copy(sunanList = updatedList, completedRakCount = count)
                    }
                }
            }
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { current ->
                    if (current.prayerCountdownSeconds > 0) {
                        current.copy(prayerCountdownSeconds = current.prayerCountdownSeconds - 1)
                    } else {
                        current
                    }
                }
            }
        }
    }

    // Tab & Navigation Switching
    fun selectTab(tab: ScreenTab) {
        _uiState.update {
            it.copy(currentTab = tab, currentDestination = NavigationDestination.MAIN_TABS)
        }
    }

    fun navigateTo(destination: NavigationDestination) {
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun navigateBack() {
        _uiState.update { it.copy(currentDestination = NavigationDestination.MAIN_TABS) }
    }

    // Home Screen preview switcher
    fun setHomePreviewState(state: String) {
        _uiState.update { current ->
            when (state) {
                "completed" -> current.copy(
                    homePreviewState = state,
                    worshipProgressPercent = 100,
                    worshipCompletedCount = 9
                )
                "nolocation" -> current.copy(
                    homePreviewState = state,
                    worshipProgressPercent = 65,
                    worshipCompletedCount = 6
                )
                "night" -> current.copy(
                    homePreviewState = state,
                    worshipProgressPercent = 86,
                    worshipCompletedCount = 8
                )
                else -> current.copy(
                    homePreviewState = "current",
                    worshipProgressPercent = 65,
                    worshipCompletedCount = 6
                )
            }
        }
    }

    // Tasbeeh Actions
    fun incrementTasbeeh() {
        val current = _uiState.value
        val newCount = current.tasbeehCount + 1
        val newTotal = current.tasbeehDailyTotal + 1

        triggerHaptic()

        var milestone = current.milestoneMessage
        if (current.tasbeehTarget != Int.MAX_VALUE && newCount == current.tasbeehTarget) {
            milestone = "أتممت الهدف بنجاح (${toArabicDigits(current.tasbeehTarget)} تسبيحة)"
            triggerMilestoneHaptic()
        }

        _uiState.update {
            it.copy(
                tasbeehCount = newCount,
                tasbeehDailyTotal = newTotal,
                milestoneMessage = milestone
            )
        }

        persistTasbeeh(newCount, current.tasbeehTarget, current.tasbeehActivePhrase, newTotal)
    }

    fun decrementTasbeeh() {
        val current = _uiState.value
        if (current.tasbeehCount > 0) {
            val newCount = current.tasbeehCount - 1
            val newTotal = maxOf(0, current.tasbeehDailyTotal - 1)
            triggerHaptic()
            _uiState.update { it.copy(tasbeehCount = newCount, tasbeehDailyTotal = newTotal) }
            persistTasbeeh(newCount, current.tasbeehTarget, current.tasbeehActivePhrase, newTotal)
        }
    }

    fun resetTasbeeh() {
        val current = _uiState.value
        triggerHaptic()
        _uiState.update { it.copy(tasbeehCount = 0) }
        persistTasbeeh(0, current.tasbeehTarget, current.tasbeehActivePhrase, current.tasbeehDailyTotal)
    }

    fun saveTasbeehSession() {
        triggerHaptic()
        showToast("تم حفظ الجلسة بنجاح ✓")
    }

    fun setTasbeehTarget(target: Int) {
        _uiState.update { it.copy(tasbeehTarget = target) }
        val current = _uiState.value
        persistTasbeeh(current.tasbeehCount, target, current.tasbeehActivePhrase, current.tasbeehDailyTotal)
    }

    fun setTasbeehPhrase(phrase: String, shortTitle: String) {
        _uiState.update {
            it.copy(
                tasbeehActivePhrase = phrase,
                tasbeehShortTitle = shortTitle,
                tasbeehCount = 0
            )
        }
        val current = _uiState.value
        persistTasbeeh(0, current.tasbeehTarget, phrase, current.tasbeehDailyTotal)
    }

    fun toggleTasbeehSound() {
        _uiState.update { it.copy(tasbeehSoundEnabled = !it.tasbeehSoundEnabled) }
    }

    fun toggleTasbeehVibration() {
        _uiState.update { it.copy(tasbeehVibrationEnabled = !it.tasbeehVibrationEnabled) }
    }

    private fun persistTasbeeh(count: Int, target: Int, phrase: String, total: Int) {
        val current = _uiState.value
        viewModelScope.launch {
            repository.saveTasbeehSession(
                TasbeehSessionEntity(
                    id = 1,
                    count = count,
                    target = target,
                    activePhrase = phrase,
                    dailyTotal = total,
                    soundEnabled = current.tasbeehSoundEnabled,
                    vibrationEnabled = current.tasbeehVibrationEnabled
                )
            )
        }
    }

    private fun triggerHaptic() {
        if (_uiState.value.tasbeehVibrationEnabled && vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        }
    }

    private fun triggerMilestoneHaptic() {
        if (_uiState.value.tasbeehVibrationEnabled && vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 40), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(80)
            }
        }
    }

    // Adhkar Filter & Reading
    fun setAdhkarCategoryFilter(category: String) {
        _uiState.update { it.copy(adhkarCategoryFilter = category) }
    }

    fun setAdhkarSearchQuery(query: String) {
        _uiState.update { it.copy(adhkarSearchQuery = query) }
    }

    fun openMorningAdhkarDetail(index: Int = 4) { // Default to Sayyid al-Istighfar (index 4)
        _uiState.update {
            it.copy(
                currentDestination = NavigationDestination.ADHKAR_DETAIL,
                selectedDhikrIndex = index,
                activeDhikrCurrentCount = 0,
                isDhikrIndexMode = false
            )
        }
    }

    fun incrementActiveDhikrTap() {
        val current = _uiState.value
        val list = current.morningAdhkarList
        if (current.selectedDhikrIndex in list.indices) {
            val target = list[current.selectedDhikrIndex].targetCount
            if (current.activeDhikrCurrentCount < target) {
                triggerHaptic()
                _uiState.update { it.copy(activeDhikrCurrentCount = it.activeDhikrCurrentCount + 1) }
            }
        }
    }

    fun resetActiveDhikrCount() {
        _uiState.update { it.copy(activeDhikrCurrentCount = 0) }
    }

    fun nextDhikr() {
        val current = _uiState.value
        if (current.selectedDhikrIndex < current.morningAdhkarList.size - 1) {
            _uiState.update {
                it.copy(
                    selectedDhikrIndex = it.selectedDhikrIndex + 1,
                    activeDhikrCurrentCount = 0
                )
            }
        } else {
            showToast("اكتملت جميع أذكار الورد المبارك ✓")
        }
    }

    fun prevDhikr() {
        val current = _uiState.value
        if (current.selectedDhikrIndex > 0) {
            _uiState.update {
                it.copy(
                    selectedDhikrIndex = it.selectedDhikrIndex - 1,
                    activeDhikrCurrentCount = 0
                )
            }
        }
    }

    fun setDhikrIndexMode(isIndex: Boolean) {
        _uiState.update { it.copy(isDhikrIndexMode = isIndex) }
    }

    fun toggleFavoriteDhikr() {
        _uiState.update { it.copy(isFavoriteDhikr = !it.isFavoriteDhikr) }
    }

    fun cycleDhikrFontSize() {
        _uiState.update {
            val nextMult = when (it.dhikrTextSizeMultiplier) {
                1.0f -> 1.15f
                1.15f -> 1.3f
                else -> 1.0f
            }
            it.copy(dhikrTextSizeMultiplier = nextMult)
        }
    }

    // Quran Actions
    fun setQuranSearchQuery(query: String) {
        _uiState.update { it.copy(quranSearchQuery = query) }
    }

    fun openSurahReader(surah: Surah) {
        val ayah = surah.ayahs.firstOrNull()
        _uiState.update {
            it.copy(
                activeSurah = surah,
                activeAyah = ayah,
                isTafsirDrawerOpen = false,
                currentDestination = NavigationDestination.SURAH_READER
            )
        }
    }

    fun selectAyah(ayah: Ayah) {
        _uiState.update { it.copy(activeAyah = ayah) }
    }

    fun toggleTafsirDrawer() {
        _uiState.update { it.copy(isTafsirDrawerOpen = !it.isTafsirDrawerOpen) }
    }

    fun increaseQuranFont() {
        _uiState.update { it.copy(quranFontSize = minOf(36f, it.quranFontSize + 2f)) }
        showToast("تم تكبير خط المصحف")
    }

    fun decreaseQuranFont() {
        _uiState.update { it.copy(quranFontSize = maxOf(18f, it.quranFontSize - 2f)) }
        showToast("تم تصغير خط المصحف")
    }

    fun toggleQuranBookmark() {
        _uiState.update { it.copy(quranBookmarked = !it.quranBookmarked) }
        showToast("تم حفظ موضع القراءة: صفحة ٢٩٣")
    }

    // Prayer Actions
    fun toggleHeroSound() {
        _uiState.update { it.copy(heroSoundEnabled = !it.heroSoundEnabled) }
    }

    fun toggleSunanRak(id: String) {
        val current = _uiState.value
        val item = current.sunanList.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repository.toggleSunan(id, item.isCompleted)
        }
        triggerHaptic()
    }

    // Calendar Actions
    fun selectCalendarDay(day: CalendarDay) {
        _uiState.update { current ->
            val updated = current.calendarDays.map {
                it.copy(isSelected = (it.gregorianDay == day.gregorianDay && it.isCurrentMonth == day.isCurrentMonth))
            }
            current.copy(calendarDays = updated)
        }
    }

    // Settings
    fun updateQuranGoalPages(pages: Int) {
        _uiState.update { it.copy(quranGoalPages = pages) }
        savePrefs()
    }

    fun updateTasbeehGoal(target: Int) {
        _uiState.update { it.copy(tasbeehGoal = target) }
        savePrefs()
    }

    fun updateQuranFontSize(size: Float) {
        _uiState.update { it.copy(quranFontSize = size) }
        savePrefs()
    }

    fun updateThemeMode(mode: String) {
        _uiState.update { it.copy(appThemeMode = mode) }
        savePrefs()
    }

    fun updateAppTextSize(size: String) {
        _uiState.update { it.copy(appTextSize = size) }
        savePrefs()
    }

    fun updateCalculationMethod(method: String) {
        _uiState.update { it.copy(calculationMethod = method) }
        savePrefs()
    }

    fun toggleNotification(key: String) {
        _uiState.update {
            when (key) {
                "morning" -> it.copy(notifyMorning = !it.notifyMorning)
                "evening" -> it.copy(notifyEvening = !it.notifyEvening)
                "sleep" -> it.copy(notifySleep = !it.notifySleep)
                "friday" -> it.copy(notifyFridayKahf = !it.notifyFridayKahf)
                "adhan" -> it.copy(fullAdhanAudio = !it.fullAdhanAudio)
                else -> it
            }
        }
        savePrefs()
    }

    fun resetDailyProgress() {
        _uiState.update {
            it.copy(
                worshipProgressPercent = 0,
                worshipCompletedCount = 0,
                tasbeehCount = 0,
                completedRakCount = 0,
                sunanList = it.sunanList.map { s -> s.copy(isCompleted = false) }
            )
        }
        showToast("تمت إعادة ضبط سجلات ورد اليوم")
    }

    private fun savePrefs() {
        val s = _uiState.value
        viewModelScope.launch {
            repository.savePreferences(
                AppPreferencesEntity(
                    id = 1,
                    quranDailyGoalPages = s.quranGoalPages,
                    tasbeehDailyGoal = s.tasbeehGoal,
                    fontScale = s.quranFontSize,
                    appTextSize = s.appTextSize,
                    themeMode = s.appThemeMode,
                    calculationMethod = s.calculationMethod,
                    notifyMorningAdhkar = s.notifyMorning,
                    notifyEveningAdhkar = s.notifyEvening,
                    notifySleepAdhkar = s.notifySleep,
                    notifyKahfFriday = s.notifyFridayKahf,
                    audioAdhanEnabled = s.fullAdhanAudio
                )
            )
        }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
        viewModelScope.launch {
            delay(2200)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }

    companion object {
        fun toArabicDigits(num: Int): String {
            if (num == Int.MAX_VALUE) return "∞"
            val arabicNumerals = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val str = num.toString()
            val sb = java.lang.StringBuilder()
            for (c in str) {
                if (c in '0'..'9') {
                    sb.append(arabicNumerals[c - '0'])
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        }

        fun formatSeconds(seconds: Int): String {
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            return String.format(java.util.Locale.US, "%02d:%02d:%02d", h, m, s)
        }

        fun formatSecondsArabic(seconds: Int): String {
            val h = toArabicDigits(seconds / 3600).padStart(2, '٠')
            val m = toArabicDigits((seconds % 3600) / 60).padStart(2, '٠')
            val s = toArabicDigits(seconds % 60).padStart(2, '٠')
            return "$h:$m:$s"
        }
    }
}
