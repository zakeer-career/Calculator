package com.zakeercareer.calculator.data.db

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

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0 AND isFavorite = 1")
    suspend fun clearFavorites()

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0 AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\')")
    suspend fun clearBySearch(query: String)

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0 AND category = :category AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\')")
    suspend fun clearBySearchAndCategory(query: String, category: String)

    @Query("UPDATE calculation_history SET isTrash = 1 WHERE isTrash = 0 AND isFavorite = 1 AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\')")
    suspend fun clearFavoritesBySearch(query: String)

    @Query("DELETE FROM calculation_history")
    suspend fun permanentlyDeleteAll()

    @Query("UPDATE calculation_history SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE calculation_history SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String?)

    @Query("UPDATE calculation_history SET isTrash = :isTrash WHERE id = :id")
    suspend fun setTrashStatus(id: Long, isTrash: Boolean)

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND category = :category ORDER BY timestamp DESC")
    fun getByCategory(category: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\') ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND category = :category AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\') ORDER BY timestamp DESC")
    fun searchHistoryByCategory(query: String, category: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 0 AND isFavorite = 1 AND (expression LIKE '%' || :query || '%' ESCAPE '\\' OR result LIKE '%' || :query || '%' ESCAPE '\\' OR note LIKE '%' || :query || '%' ESCAPE '\\') ORDER BY timestamp DESC")
    fun searchFavorites(query: String): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculation_history WHERE isTrash = 1 ORDER BY timestamp DESC")
    fun getTrashHistory(): Flow<List<CalculationEntity>>

    @Query("DELETE FROM calculation_history WHERE isTrash = 1")
    suspend fun emptyTrash()

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)
}

