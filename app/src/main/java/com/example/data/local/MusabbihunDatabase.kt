package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasbeeh_sessions")
data class TasbeehSessionEntity(
    @PrimaryKey val id: Int = 1,
    val count: Int,
    val target: Int,
    val activePhrase: String,
    val dailyTotal: Int,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean
)

@Entity(tableName = "sunan_state")
data class SunanStateEntity(
    @PrimaryKey val id: String,
    val isCompleted: Boolean
)

@Entity(tableName = "app_preferences")
data class AppPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val quranDailyGoalPages: Int = 7,
    val tasbeehDailyGoal: Int = 500,
    val fontScale: Float = 24f,
    val appTextSize: String = "متوسط",
    val themeMode: String = "فاتح", // فاتح, داكن, تلقائي
    val calculationMethod: String = "تقويم أم القرى (مكة المكرمة)",
    val notifyMorningAdhkar: Boolean = true,
    val notifyEveningAdhkar: Boolean = true,
    val notifySleepAdhkar: Boolean = true,
    val notifyKahfFriday: Boolean = true,
    val audioAdhanEnabled: Boolean = true,
    val bookmarkedSurahNumber: Int = 18,
    val bookmarkedAyahNumber: Int = 1,
    val bookmarkedPageNumber: Int = 293
)

@Dao
interface MusabbihunDao {
    @Query("SELECT * FROM tasbeeh_sessions WHERE id = 1")
    fun getTasbeehSession(): Flow<TasbeehSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbeehSession(session: TasbeehSessionEntity)

    @Query("SELECT * FROM sunan_state")
    fun getAllSunanStates(): Flow<List<SunanStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSunanState(state: SunanStateEntity)

    @Query("SELECT * FROM app_preferences WHERE id = 1")
    fun getPreferences(): Flow<AppPreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(prefs: AppPreferencesEntity)
}

@Database(
    entities = [TasbeehSessionEntity::class, SunanStateEntity::class, AppPreferencesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusabbihunDatabase : RoomDatabase() {
    abstract fun dao(): MusabbihunDao

    companion object {
        @Volatile
        private var INSTANCE: MusabbihunDatabase? = null

        fun getInstance(context: Context): MusabbihunDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusabbihunDatabase::class.java,
                    "musabbihun_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
