package com.example.data.repository

import com.example.data.local.AppPreferencesEntity
import com.example.data.local.MusabbihunDao
import com.example.data.local.SunanStateEntity
import com.example.data.local.TasbeehSessionEntity
import com.example.data.model.Ayah
import com.example.data.model.CalendarDay
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.HadithQuote
import com.example.data.model.IslamicEvent
import com.example.data.model.PrayerScheduleItem
import com.example.data.model.SunnahRakItem
import com.example.data.model.Surah
import com.example.data.model.WorshipGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusabbihunRepository(private val dao: MusabbihunDao) {

    val tasbeehSession: Flow<TasbeehSessionEntity?> = dao.getTasbeehSession()
    val preferences: Flow<AppPreferencesEntity?> = dao.getPreferences()
    val sunanStates: Flow<List<SunanStateEntity>> = dao.getAllSunanStates()

    suspend fun saveTasbeehSession(session: TasbeehSessionEntity) {
        dao.saveTasbeehSession(session)
    }

    suspend fun savePreferences(prefs: AppPreferencesEntity) {
        dao.savePreferences(prefs)
    }

    suspend fun toggleSunan(id: String, currentState: Boolean) {
        dao.saveSunanState(SunanStateEntity(id = id, isCompleted = !currentState))
    }

    // Default static authentic data
    fun getHadithOfTheDay(): HadithQuote {
        return HadithQuote(
            quote = "«أَحَبُّ الأَعْمَالِ إِلَى اللهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
            source = "صحيح البخاري ومسلم",
            benefit = "جعل الله ذكرك نجاة ونوراً في قلبك."
        )
    }

    fun getDailyWorshipGoals(): List<WorshipGoal> {
        return listOf(
            WorshipGoal("1", "أذكار الصباح", "١٤ من ١٤ مكتملة", isCompleted = true),
            WorshipGoal("2", "ورد القرآن اليومي", "٧ صفحات (الجزء ١٥)", isCompleted = true),
            WorshipGoal("3", "المسبحة والاستغفار", "٣٢٧ من ٥٠٠ تسبيحة", isCompleted = false, inProgress = true),
            WorshipGoal("4", "أذكار المساء", "بعد صلاة العصر", isCompleted = false),
            WorshipGoal("5", "أذكار النوم وقيام الليل", "معلق للمساء", isCompleted = false)
        )
    }

    fun getTodayPrayerSchedule(): List<PrayerScheduleItem> {
        return listOf(
            PrayerScheduleItem(
                id = "fajr",
                name = "الفجر",
                timeFormatted = "٠٤:٥٢ ص",
                subtitle = "الشروق ٠٦:١٠ ص",
                isElapsed = true,
                isNext = false,
                isNotificationOn = true,
                iconType = "check_circle"
            ),
            PrayerScheduleItem(
                id = "sunrise",
                name = "الشروق",
                timeFormatted = "٠٦:١٠ ص",
                subtitle = "وقت الإشراق",
                isElapsed = true,
                isNext = false,
                isNotificationOn = false,
                iconType = "wb_twilight"
            ),
            PrayerScheduleItem(
                id = "dhuhr",
                name = "الظهر",
                timeFormatted = "١٢:٠٨ م",
                subtitle = "الزوال",
                isElapsed = true,
                isNext = false,
                isNotificationOn = true,
                iconType = "check_circle"
            ),
            PrayerScheduleItem(
                id = "asr",
                name = "العصر",
                timeFormatted = "٠٣:٣٢ م",
                subtitle = "متبقي ٠١:٢٤ ساعة",
                isElapsed = false,
                isNext = true,
                isNotificationOn = true,
                iconType = "wb_sunny"
            ),
            PrayerScheduleItem(
                id = "maghrib",
                name = "المغرب",
                timeFormatted = "٠٦:٠٥ م",
                subtitle = "الغروب والإفطار",
                isElapsed = false,
                isNext = false,
                isNotificationOn = true,
                iconType = "wb_twilight"
            ),
            PrayerScheduleItem(
                id = "isha",
                name = "العشاء",
                timeFormatted = "٠٧:٣٥ م",
                subtitle = "صلاة التراويح",
                isElapsed = false,
                isNext = false,
                isNotificationOn = true,
                iconType = "dark_mode"
            ),
            PrayerScheduleItem(
                id = "qiyam",
                name = "قيام الليل والثلث الأخير",
                timeFormatted = "٠٢:١٥ ص",
                subtitle = "يبدأ الثلث الأخير",
                isElapsed = false,
                isNext = false,
                isNotificationOn = true,
                iconType = "bedtime"
            )
        )
    }

    fun getSunanRawatib(): List<SunnahRakItem> {
        return listOf(
            SunnahRakItem("fajr", "الفجر", 2, "٢ ركعة", isCompleted = true),
            SunnahRakItem("dhuhr_pre", "قبل الظهر", 4, "٤ ركعات", isCompleted = true),
            SunnahRakItem("dhuhr_post", "بعد الظهر", 2, "٢ ركعة", isCompleted = false),
            SunnahRakItem("maghrib_post", "المغرب", 2, "٢ ركعة", isCompleted = false),
            SunnahRakItem("isha_post", "العشاء", 2, "٢ ركعة", isCompleted = false)
        )
    }

    fun getMorningAdhkarList(): List<DhikrItem> {
        return listOf(
            DhikrItem(
                id = 1,
                title = "آية الكرسي",
                subtitle = "أعظم آية في كتاب الله",
                text = "«اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ»",
                targetCount = 1,
                source = "سورة البقرة: ٢٥٥",
                virtue = "من قرأها حين يصبح أجير من الجن حتى يمسي، ومن قرأها حين يمسي أجير منهم حتى يصبح."
            ),
            DhikrItem(
                id = 2,
                title = "المعوذتان وسورة الإخلاص",
                subtitle = "قل هو الله أحد، قل أعوذ برب الفلق، قل أعوذ برب الناس",
                text = "«قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ»\n\n«قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ»\n\n«قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ»",
                targetCount = 3,
                source = "رواه أبو داود والترمذي",
                virtue = "تكفيك من كل شيء."
            ),
            DhikrItem(
                id = 3,
                title = "أصبحنا وأصبح الملك لله",
                subtitle = "إقرار بالتوحيد والحمد",
                text = "«أَصْبَحْنَا وَأَصْبَحَ المُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ المُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ»",
                targetCount = 1,
                source = "صحيح مسلم",
                virtue = "سؤال خير اليوم والتعوذ من الشر والكسل وعذاب النار والقبر."
            ),
            DhikrItem(
                id = 4,
                title = "اللهم بك أصبحنا",
                subtitle = "التفويض والتوكل على الحي القيوم",
                text = "«اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ»",
                targetCount = 1,
                source = "رواه الترمذي وأبو داود",
                virtue = "استشعار معية الله في كل طرفة عين وإليه المعاد."
            ),
            DhikrItem(
                id = 5,
                title = "سيّد الاستغفار",
                subtitle = "أعظم أدعية الاستغفار",
                text = "«اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي؛ فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ»",
                targetCount = 1,
                source = "رواه البخاري",
                virtue = "مَن قالها موقناً بها حين يمسي فمات من ليلته دخل الجنة، وكذلك إذا أصبح."
            ),
            DhikrItem(
                id = 6,
                title = "اللهم عافني في بدني",
                subtitle = "سؤال العافية والاستعاذة من الكفر والفقر",
                text = "«اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ. اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْكُفْرِ، وَالْفَقْرِ، وَأَعُوذُ بِكَ مِنْ عَذَابِ الْقَبْرِ، لَا إِلَهَ إِلَّا أَنْتَ»",
                targetCount = 3,
                source = "رواه أحمد وأبو داود",
                virtue = "حفظ الجوارح وتمام العافية في الدين والبدن."
            ),
            DhikrItem(
                id = 7,
                title = "رضيت بالله رباً",
                subtitle = "الرضا بالرب والمنهاج والرسالة",
                text = "«رَضِيتُ بِاللَّهِ رَبّاً، وَبِالإِسْلاَمِ دِيناً، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيّاً»",
                targetCount = 3,
                source = "رواه الترمذي وأبو داود",
                virtue = "من قالها حين يصبح وحين يمسي كان حقاً على الله أن يرضيه يوم القيامة."
            ),
            DhikrItem(
                id = 8,
                title = "يا حي يا قيوم برحمتك أستغيث",
                subtitle = "دعاء الكرب وتفويض الأمر",
                text = "«يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلاَ تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ»",
                targetCount = 1,
                source = "رواه الحاكم وصححه الألباني",
                virtue = "صلاح الأمور كلها وعدم الاتكال على الحول والقوة الشخصية."
            ),
            DhikrItem(
                id = 9,
                title = "بسم الله الذي لا يضر مع اسمه شيء",
                subtitle = "حرز الصباح من كل ضر وسوء",
                text = "«بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ»",
                targetCount = 3,
                source = "رواه أبو داود والترمذي",
                virtue = "لم يضره شيء حتى يمسي، وإذا قالها مساءً لم يضره شيء حتى يصبح."
            ),
            DhikrItem(
                id = 10,
                title = "سبحان الله وبحمده عدد خلقه",
                subtitle = "جوامع التسبيح ومضاعفة الأجر",
                text = "«سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ»",
                targetCount = 3,
                source = "صحيح مسلم",
                virtue = "تزن في الأجر ساعات طويلة من الذكر المفرد."
            )
        )
    }

    fun getAdhkarCategories(): List<DhikrCategory> {
        return listOf(
            DhikrCategory("prayer", "أذكار بعد الصلاة المفروضة", "الأدعية المأثورة عقب كل صلاة", 8, "mosque", "prayer"),
            DhikrCategory("sleep", "أذكار النوم", "لراحة البال ونوم هانئ ومبارك", 15, "nights_stay", "sleep"),
            DhikrCategory("wake", "أذكار الاستيقاظ", "الحمد لله الذي أحيانا بعد ما أماتنا", 5, "alarm", "sleep"),
            DhikrCategory("travel", "أدعية السفر والخروج", "دعاء ركوب الدابة ودعاء الخروج من المنزل", 7, "flight_takeoff", "events"),
            DhikrCategory("general", "أذكار عامة واستغفار", "سيد الاستغفار، الحوقلة، والتسبيح المطلق", 18, "all_inclusive", "daily")
        )
    }

    fun getSurahsList(): List<Surah> {
        val kahfVerses = listOf(
            Ayah(
                1, "١",
                "ٱلْحَمْدُ لِلَّهِ ٱلَّذِى أَنزَلَ عَلَىٰ عَبْدِهِ ٱلْكِتَٰبَ وَلَمْ يَجْعَل لَّهُۥ عِوَجَاۜ",
                "الثناء الكامل لله سبحانه وتعالى بصفاته التي كلها أوصاف كمال، وبنعمه الظاهرة والباطنة، الدينية والدنيوية، الذي أنزل على عبده ورسوله محمد ﷺ القرآن العظيم، ولم يجعل فيه شيئاً من الميل عن الحق ولا تناقضاً."
            ),
            Ayah(
                2, "٢",
                "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ ٱلْمُؤْمِنِينَ ٱلَّذِينَ يَعْمَلُونَ ٱلصَّٰلِحَٰتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا",
                "جعله كتاباً مستقيماً لا خلل فيه، لينذر الكافرين عذاباً شديداً من عنده، ويبشر المؤمنين الذين يعملون الصالحات بأن لهم ثواباً حسناً وهو الجنة."
            ),
            Ayah(
                3, "٣",
                "مَّٰكِثِينَ فِيهِ أَبَدًا",
                "خالدين في هذا النعيم المقيم أبداً، لا يحولون عنه ولا يزول."
            ),
            Ayah(
                4, "٤",
                "وَيُنذِرَ ٱلَّذِينَ قَالُوا۟ ٱتَّخَذَ ٱللَّهُ وَلَدًا",
                "ولينذر كذلك المشركين واليهود والنصارى الذين قالوا كفراً وافتراءً: اتخذ الله ولداً."
            ),
            Ayah(
                5, "٥",
                "مَّا لَهُم بِهِۦ مِنْ عِلْمٍ وَلَا لِءَابَآئِهِمْ ۚ كَبُرَتْ كَلِمَةً تَخْرُجُ مِنْ أَفْوَٰهِهِمْ ۚ إِن يَقُولُونَ إِلَّا كَذِبًا",
                "ليس لهؤلاء القائلين بذلك القول برهان ولا علم، ولا لآبائهم من قبلهم، عظمت هذه الكلمة الشنيعة الصادرة عن ألسنتهم كذباً وزوراً."
            ),
            Ayah(
                6, "٦",
                "فَلَعَلَّكَ بَٰخِعٌ نَّفْسَكَ عَلَىٰٓ ءَاثَٰرِهِمْ إِن لَّمْ يُؤْمِنُوا۟ بِهَٰذَا ٱلْحَدِيثِ أَسَفًا",
                "فلعلك -أيها الرسول- مهلك نفسك حزناً وأسفاً على إعراض قومك إن لم يؤمنوا بهذا القرآن ويصدقوا به."
            ),
            Ayah(
                7, "٧",
                "إِنَّا جَعَلْنَا مَا عَلَى ٱلْأَرْضِ زِينَةً لَّهَا لِنَبْلُوَهُمْ أَيُّهُمْ أَحْسَنُ عَمَلًا",
                "إنا جعلنا ما على وجه الأرض من المخلوقات والمباهج زينة لها؛ لنختبر الناس أيهم أحسن عملاً وأطوع لله."
            ),
            Ayah(
                8, "٨",
                "وَإِنَّا لَجَٰعِلُونَ مَا عَلَيْهَا صَعِيدًا جُرُزًا",
                "وإنا لمصيرون جميع ما عليها عند انتهاء الدنيا تراباً يابساً لا نبت فيه ولا أثر لعمارة."
            )
        )

        return listOf(
            Surah(1, "١", "الفاتحة", "Al-Fatihah", 7, "٧ آيات", 1, "صفحة ١", true),
            Surah(2, "٢", "البقرة", "Al-Baqarah", 286, "٢٨٦ آية", 2, "صفحة ٢", false),
            Surah(3, "٣", "آل عمران", "Aal-E-Imran", 200, "٢٠٠ آية", 50, "صفحة ٥٠", false),
            Surah(4, "٤", "النساء", "An-Nisa", 176, "١٧٦ آية", 77, "صفحة ٧٧", false),
            Surah(5, "٥", "المائدة", "Al-Ma'idah", 120, "١٢٠ آية", 106, "صفحة ١٠٦", false),
            Surah(6, "٦", "الأنعام", "Al-An'am", 165, "١٦٥ آية", 128, "صفحة ١٢٨", true),
            Surah(7, "٧", "الأعراف", "Al-A'raf", 206, "٢٠٦ آيات", 151, "صفحة ١٥١", true),
            Surah(8, "٨", "الأنفال", "Al-Anfal", 75, "٧٥ آية", 177, "صفحة ١٧٧", false),
            Surah(9, "٩", "التوبة", "At-Tawbah", 129, "١٢٩ آية", 187, "صفحة ١٨٧", false),
            Surah(10, "١٠", "يونس", "Yunus", 109, "١٠٩ آيات", 208, "صفحة ٢٠٨", true),
            Surah(11, "١١", "هود", "Hud", 123, "١٢٣ آية", 221, "صفحة ٢٢١", true),
            Surah(12, "١٢", "يوسف", "Yusuf", 111, "١١١ آية", 235, "صفحة ٢٣٥", true),
            Surah(13, "١٣", "الرعد", "Ar-Ra'd", 43, "٤٣ آية", 249, "صفحة ٢٤٩", false),
            Surah(14, "١٤", "إبراهيم", "Ibrahim", 52, "٥٢ آية", 255, "صفحة ٢٥٥", true),
            Surah(15, "١٥", "الحجر", "Al-Hijr", 99, "٩٩ آية", 262, "صفحة ٢٦٢", true),
            Surah(16, "١٦", "النحل", "An-Nahl", 128, "١٢٨ آية", 267, "صفحة ٢٦٧", true),
            Surah(17, "١٧", "الإسراء", "Al-Isra", 111, "١١١ آية", 282, "صفحة ٢٨٢", true),
            Surah(18, "١٨", "الكهف", "Al-Kahf", 110, "١١٠ آيات", 293, "صفحة ٢٩٣", true, isBookmarked = true, ayahs = kahfVerses),
            Surah(19, "١٩", "مريم", "Maryam", 98, "٩٨ آية", 305, "صفحة ٣٠٥", true),
            Surah(20, "٢٠", "طه", "Ta-Ha", 135, "١٣٥ آية", 312, "صفحة ٣١٢", true),
            Surah(21, "٢١", "الأنبياء", "Al-Anbiya", 112, "١١٢ آية", 322, "صفحة ٣٢٢", true),
            Surah(22, "٢٢", "الحج", "Al-Hajj", 78, "٧٨ آية", 332, "صفحة ٣٣٢", false),
            Surah(23, "٢٣", "المؤمنون", "Al-Mu'minun", 118, "١١٨ آية", 342, "صفحة ٣٤٢", true),
            Surah(24, "٢٤", "النور", "An-Nur", 64, "٦٤ آية", 350, "صفحة ٣٥٠", false),
            Surah(25, "٢٥", "الفرقان", "Al-Furqan", 77, "٧٧ آية", 359, "صفحة ٣٥٩", true),
            Surah(26, "٢٦", "الشعراء", "Ash-Shu'ara", 227, "٢٢٧ آية", 367, "صفحة ٣٦٧", true),
            Surah(27, "٢٧", "النمل", "An-Naml", 93, "٩٣ آية", 377, "صفحة ٣٧٧", true),
            Surah(28, "٢٨", "القصص", "Al-Qasas", 88, "٨٨ آية", 385, "صفحة ٣٨٥", true),
            Surah(29, "٢٩", "العنكبوت", "Al-Ankabut", 69, "٦٩ آية", 396, "صفحة ٣٩٦", true),
            Surah(30, "٣٠", "الروم", "Ar-Rum", 60, "٦٠ آية", 404, "صفحة ٤٠٤", true),
            Surah(31, "٣١", "لقمان", "Luqman", 34, "٣٤ آية", 411, "صفحة ٤١١", true),
            Surah(32, "٣٢", "السجدة", "As-Sajdah", 30, "٣٠ آية", 415, "صفحة ٤١٥", true),
            Surah(33, "٣٣", "الأحزاب", "Al-Ahzab", 73, "٧٣ آية", 418, "صفحة ٤١٨", false),
            Surah(34, "٣٤", "سبأ", "Saba", 54, "٥٤ آية", 428, "صفحة ٤٢٨", true),
            Surah(35, "٣٥", "فاطر", "Fatir", 45, "٤٥ آية", 434, "صفحة ٤٣٤", true),
            Surah(36, "٣٦", "يس", "Ya-Sin", 83, "٨٣ آية", 440, "صفحة ٤٤٠", true),
            Surah(55, "٥٥", "الرحمن", "Ar-Rahman", 78, "٧٨ آية", 531, "صفحة ٥٣١", false),
            Surah(56, "٥٦", "الواقعة", "Al-Waqi'ah", 96, "٩٦ آية", 534, "صفحة ٥٣٤", true),
            Surah(67, "٦٧", "الملك", "Al-Mulk", 30, "٣٠ آية", 562, "صفحة ٥٦٢", true),
            Surah(112, "١١٢", "الإخلاص", "Al-Ikhlas", 4, "٤ آيات", 604, "صفحة ٦٠٤", true),
            Surah(113, "١١٣", "الفلق", "Al-Falaq", 5, "٥ آيات", 604, "صفحة ٦٠٤", true),
            Surah(114, "١١٤", "الناس", "An-Nas", 6, "٦ آيات", 604, "صفحة ٦٠٤", true)
        )
    }

    fun getOctober2026Days(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        // September tail (Sun 27 - Wed 30)
        days.add(CalendarDay(27, 5, isCurrentMonth = false, isToday = false, isFriday = false))
        days.add(CalendarDay(28, 6, isCurrentMonth = false, isToday = false, isFriday = false))
        days.add(CalendarDay(29, 7, isCurrentMonth = false, isToday = false, isFriday = false))
        days.add(CalendarDay(30, 8, isCurrentMonth = false, isToday = false, isFriday = false))

        // October 1 to 31
        for (g in 1..31) {
            val hijri = g + 8
            val dayOfWeek = (g + 3) % 7 // Oct 1 is Thursday
            val isFriday = (dayOfWeek == 5)
            val isToday = (g == 4) // Sunday Oct 4, 2026
            val hasKahf = isFriday
            val isCompleted = (g in listOf(1, 3, 4, 8, 10))
            days.add(
                CalendarDay(
                    gregorianDay = g,
                    hijriDay = if (hijri > 30) hijri - 30 else hijri,
                    isCurrentMonth = true,
                    isToday = isToday,
                    isFriday = isFriday,
                    isSelected = isToday,
                    hasQuranReading = hasKahf,
                    isDhikrCompleted = isCompleted
                )
            )
        }
        return days
    }

    fun getUpcomingEvents(): List<IslamicEvent> {
        return listOf(
            IslamicEvent(
                id = "ramadan",
                title = "شهر رمضان المبارك ١٤٤٨ هـ",
                hijriDate = "١ رمضان",
                gregorianDate = "المتوقع فلكياً: الخميس ١٨ فبراير ٢٠٢٧",
                daysRemaining = 136
            ),
            IslamicEvent(
                id = "isra",
                title = "ذكرى الإسراء والمعراج",
                hijriDate = "٢٧ رجب ١٤٤٨ هـ",
                gregorianDate = "الأربعاء ٦ يناير ٢٠٢٧",
                daysRemaining = 93
            )
        )
    }
}
