package com.kad.power.di

import android.content.Context
import androidx.room.Room
import com.kad.power.data.local.KADDatabase
import com.kad.power.data.local.dao.InquirySyncDao
import com.kad.power.data.local.dao.ProductDao
import com.kad.power.data.local.dao.SolarCalculationDao
import com.kad.power.data.remote.KADApiService
import com.kad.power.data.repository.SolarRepositoryImpl
import com.kad.power.domain.repository.SolarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SolarHiltModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KADDatabase {
        return Room.databaseBuilder(
            context,
            KADDatabase::class.java,
            "kad_alternative_energy.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProductDao(db: KADDatabase): ProductDao = db.productDao()

    @Provides
    fun provideSolarCalculationDao(db: KADDatabase): SolarCalculationDao = db.solarCalculationDao()

    @Provides
    fun provideInquirySyncDao(db: KADDatabase): InquirySyncDao = db.inquirySyncDao()

    @Provides
    @Singleton
    fun provideApiService(): KADApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.kad-power.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KADApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSolarRepository(
        productDao: ProductDao,
        calculationDao: SolarCalculationDao,
        inquirySyncDao: InquirySyncDao,
        apiService: KADApiService
    ): SolarRepository {
        return SolarRepositoryImpl(
            productDao,
            calculationDao,
            inquirySyncDao,
            apiService
        )
    }
}
