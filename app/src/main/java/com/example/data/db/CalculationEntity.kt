package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "calculation_history",
    indices = [
        Index(value = ["isTrash", "timestamp"]),
        Index(value = ["isTrash", "category", "timestamp"]),
        Index(value = ["isTrash", "isFavorite", "timestamp"])
    ]
)
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "STANDARD", "MATRIX", "UNIT", "CURRENCY"
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val note: String? = null,
    val details: String? = null, // Additional metadata e.g. "USD -> EUR" or "3x3 Matrix Det"
    val isTrash: Boolean = false
)
