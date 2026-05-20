package com.kad.power.domain.model

import kotlin.math.ceil

object SolarCalculatorEngine {

    private const val PEAK_SUN_HOURS_DEFAULT = 5.2
    private const val INVERTER_EFFICIENCY = 0.90
    private const val SYSTEM_LOSS_COEFFICIENT = 0.82
    private const val PANEL_CAPACITY_450W = 450.0
    private const val BATTERY_VOLTAGE_12V = 12.0

    data class LoadInput(
        val applianceName: String,
        val watts: Double,
        val quantity: Int,
        val hoursPerDay: Double
    )

    data class CalculationResult(
        val totalDailyWattHours: Double,
        val recommendedPvCapacityWatts: Double,
        val batteryBankAh12v: Double,
        val batteryBankAh24v: Double,
        val batteryBankAh48v: Double,
        val recommendedInverterWatts: Double,
        val recommendedInverterWattsSurge: Double,
        val chargeControllerAmps: Double,
        val estimatedPanelsCount: Int,
        val estimatedBatteriesCount: Int,
        val optimalTiltAngleDegrees: Double
    )

    fun calculateSystemSize(
        loads: List<LoadInput>,
        locationLatitude: Double = 31.96,
        batteryType: String = "GEL"
    ): CalculationResult {
        val totalDailyWattHours = loads.sumOf { it.watts * it.quantity * it.hoursPerDay }

        if (totalDailyWattHours <= 0) {
            return CalculationResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0)
        }

        val divisor = PEAK_SUN_HOURS_DEFAULT * SYSTEM_LOSS_COEFFICIENT * INVERTER_EFFICIENCY
        val recommendedPvCapacityWatts = totalDailyWattHours / divisor
        val estimatedPanelsCount = ceil(recommendedPvCapacityWatts / PANEL_CAPACITY_450W).toInt()

        val depthOfDischarge = if (batteryType.uppercase() == "LITHIUM") 0.80 else 0.50
        val requiredBatteryWattHours = totalDailyWattHours / depthOfDischarge

        val batteryAh12v = requiredBatteryWattHours / 12.0
        val batteryAh24v = requiredBatteryWattHours / 24.0
        val batteryAh48v = requiredBatteryWattHours / 48.0
        val estimatedBatteriesCount = ceil(batteryAh12v / 200.0).toInt()

        val maxSimultaneousWatts = loads.sumOf { it.watts * it.quantity }
        val recommendedInverterWatts = maxSimultaneousWatts * 1.25
        val recommendedInverterWattsSurge = maxSimultaneousWatts * 2.10

        val systemVoltage = if (recommendedPvCapacityWatts > 3000) 48.0 else if (recommendedPvCapacityWatts > 1500) 24.0 else 12.0
        val rawChargeControllerAmps = recommendedPvCapacityWatts / systemVoltage
        val chargeControllerAmps = ceil(rawChargeControllerAmps * 1.20)

        val optimalTiltAngleDegrees = locationLatitude * 0.90 + 2.5

        return CalculationResult(
            totalDailyWattHours = totalDailyWattHours,
            recommendedPvCapacityWatts = recommendedPvCapacityWatts,
            batteryBankAh12v = batteryAh12v,
            batteryBankAh24v = batteryAh24v,
            batteryBankAh48v = batteryAh48v,
            recommendedInverterWatts = recommendedInverterWatts,
            recommendedInverterWattsSurge = recommendedInverterWattsSurge,
            chargeControllerAmps = chargeControllerAmps,
            estimatedPanelsCount = estimatedPanelsCount,
            estimatedBatteriesCount = estimatedBatteriesCount,
            optimalTiltAngleDegrees = optimalTiltAngleDegrees
        )
    }
}
