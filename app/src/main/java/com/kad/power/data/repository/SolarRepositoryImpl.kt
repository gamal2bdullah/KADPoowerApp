package com.kad.power.data.repository

import com.kad.power.data.local.dao.*
import com.kad.power.data.local.entities.*
import com.kad.power.data.remote.KADApiService
import com.kad.power.data.remote.ProductNetworkModel
import com.kad.power.domain.repository.SolarRepository
import kotlinx.coroutines.flow.Flow
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolarRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val calculationDao: SolarCalculationDao,
    private val inquirySyncDao: InquirySyncDao,
    private val apiService: KADApiService
) : SolarRepository {

    override fun getProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    override fun getBookmarkedProducts(): Flow<List<ProductEntity>> = productDao.getBookmarkedProducts()

    override suspend fun refreshProducts(): Result<Unit> {
        return try {
            val response = apiService.fetchLatestProducts()
            if (response.isSuccessful && response.body() != null) {
                val networkProducts = response.body()!!
                val localEntities = networkProducts.map { net ->
                    ProductEntity(
                        id = net.id,
                        name = net.name,
                        category = net.category,
                        brand = net.brand,
                        specsJson = net.specsJson,
                        description = net.description,
                        descriptionAr = net.descriptionAr,
                        isBookmarked = false,
                        imageUrl = net.imageUrl
                    )
                }
                productDao.insertProducts(localEntities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server error: " + response.message()))
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun toggleProductBookmark(productId: String, isBookmarked: Boolean) {
        productDao.updateBookmark(productId, isBookmarked)
    }

    override fun getSavedCalculations(): Flow<List<SolarCalculationEntity>> = calculationDao.getAllCalculations()

    override suspend fun saveCalculation(calculation: SolarCalculationEntity) {
        calculationDao.insertCalculation(calculation)
    }

    override suspend fun deleteSavedCalculation(calculation: SolarCalculationEntity) {
        calculationDao.deleteCalculation(calculation)
    }

    override suspend fun requestConsultation(
        name: String,
        phone: String,
        city: String,
        systemType: String,
        notes: String
    ): Result<Unit> {
        val inquiry = InquirySyncEntity(
            name = name,
            phone = phone,
            city = city,
            systemType = systemType,
            notes = notes
        )
        return try {
            val response = apiService.submitInquiry(inquiry)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                inquirySyncDao.enqueueInquiry(inquiry)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            inquirySyncDao.enqueueInquiry(inquiry)
            Result.success(Unit)
        }
    }

    override suspend fun getPendingSyncCount(): Int {
        return inquirySyncDao.getPendingInquiries().size
    }

    @Synchronized
    override suspend fun syncPendingInquiries(): Result<Int> {
        val pending = inquirySyncDao.getPendingInquiries()
        if (pending.isEmpty()) return Result.success(0)

        var syncedCount = 0
        for (inquiry in pending) {
            try {
                val response = apiService.submitInquiry(inquiry)
                if (response.isSuccessful) {
                    inquirySyncDao.markAsSynchronized(inquiry.id)
                    syncedCount++
                } else {
                    inquirySyncDao.incrementRetryCount(inquiry.id)
                }
            } catch (e: Exception) {
                inquirySyncDao.incrementRetryCount(inquiry.id)
                return Result.failure(e)
            }
        }
        return Result.success(syncedCount)
    }
}
