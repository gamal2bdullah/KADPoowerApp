package com.kad.power.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "solar_products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val brand: String,
    val specsJson: String,
    val description: String,
    val descriptionAr: String,
    val isBookmarked: Boolean = false,
    val imageUrl: String
)

@Entity(tableName = "solar_calculations")
data class SolarCalculationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val totalDailyWattHours: Double,
    val pvCapacityWatts: Double,
    val batteryAh12v: Double,
    val batteryAh24v: Double,
    val batteryAh48v: Double,
    val inverterWatts: Double,
    val mpptChargeControllerAmps: Double,
    val estimatedPanelsCount: Int,
    val inputLoadsJson: String
)

@Entity(tableName = "inquiry_queue")
data class InquirySyncEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val city: String,
    val systemType: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynchronized: Boolean = false,
    val retryCount: Int = 0
)
