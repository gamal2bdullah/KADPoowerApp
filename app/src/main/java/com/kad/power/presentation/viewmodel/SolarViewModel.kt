package com.kad.power.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kad.power.data.local.entities.SolarCalculationEntity
import com.kad.power.domain.model.SolarCalculatorEngine
import com.kad.power.domain.repository.SolarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SolarViewModel @Inject constructor(
    private val repository: SolarRepository
) : ViewModel() {

    val productsState: StateFlow<List<com.kad.power.data.local.entities.ProductEntity>> = repository.getProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedProductsState: StateFlow<List<com.kad.power.data.local.entities.ProductEntity>> = repository.getBookmarkedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _pendingSyncCount = MutableStateFlow(0)
    val pendingSyncCount: StateFlow<Int> = _pendingSyncCount.asStateFlow()

    private val _syncInProgress = MutableStateFlow(false)
    val syncInProgress: StateFlow<Boolean> = _syncInProgress.asStateFlow()

    private val _currentLoads = MutableStateFlow<List<SolarCalculatorEngine.LoadInput>>(emptyList())
    val currentLoads: StateFlow<List<SolarCalculatorEngine.LoadInput>> = _currentLoads.asStateFlow()

    private val _calculationResult = MutableStateFlow<SolarCalculatorEngine.CalculationResult?>(null)
    val calculationResult: StateFlow<SolarCalculatorEngine.CalculationResult?> = _calculationResult.asStateFlow()

    init {
        refreshCatalog()
        updatePendingSyncStatus()
    }

    fun setOnlineStatus(online: Boolean) {
        _isOnline.value = online
        if (online) {
            triggerBackgroundSync()
        }
    }

    fun refreshCatalog() {
        viewModelScope.launch {
            repository.refreshProducts()
        }
    }

    fun toggleProductBookmark(productId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleProductBookmark(productId, isBookmarked)
        }
    }

    fun updatePendingSyncStatus() {
        viewModelScope.launch {
            _pendingSyncCount.value = repository.getPendingSyncCount()
        }
    }

    fun addLoadItem(appliance: String, watts: Double, quantity: Int, hours: Double) {
        val updated = _currentLoads.value + SolarCalculatorEngine.LoadInput(appliance, watts, quantity, hours)
        _currentLoads.value = updated
        recalculate()
    }

    fun removeLoadItem(index: Int) {
        val list = _currentLoads.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentLoads.value = list
            recalculate()
        }
    }

    fun clearLoads() {
        _currentLoads.value = emptyList()
        _calculationResult.value = null
    }

    private fun recalculate() {
        _calculationResult.value = SolarCalculatorEngine.calculateSystemSize(_currentLoads.value)
    }

    fun saveCurrentCalculation() {
        val result = _calculationResult.value ?: return
        viewModelScope.launch {
            val entity = SolarCalculationEntity(
                id = UUID.randomUUID().toString(),
                totalDailyWattHours = result.totalDailyWattHours,
                pvCapacityWatts = result.recommendedPvCapacityWatts,
                batteryAh12v = result.batteryBankAh12v,
                batteryAh24v = result.batteryBankAh24v,
                batteryAh48v = result.batteryBankAh48v,
                inverterWatts = result.recommendedInverterWatts,
                mpptChargeControllerAmps = result.chargeControllerAmps,
                estimatedPanelsCount = result.estimatedPanelsCount,
                inputLoadsJson = "saved_loads"
            )
            repository.saveCalculation(entity)
        }
    }

    fun submitInquiry(name: String, phone: String, city: String, system: String, notes: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.requestConsultation(name, phone, city, system, notes)
            updatePendingSyncStatus()
            onSuccess()
        }
    }

    fun triggerBackgroundSync() {
        if (_syncInProgress.value) return
        viewModelScope.launch {
            _syncInProgress.value = true
            repository.syncPendingInquiries()
            _syncInProgress.value = false
            updatePendingSyncStatus()
        }
    }
}
