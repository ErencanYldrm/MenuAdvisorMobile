package com.example.menuadvisor.model

data class SearchResult(
    val id: Int,
    val name: String,
    val type: String, // "cafe" or "product"
    val distance: Double,
    val rating: Double
)
