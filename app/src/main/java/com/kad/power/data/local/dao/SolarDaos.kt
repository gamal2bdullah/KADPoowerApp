package com.kad.power.data.local.dao

import androidx.room.*
import com.kad.power.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM solar_products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM solar_products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM solar_products WHERE isBookmarked = 1")
    fun getBookmarkedProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("UPDATE solar_products SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)
}

@Dao
interface SolarCalculationDao {
    @Query("SELECT * FROM solar_calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<SolarCalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: SolarCalculationEntity)

    @Delete
    suspend fun deleteCalculation(calculation: SolarCalculationEntity)
}

@Dao
interface InquirySyncDao {
    @Query("SELECT * FROM inquiry_queue WHERE isSynchronized = 0")
    suspend fun getPendingInquiries(): List<InquirySyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueInquiry(inquiry: InquirySyncEntity)

    @Query("UPDATE inquiry_queue SET isSynchronized = 1 WHERE id = :id")
    suspend fun markAsSynchronized(id: String)

    @Query("UPDATE inquiry_queue SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetryCount(id: String)
}
