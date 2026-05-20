package com.kad.power.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kad.power.data.local.dao.*
import com.kad.power.data.local.entities.*

@Database(
    entities = [
        ProductEntity::class,
        SolarCalculationEntity::class,
        InquirySyncEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KADDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun solarCalculationDao(): SolarCalculationDao
    abstract fun inquirySyncDao(): InquirySyncDao
}
