package com.kad.power.data.remote

data class ProductNetworkModel(
    val id: String,
    val name: String,
    val category: String,
    val brand: String,
    val specsJson: String,
    val description: String,
    val descriptionAr: String,
    val imageUrl: String
)
