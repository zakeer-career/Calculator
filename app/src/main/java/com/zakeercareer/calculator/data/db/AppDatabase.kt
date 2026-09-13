package com.zakeercareer.calculator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [CalculationEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculationDao(): CalculationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE calculation_history ADD COLUMN details TEXT DEFAULT NULL")
                } catch (ignored: Exception) {}
                try {
                    db.execSQL("ALTER TABLE calculation_history ADD COLUMN isTrash INTEGER NOT NULL DEFAULT 0")
                } catch (ignored: Exception) {}
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_calculation_history_isTrash_timestamp ON calculation_history (isTrash, timestamp)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_calculation_history_isTrash_category_timestamp ON calculation_history (isTrash, category, timestamp)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS index_calculation_history_isTrash_isFavorite_timestamp ON calculation_history (isTrash, isFavorite, timestamp)")
                } catch (ignored: Exception) {}
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "advanced_calculator_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

