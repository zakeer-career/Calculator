package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CalculationEntity): Long

    @Update
    suspend fun update(entry: CalculationEntity)

    @Delete
    suspend fun delete(entry: CalculationEntity)

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0")
    suspend fun clearAll()

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0 AND category = :category")
    suspend fun clearByCategory(category: String)

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND category = :category ORDER BY timestamp DESC")
    fun getByCategory(category: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND (expression LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 1 ORDER BY timestamp DESC")
    fun getTrashHistory(): Flow<List<CalculationEntity>>

    @Query("DELETE FROM calculation_history WHERE isTrash = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)
}
