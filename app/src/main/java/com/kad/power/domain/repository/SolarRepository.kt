package com.kad.power.domain.repository

import com.kad.power.data.local.entities.*
import kotlinx.coroutines.flow.Flow

interface SolarRepository {
    fun getProducts(): Flow<List<ProductEntity>>
    fun getBookmarkedProducts(): Flow<List<ProductEntity>>
    suspend fun refreshProducts(): Result<Unit>
    suspend fun toggleProductBookmark(productId: String, isBookmarked: Boolean)

    fun getSavedCalculations(): Flow<List<SolarCalculationEntity>>
    suspend fun saveCalculation(calculation: SolarCalculationEntity)
    suspend fun deleteSavedCalculation(calculation: SolarCalculationEntity)

    suspend fun requestConsultation(name: String, phone: String, city: String, systemType: String, notes: String): Result<Unit>
    suspend fun getPendingSyncCount(): Int
    suspend fun syncPendingInquiries(): Result<Int>
}
